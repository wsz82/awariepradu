package io.wsz82.awariepradu.model.phonebook;

import io.wsz82.awariepradu.DisplayKey;
import io.wsz82.awariepradu.PropertyKey;
import io.wsz82.awariepradu.model.contact.Contact;
import io.wsz82.awariepradu.model.contact.SubscriptionType;
import io.wsz82.awariepradu.model.information.InformationService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class PhonebookContactService {
    private static final int CODE_INVALID_PHONE_NUMBER = 400;
    private static final int CODE_PHONE_NUMBER_NOT_FOUND = 404;
    private static final int CODE_PHONE_NUMBER_ALREADY_EXISTS = 409;

    private final Logger logger = LoggerFactory.getLogger(InformationService.class);

    private final String oauthToken;
    private final HttpClient client;

    public PhonebookContactService() {
        oauthToken = System.getenv("SMS_TOKEN");
        client = HttpClient.newHttpClient();
    }

    public ContactResponse deleteContact(Contact contact) {
        String email = contact.getEmail();
        PhonebookContact phonebookContact = getContactByEmail(email);
        if (phonebookContact == null) {
            return new ContactResponse(ContactResponseType.FAIL_PRESENCE, DisplayKey.of("Contact.Delete.FailPresence"), null);
        }
        String id = phonebookContact.id();

        String body;
        try {
            HttpRequest.Builder delete = HttpRequest.newBuilder()
                    .DELETE();
            HttpResponse<String> response = getResponse(delete, "/" + id);
            body = response.body();
        } catch (Exception e) {
            e.printStackTrace();
            return new ContactResponse(ContactResponseType.INTERNAL_ERROR, DisplayKey.of("Contact.Request.ServerError"), e);
        }

        if (body != null && body.length() > 0) {
            logger.info("Delete response body: {}", body);
            JSONObject jsonObject = new JSONObject(body);
            Object code = jsonObject.get("code");
            if (code.equals(CODE_INVALID_PHONE_NUMBER)) {
                return new ContactResponse(ContactResponseType.FAIL_INVALID, DisplayKey.of("Contact.Delete.FailInvalid"), null);
            } else if (code.equals(CODE_PHONE_NUMBER_NOT_FOUND)) {
                return new ContactResponse(ContactResponseType.FAIL_PRESENCE, DisplayKey.of("Contact.Delete.FailPresence"), null);
            }
        }

        return new ContactResponse(ContactResponseType.SUCCESS, DisplayKey.of("Contact.Delete.Success"), null);
    }

    public boolean doContactExist(String email) {
        return getContactByEmail(email) != null;
    }

    public PhonebookContact getContactByEmail(String email) {
        return getPhonebookContact(email, "email");
    }

    private PhonebookContact getPhonebookContact(String param, String paramName) {
        String getBody;
        try {
            HttpRequest.Builder get = HttpRequest.newBuilder()
                    .GET();
            HttpResponse<String> response = getResponse(get, "/" + "?" + paramName + "=" + param);
            getBody = response.body();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        JSONObject jsonObject = new JSONObject(getBody);
        JSONArray contacts = jsonObject.getJSONArray("collection");
        if (contacts.length() == 0) return null;
        JSONObject contactJson = contacts.getJSONObject(0);
        if (contactJson == null) return null;

        String id = contactJson.getString("id");
        String email = contactJson.getString("email");
        String phoneNumber = "";
        try {
            phoneNumber = contactJson.getString("phone_number");
        } catch (JSONException ignore) {}
        int subscriptionCode = Integer.parseInt(contactJson.getString(DisplayKey.of("Request.Param.SubscriptionType")));
        SubscriptionType subscriptionType = SubscriptionType.values()[subscriptionCode];
        return new PhonebookContact(id, phoneNumber, email, subscriptionType);
    }

    private HttpResponse<String> getResponse(HttpRequest.Builder builder, String afterUri) throws Exception {
        URI uri = new URI(PropertyKey.of("proxy.url.contacts") + afterUri);

        HttpRequest request = builder
                .setHeader("Authorization", " Bearer " + oauthToken)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .uri(uri)
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public ContactResponse addContact(Contact contact) {
        String groupName = makeGroupName(contact);
        Map<Object, Object> params = new HashMap<>(4);
        String phoneNumber = contact.getPhoneNumber();
        phoneNumber = phoneNumber != null ? phoneNumber : "";
        params.put("phone_number", phoneNumber);
        params.put("email", contact.getEmail());
        params.put(DisplayKey.of("Request.Param.Location"), groupName);
        params.put(DisplayKey.of("Request.Param.SubscriptionType"), contact.getSubscriptionType());

        URI uri;
        try {
            uri = new URI(PropertyKey.of("proxy.url.contacts"));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return new ContactResponse(ContactResponseType.INTERNAL_ERROR, DisplayKey.of("Contact.Request.ServerError"), e);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .setHeader("Authorization", " Bearer " + oauthToken)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(buildFormDataFromMap(params))
                .uri(uri)
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            return new ContactResponse(ContactResponseType.INTERNAL_ERROR, DisplayKey.of("Contact.Request.ServerError"), e);
        }

        String body = response.body();

        JSONObject jsonObject = new JSONObject(body);
        try {
            Object code = jsonObject.get("code");
            if (code.equals(CODE_INVALID_PHONE_NUMBER)) {
                return new ContactResponse(ContactResponseType.FAIL_INVALID, DisplayKey.of("Contact.Add.FailInvalid"), null);
            } else if (code.equals(CODE_PHONE_NUMBER_ALREADY_EXISTS)) {
                return new ContactResponse(ContactResponseType.FAIL_PRESENCE, DisplayKey.of("Contact.Add.FailPresence"), null);
            }
        } catch (JSONException ignore) {
        }
        logger.info("Post response body: {}", body);

        return new ContactResponse(ContactResponseType.SUCCESS, DisplayKey.of("Contact.Add.Success"), null);
    }

    private String makeGroupName(Contact contact) {
        Boolean isCity = contact.getIsCity();
        String groupName = contact.getRegion() + " - " + contact.getLocation();
        if (isCity) {
            groupName += " - miasto";
        }
        return groupName;
    }

    private HttpRequest.BodyPublisher buildFormDataFromMap(Map<Object, Object> data) {
        var builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }
}
