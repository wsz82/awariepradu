package io.wsz82.awariepradu.contact;

import io.wsz82.awariepradu.Misc;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class ContactUpdateServiceTest {

    private static Contact contact;
    private static ContactUpdateService contactUpdateService;

    @BeforeAll
    static void beforeAll() throws Exception {
        contact = new Contact("48693843898", "Gdańsk", "Testowa gmina", true);
        contactUpdateService = new ContactUpdateService();
    }

    @Test
    void addContact() {
        ContactResponse contactDeleteResponse = contactUpdateService.deleteContact(new ContactUnsubscribe(contact.getPhoneNumber()));
        ContactResponseType typeDelete = contactDeleteResponse.type();
        assertTrue(typeDelete == ContactResponseType.SUCCESS || typeDelete == ContactResponseType.FAIL_PRESENCE);

        ContactResponse contactAddResponse = contactUpdateService.addContact(contact);
        ContactResponseType typeAdd = contactAddResponse.type();
        System.out.println(typeAdd);
        assertTrue(typeAdd == ContactResponseType.SUCCESS || typeAdd == ContactResponseType.FAIL_PRESENCE);
    }

    @Test
    void deleteContact() {
        ContactResponse contactDeleteResponse = contactUpdateService.deleteContact(new ContactUnsubscribe(contact.getPhoneNumber()));
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
                    .uri(new URI(Misc.PROXY_CONTACTS_URL))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String body = response.body();
            System.out.println(body);
        });
    }

}