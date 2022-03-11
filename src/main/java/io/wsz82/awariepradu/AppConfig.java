package io.wsz82.awariepradu;

import io.wsz82.awariepradu.contact.ContactUpdateService;
import io.wsz82.awariepradu.notification.InformationService;
import io.wsz82.awariepradu.notification.NotificationService;
import io.wsz82.awariepradu.notification.UpdateCheckService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public NotificationService notificationService() throws Exception {
        return new NotificationService();
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
    public ContactUpdateService contactUpdaterService() {
        return new ContactUpdateService();
    }
}
