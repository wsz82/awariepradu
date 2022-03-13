package io.wsz82.awariepradu;

import io.wsz82.awariepradu.model.contact.Contact;
import io.wsz82.awariepradu.model.contact.SubscriptionType;

public class TestUtils {

    public static Contact makeTestContact() {
        Contact contact = new Contact();
        contact.setPhoneNumber("48666777888");
        contact.setEmail("awariepradu@gmail.com");
        contact.setRegion("Testowy region");
        contact.setLocation("Testowa gmina");
        contact.setIsCity(true);
        contact.setSubscriptionType(SubscriptionType.ALL.ordinal());
        return contact;
    }
}
