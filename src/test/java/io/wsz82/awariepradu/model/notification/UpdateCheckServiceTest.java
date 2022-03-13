package io.wsz82.awariepradu.model.notification;

import io.wsz82.awariepradu.model.information.InformationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

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