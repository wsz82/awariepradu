package io.wsz82.awariepradu;

import io.wsz82.awariepradu.notification.InformationService;
import io.wsz82.awariepradu.notification.NotificationService;
import io.wsz82.awariepradu.notification.UpdateCheckService;
import io.wsz82.awariepradu.database.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ScheduledUpdateTask {
    private final Logger logger = LoggerFactory.getLogger(ScheduledUpdateTask.class);

    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private AreaNotificationRepository areaNotificationRepository;

    @Autowired
    private SingleValuesRepository singleValuesRepository;

    @Scheduled(fixedRate = 300000) //5 min
    public void runUpdate() {
        boolean databaseUpToDate = isDatabaseUpToDate();
        if (databaseUpToDate) {
            return;
        }

        InformationService informationService = appContext.getBean(InformationService.class);
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

    private void notifyGroups(List<Notification> lastNotifications, Iterable<Notification> previousNotifications) throws Exception {
        NotificationService sender = appContext.getBean(NotificationService.class);
        sender.refreshContacts();

        for (Notification actual : lastNotifications) {
            final boolean[] canNotify = new boolean[] {true};

            previousNotifications.forEach(previous -> {
                if (previous.isTheSameArea(actual)) {
                    if (previous.getAreaMessage().equals(actual.getAreaMessage()) &&
                            previous.getWarningPeriod().equals(actual.getWarningPeriod())) {
                        canNotify[0] = false;
                    }
                }
            });

            if (canNotify[0]) {
                String message = actual.getAreaMessage() + " \n" + "Czas: " + actual.getWarningPeriod();
                String location = actual.getRegionName() + " - " + actual.getAreaName();
                boolean isSent = sender.sendToLocation(location, message);
                if (isSent) {
                    logger.info("Notified location: " + location);
                }
            }
        }
    }

    private boolean isDatabaseUpToDate() {
        int id = SingleValueNumber.LAST_CHECKED_LENGTH.getId();
        Optional<SingleValue> lastCheckedLengthOpt = singleValuesRepository.findById(id);

        if (lastCheckedLengthOpt.isEmpty()) {
            logger.info("Failed to receive last content length from database");
            return false;
        }

        String lastUpdateLength = lastCheckedLengthOpt.get().value;
        UpdateCheckService updateCheckService = appContext.getBean(UpdateCheckService.class);
        updateCheckService.setLastUpdateLength(lastUpdateLength);
        boolean upToDate = updateCheckService.isUpToDate();
        logger.info(upToDate ? "The database is up to date" : "The database isn't up to date");

        return upToDate;
    }
}
