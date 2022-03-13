package io.wsz82.awariepradu.model.email;

import io.wsz82.awariepradu.PropertyKey;
import io.wsz82.awariepradu.model.contact.Contact;
import io.wsz82.awariepradu.model.contact.ContactRepository;
import io.wsz82.awariepradu.model.phonebook.PhonebookContact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class UnsubscribeListener implements ApplicationListener<OnUnsubscribeCompleteEvent> {

    @Autowired
    VerificationTokenRepository verificationTokenRepository;

    @Autowired
    ContactRepository contactRepository;

    @Autowired
    EmailService emailService;

    @Override
    public void onApplicationEvent(OnUnsubscribeCompleteEvent event) {
        this.confirmSubscribe(event);
    }

    @Transactional
    private void confirmSubscribe(OnUnsubscribeCompleteEvent event) {
        PhonebookContact contact = event.getContact();
        String token = UUID.randomUUID().toString();

        Contact entity = new Contact(contact.email(), contact.phoneNumber(), "N/A", "N/A", false, -1);
        contactRepository.save(entity);
        VerificationToken verificationToken = new VerificationToken(token, entity);
        verificationTokenRepository.save(verificationToken);

        String confirmationUrl = PropertyKey.of("contacts.unsubscribe.url.token.confirm", event.getAppUrl(), token);
        try {
            emailService.sendUnsubscribeEmail(entity, confirmationUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}