package io.wsz82.awariepradu.model.information;

import io.wsz82.awariepradu.model.notification.Notification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InformationServiceTest {

    private static InformationService informationService;

    @BeforeAll
    static void beforeAll() {
        informationService = new InformationService();
        informationService.refreshNotifications();
    }

    @Test
    void notificationsAreNotNull() {
        List<Notification> notifications = informationService.getNotifications();

        assertNotNull(notifications);
    }

    @Test
    void contentLengthIsReceived() {
        String contentLengthStr = informationService.getContentLength();
        long contentLength = Long.parseLong(contentLengthStr);

        assertTrue(contentLength > 0);
    }
}