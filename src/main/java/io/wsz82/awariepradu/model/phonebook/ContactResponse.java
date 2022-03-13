package io.wsz82.awariepradu.model.phonebook;

public record ContactResponse(ContactResponseType type, String message, Exception exception) {
}
