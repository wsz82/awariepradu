package io.wsz82.awariepradu.model.email;

import io.wsz82.awariepradu.model.contact.Contact;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class OnSubscribeCompleteEvent extends ApplicationEvent {
    private String appUrl;
    private Contact contact;

    public OnSubscribeCompleteEvent(Contact contact, String appUrl) {
        super(contact);
        this.contact = contact;
        this.appUrl = appUrl;
    }
}
