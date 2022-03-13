package io.wsz82.awariepradu;

import io.wsz82.awariepradu.model.email.EmailService;
import io.wsz82.awariepradu.model.information.InformationService;
import io.wsz82.awariepradu.model.notification.AreaNotificationRepository;
import io.wsz82.awariepradu.model.notification.Notification;
import io.wsz82.awariepradu.model.notification.NotificationService;
import io.wsz82.awariepradu.model.notification.UpdateCheckService;
import io.wsz82.awariepradu.model.phonebook.PhonebookContact;
import io.wsz82.awariepradu.model.singlevalue.SingleValue;
import io.wsz82.awariepradu.model.singlevalue.SingleValueNumber;
import io.wsz82.awariepradu.model.singlevalue.SingleValuesRepository;
import io.wsz82.awariepradu.model.sms.SmsService;
import io.wsz82.awariepradu.view.mail.NotificationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class ScheduledUpdateTask {
    private final Logger logger = LoggerFactory.getLogger(ScheduledUpdateTask.class);

    @Autowired
    SpringTemplateEngine templateEngine;

    @Autowired
    InformationService informationService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    UpdateCheckService updateCheckService;

    @Autowired
    AreaNotificationRepository areaNotificationRepository;

    @Autowired
    SingleValuesRepository singleValuesRepository;

    @Autowired
    SmsService smsService;

    @Autowired
    EmailService emailService;

    @Scheduled(fixedRate = 5, timeUnit = TimeUnit.MINUTES)
    public void runUpdate() {
        boolean databaseUpToDate = isDatabaseUpToDate();
        if (databaseUpToDate) {
            return;
        }

        informationService.refreshNotifications();
        List<Notification> lastNotifications = informationService.getNotifications();

        Iterable<Notification> previousNotifications = areaNotificationRepository.findAll();

        try {
            notifyGroups(lastNotifications, previousNotifications);
        } catch (Exception e) {
            e.printStackTrace();
        }

        areaNotificationRepository.saveAll(lastNotifications);

        String contentLength = informationService.getContentLength();
        SingleValue contentLengthValue = new SingleValue(SingleValueNumber.LAST_CHECKED_LENGTH.getId(), contentLength);
        singleValuesRepository.save(contentLengthValue);
    }

    private void notifyGroups(List<Notification> lastNotifications, Iterable<Notification> previousNotifications) {
        Map<String, List<PhonebookContact>> contactGroups = notificationService.makeContactGroups();

        for (Notification actual : lastNotifications) {
            boolean canNotify = canNotify(previousNotifications, actual);

            String location = actual.getRegionName() + " - " + actual.getAreaName();

            List<PhonebookContact> contacts = contactGroups.get(location);
            if (contacts == null) {
                logger.info("Location not found in contacts: " + location);
                continue;
            }

            if (canNotify) {
                String message = makeNotificationMessage(location, actual.getAreaMessage(), actual.getWarningPeriod());
                boolean isSent = notificationService.sendToContacts(emailService, smsService, contacts, message);
                if (isSent) {
                    logger.info("Notified location: " + location);
                }
            }
        }
    }

    private boolean canNotify(Iterable<Notification> previousNotifications, Notification actual) {
        for (Notification previous : previousNotifications) {
            if (previous.isTheSameArea(actual)) {
                if (previous.getAreaMessage().equals(actual.getAreaMessage()) &&
                        previous.getWarningPeriod().equals(actual.getWarningPeriod())) {
                    return false;
                }
            }
        }
        return true;
    }

    private String makeNotificationMessage(String location, String message, String time) {
        Context thymeleafContext = new Context();
        NotificationDTO notificationDTO = new NotificationDTO(location, message, time);
        Map<String, Object> variables = notificationDTO.getVariables();
        thymeleafContext.setVariables(variables);
        return templateEngine.process("notification.html", thymeleafContext);
    }

    private boolean isDatabaseUpToDate() {
        int id = SingleValueNumber.LAST_CHECKED_LENGTH.getId();
        Optional<SingleValue> lastCheckedLengthOpt = singleValuesRepository.findById(id);

        if (lastCheckedLengthOpt.isEmpty()) {
            logger.info("Failed to receive last content length from database");
            return false;
        }

        String lastUpdateLength = lastCheckedLengthOpt.get().value;
        updateCheckService.setLastUpdateLength(lastUpdateLength);
        boolean upToDate = updateCheckService.isUpToDate();
        logger.info(upToDate ? "The database is up to date" : "The database isn't up to date");

        return upToDate;
    }
}
