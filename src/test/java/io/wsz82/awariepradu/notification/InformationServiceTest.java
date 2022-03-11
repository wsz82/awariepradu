package io.wsz82.awariepradu.notification;

import io.wsz82.awariepradu.notification.InformationService;
import io.wsz82.awariepradu.database.Notification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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