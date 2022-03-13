package io.wsz82.awariepradu.model.email;

import io.wsz82.awariepradu.TestUtils;
import io.wsz82.awariepradu.model.contact.Contact;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmailServiceTest {

    @Autowired
    EmailService emailServiceTest;

    @Test
    public void sendMail() {
        Contact contact = TestUtils.makeTestContact();
        assertDoesNotThrow(() -> {
            emailServiceTest.sendSubscribeEmail(contact, "");
        });
    }
    @TestConfiguration
    static class TestConfig {

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
