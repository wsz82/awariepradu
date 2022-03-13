package io.wsz82.awariepradu.model.email;

import io.wsz82.awariepradu.model.phonebook.PhonebookContact;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class OnUnsubscribeCompleteEvent extends ApplicationEvent {
    private String appUrl;
    private PhonebookContact contact;

    public OnUnsubscribeCompleteEvent(PhonebookContact contact, String appUrl) {
        super(contact);
        this.contact = contact;
        this.appUrl = appUrl;
    }
}
