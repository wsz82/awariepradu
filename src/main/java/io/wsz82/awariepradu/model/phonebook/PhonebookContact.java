package io.wsz82.awariepradu.model.phonebook;

import io.wsz82.awariepradu.model.contact.SubscriptionType;

public record PhonebookContact(String id, String phoneNumber, String email, SubscriptionType subscriptionType) {
}
