package io.wsz82.awariepradu.model.email;

import io.wsz82.awariepradu.PropertyKey;
import io.wsz82.awariepradu.model.contact.Contact;
import io.wsz82.awariepradu.model.contact.ContactRepository;
import io.wsz82.awariepradu.model.contact.SubscriptionType;
import io.wsz82.awariepradu.model.sms.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class SubscribeListener implements ApplicationListener<OnSubscribeCompleteEvent> {

    @Autowired
    VerificationTokenRepository verificationTokenRepository;

    @Autowired
    ContactRepository contactRepository;

    @Autowired
    EmailService emailService;

    @Autowired
    SmsService smsService;

    @Override
    public void onApplicationEvent(OnSubscribeCompleteEvent event) {
        this.confirmSubscribe(event);
    }

    @Transactional
    private void confirmSubscribe(OnSubscribeCompleteEvent event) {
        Contact contact = event.getContact();
        String token = UUID.randomUUID().toString();

        contactRepository.save(contact);
        VerificationToken verificationToken = new VerificationToken(token, contact);
        verificationTokenRepository.save(verificationToken);

        String confirmationUrl = PropertyKey.of("contacts.subscribe.url.token.confirm", event.getAppUrl(), token);
        try {
            SubscriptionType subscriptionType = contact.parsedSubscriptionType();
            if (subscriptionType == SubscriptionType.EMAIL) {
                emailService.sendSubscribeEmail(contact, confirmationUrl);
            } else {
                smsService.sendSubscribeSms(contact.getPhoneNumber(), confirmationUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}