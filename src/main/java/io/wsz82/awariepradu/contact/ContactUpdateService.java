package io.wsz82.awariepradu.contact;

import io.wsz82.awariepradu.DisplayKey;
import io.wsz82.awariepradu.Misc;
import io.wsz82.awariepradu.notification.InformationService;
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

public class ContactUpdateService {
    private static final int CODE_INVALID_PHONE_NUMBER = 400;
    private static final int CODE_PHONE_NUMBER_NOT_FOUND = 404;
    private static final int CODE_PHONE_NUMBER_ALREADY_EXISTS = 409;

    private final Logger logger = LoggerFactory.getLogger(InformationService.class);

    private final String oauthToken;
    private final HttpClient client;

    public ContactUpdateService() {
        oauthToken = System.getenv("SMS_TOKEN");
        client = HttpClient.newHttpClient();
    }

    public ContactResponse deleteContact(ContactUnsubscribe contact) {
        String id = getId(contact);

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

    private String getId(ContactUnsubscribe contact) {
        Map<String, String> phoneNumberIdMap = getPhoneNumberIdMap();
        if (phoneNumberIdMap == null) {
            return null;
        }
        String key = contact.getPhoneNumber();
        return phoneNumberIdMap.get(key);
    }

    private Map<String, String> getPhoneNumberIdMap() {
        String getBody;
        try {
            HttpRequest.Builder get = HttpRequest.newBuilder();
            HttpResponse<String> response = getResponse(get, "");
            getBody = response.body();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        JSONObject jsonObject = new JSONObject(getBody);
        JSONArray contacts = jsonObject.getJSONArray("collection");
        int length = contacts.length();
        Map<String, String> phoneNumberIdMap = new HashMap<>(length);
        for (int i = 0; i < length; i++) {
            JSONObject contact = contacts.getJSONObject(i);
            String phoneNumber = contact.getString("phone_number");
            String id = contact.getString("id");
            phoneNumberIdMap.put(phoneNumber, id);
        }
        return phoneNumberIdMap;
    }

    private HttpResponse<String> getResponse(HttpRequest.Builder builder, String afterUri) throws Exception {
        URI uri = new URI(Misc.PROXY_CONTACTS_URL + afterUri);

        HttpRequest request = builder
                .setHeader("Authorization", " Bearer " + oauthToken)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .uri(uri)
                .build();

        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public ContactResponse addContact(Contact contact) {
        String groupName = Misc.makeGroupName(contact.getRegion(), contact.getLocation(), contact.getIsCity());
        Map<Object, Object> params = new HashMap<>(2);
        params.put("phone_number", contact.getPhoneNumber());
        params.put("location", groupName);

        URI uri;
        try {
            uri = new URI(Misc.PROXY_CONTACTS_URL);
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
        } catch (JSONException ignore) {}
        logger.info("Post response body: {}", body);

        return new ContactResponse(ContactResponseType.SUCCESS, DisplayKey.of("Contact.Add.Success"), null);
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
