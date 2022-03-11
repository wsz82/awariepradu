package io.wsz82.awariepradu.notification;

import io.wsz82.awariepradu.notification.InformationService;
import io.wsz82.awariepradu.notification.UpdateCheckService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdateCheckServiceTest {

    @Test
    void isUpToDate() {
        InformationService informationService = new InformationService();
        informationService.refreshNotifications();
        String contentLength = informationService.getContentLength();
        UpdateCheckService updateCheckService = new UpdateCheckService();
        updateCheckService.setLastUpdateLength(contentLength);
        boolean upToDate = updateCheckService.isUpToDate();

        assertTrue(upToDate);
    }
}