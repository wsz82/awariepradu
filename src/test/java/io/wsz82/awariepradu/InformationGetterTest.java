package io.wsz82.awariepradu;

import io.wsz82.awariepradu.contact.InformationGetter;
import io.wsz82.awariepradu.database.Notification;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InformationGetterTest {

    private static InformationGetter informationGetter;

    @BeforeAll
    static void beforeAll() {
        informationGetter = new InformationGetter();
    }

    @Test
    void notificationsAreNotNull() {
        List<Notification> notifications = informationGetter.getNotifications();

        assertNotNull(notifications);
    }

    @Test
    void contentLengthIsReceived() {
        String contentLengthStr = informationGetter.getContentLength();
        long contentLength = Long.parseLong(contentLengthStr);

        assertTrue(contentLength > 0);
    }
}