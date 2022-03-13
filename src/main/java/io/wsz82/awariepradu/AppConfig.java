package io.wsz82.awariepradu;

import io.wsz82.awariepradu.model.email.EmailService;
import io.wsz82.awariepradu.model.information.InformationService;
import io.wsz82.awariepradu.model.notification.NotificationService;
import io.wsz82.awariepradu.model.notification.UpdateCheckService;
import io.wsz82.awariepradu.model.phonebook.PhonebookContactService;
import io.wsz82.awariepradu.model.sms.SmsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class AppConfig {

    @Bean
    public NotificationService notificationService() {
        return new NotificationService();
    }

    @Bean
    public SmsService smsService() throws Exception {
        return new SmsService();
    }

    @Bean
    public EmailService emailService() {
        return new EmailService();
    }

    @Bean
    public InformationService informationService() {
        return new InformationService();
    }

    @Bean
    public UpdateCheckService updateCheckService() {
        return new UpdateCheckService();
    }

    @Bean
    public PhonebookContactService contactUpdaterService() {
        return new PhonebookContactService();
    }

    @Bean
    public JavaMailSender javaMailSender() {
        return Config.javaMailSender();
    }

}
