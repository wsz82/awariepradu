package io.wsz82.awariepradu.model.notification;

import io.wsz82.awariepradu.model.email.EmailService;
import io.wsz82.awariepradu.model.phonebook.PhonebookContact;
import io.wsz82.awariepradu.model.sms.SmsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NotificationServiceTest {
    private final String location = "Testowy region - Testowa gmina";
    private final String message =
                    """
                    Uwaga, uwaga!
                    To tylko ćwiczenia.
                    """;

    @Autowired
    NotificationService notificationService;

    @Autowired
    SmsService smsServiceTest;

    @Autowired
    EmailService emailServiceTest;

    @Test
    public void sendToGroup() {
        Map<String, List<PhonebookContact>> contactGroups = notificationService.makeContactGroups();
        List<PhonebookContact> contacts = contactGroups.get(location);

        boolean isSent = notificationService.sendToContacts(emailServiceTest, smsServiceTest, contacts, message);
        assertTrue(isSent);
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public SmsService smsServiceTest() throws Exception {
            return new SmsService() {
                @Override
                protected boolean isTest() {
                    return true;
                }
            };
        }

        @Bean
        public EmailService emailServiceTest() {
            return new EmailService() {
                @Override
                protected boolean isTest() {
                    return true;
                }
            };
        }

    }
}