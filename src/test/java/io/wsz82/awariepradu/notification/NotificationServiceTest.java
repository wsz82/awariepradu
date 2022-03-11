package io.wsz82.awariepradu.notification;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class NotificationServiceTest {
    private final String location = "Testowy region - Testowa gmina";
    private final String message = """
            Uwaga, uwaga!
            To tylko ćwiczenia.
            """;

    @Test
    void sendToGroup() throws Exception {
        NotificationService sender = new NotificationService() {
            @Override
            protected boolean isTest() {
                return true;
            }
        };

        sender.refreshContacts();
        boolean isSent = sender.sendToLocation(location, message);
        assertTrue(isSent);

//        Exception exception = sender.getException();
//        assertNull(exception);
    }
}