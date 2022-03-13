package io.wsz82.awariepradu.model.phonebook;

import io.wsz82.awariepradu.PropertyKey;
import io.wsz82.awariepradu.TestUtils;
import io.wsz82.awariepradu.model.contact.Contact;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class PhonebookContactServiceTest {

    private static Contact contact;
    private static PhonebookContactService phonebookContactService;

    @BeforeAll
    static void beforeAll() {
        contact = TestUtils.makeTestContact();
        phonebookContactService = new PhonebookContactService();
    }

    @Test
    void addContact() {
        phonebookContactService.deleteContact(contact);

        ContactResponse contactAddResponse = phonebookContactService.addContact(contact);
        ContactResponseType typeAdd = contactAddResponse.type();
        System.out.println(typeAdd);
        assertTrue(typeAdd == ContactResponseType.SUCCESS || typeAdd == ContactResponseType.FAIL_PRESENCE);
    }

    @Test
    void deleteContact() {
        phonebookContactService.addContact(contact);

        ContactResponse contactDeleteResponse = phonebookContactService.deleteContact(contact);
        ContactResponseType type = contactDeleteResponse.type();
        System.out.println(type);
        assertTrue(type == ContactResponseType.SUCCESS || type == ContactResponseType.FAIL_PRESENCE);
    }

    @Test
    void getContacts() {
        assertDoesNotThrow(() -> {
            String oauthToken = System.getenv("SMS_TOKEN");

            HttpRequest request = HttpRequest.newBuilder()
                    .setHeader("Authorization", " Bearer " + oauthToken)
                    .uri(new URI(PropertyKey.of("proxy.url.contacts")))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String body = response.body();
            System.out.println(body);
        });
    }

    @Test
    void getPhonebookContactByEmail() {
        phonebookContactService.addContact(contact);
        PhonebookContact phonebookContact = phonebookContactService.getContactByEmail(contact.getEmail());
        phonebookContactService.deleteContact(contact);
        assertNotNull(phonebookContact);
    }
}