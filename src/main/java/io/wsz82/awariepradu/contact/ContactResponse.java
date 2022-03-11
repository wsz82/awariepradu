package io.wsz82.awariepradu.contact;

public record ContactResponse(ContactResponseType type, String message, Exception exception) {
}
