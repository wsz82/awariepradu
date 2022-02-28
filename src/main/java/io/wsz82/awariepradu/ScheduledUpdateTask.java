package io.wsz82.awariepradu;

import io.wsz82.awariepradu.contact.InformationGetter;
import io.wsz82.awariepradu.contact.NotificationSender;
import io.wsz82.awariepradu.contact.UpdateChecker;
import io.wsz82.awariepradu.database.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ScheduledUpdateTask {

    @Autowired
    private AreaNotificationRepository areaNotificationRepository;

    @Autowired
    private SingleValuesRepository singleValuesRepository;

    @Scheduled(fixedRate = 300000) //5 min
    public void runUpdate() {
        if (isDatabaseUpToDate()) {
            return;
        }

        InformationGetter informationGetter = new InformationGetter();
        List<Notification> lastNotifications = informationGetter.getNotifications();
        String contentLength = informationGetter.getContentLength();
        SingleValue contentLengthValue = new SingleValue(SingleValueNumber.LAST_CHECKED_LENGTH.getId(), contentLength);
        singleValuesRepository.save(contentLengthValue);

        Iterable<Notification> previousNotifications = areaNotificationRepository.findAll();

        try {
            notifyGroups(lastNotifications, previousNotifications);
        } catch (Exception e) {
            e.printStackTrace();
        }

        areaNotificationRepository.saveAll(lastNotifications);
    }

    private void notifyGroups(List<Notification> lastNotifications, Iterable<Notification> previousNotifications) throws Exception {
        NotificationSender sender = new NotificationSender(false);

        for (Notification actual : lastNotifications) {
            final boolean[] canNotify = new boolean[] {true};

            previousNotifications.forEach(previous -> {
                if (previous.isTheSameArea(actual)) {
                    if (previous.areaMessage.equals(actual.areaMessage) &&
                            previous.warningPeriod.equals(actual.warningPeriod)) {
                        canNotify[0] = false;
                    }
                }
            });

            if (canNotify[0]) {
                String message = actual.areaMessage + " \n" + "Czas: " + actual.warningPeriod;
                String groupName = actual.regionName + " - " + actual.areaName;
                sender.sendToGroup(groupName, message);
                System.out.println("Notified group: " + groupName);
            }
        }
    }

    private boolean isDatabaseUpToDate() {
        Optional<SingleValue> lastCheckedLengthOpt = singleValuesRepository.findById(SingleValueNumber.LAST_CHECKED_LENGTH.getId());

        boolean upToDate;
        if (lastCheckedLengthOpt.isPresent()) {
            String lastUpdateLength = lastCheckedLengthOpt.get().value;
            UpdateChecker updateChecker = new UpdateChecker(lastUpdateLength);
            upToDate = updateChecker.isUpToDate();
        } else {
            upToDate = false;
        }
        System.out.println(upToDate ? "The database is up to date" : "The database isn't up to date");

        return upToDate;
    }
}
