package io.wsz82.awariepradu;

import io.wsz82.awariepradu.contact.NotificationSender;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationSenderTest {

    private final String testGroup = "Testowy region - Testowa gmina";
    private final String testMessage = """
            Uwaga, uwaga!
            To tylko ćwiczenia.
            """;

    @Test
    void sendToGroup() throws Exception {
        NotificationSender sender = new NotificationSender(true);
        sender.sendToGroup(testGroup, testMessage);
        String exception = sender.getExceptionMessage();

        assertNull(exception);
    }
}