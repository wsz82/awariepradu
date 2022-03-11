package io.wsz82.awariepradu.notification;

import io.wsz82.awariepradu.Misc;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.smsapi.OAuthClient;
import pl.smsapi.api.SmsFactory;
import pl.smsapi.api.action.sms.SMSSend;
import pl.smsapi.api.response.MessageResponse;
import pl.smsapi.api.response.StatusResponse;
import pl.smsapi.exception.SmsapiException;
import pl.smsapi.proxy.ProxyNative;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class NotificationService {
    private static final String SENDER_NAME = "AwariePradu";

    private final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final SmsFactory smsApi;
    private final String oauthToken = System.getenv("SMS_TOKEN");
    private Map<String, List<String>> locationWithPhoneNumbersMap;
    private Exception exception;

    public NotificationService() throws Exception {
        OAuthClient client = new OAuthClient(oauthToken);
        ProxyNative proxy = new ProxyNative(Misc.PROXY_URL);
        this.smsApi = new SmsFactory(client, proxy);
    }

    public void refreshContacts() {
        this.locationWithPhoneNumbersMap = makeLocationWithPhoneNumbersMap(oauthToken);
        if (locationWithPhoneNumbersMap == null) {
            throw new NullPointerException("Location with phone numbers map is null (no contact with sms-api)");
        }
    }

    private Map<String, List<String>> makeLocationWithPhoneNumbersMap(String oauthToken) {
        String getBody;
        try {
            HttpRequest.Builder get = HttpRequest.newBuilder();
            URI uri = new URI(Misc.PROXY_CONTACTS_URL + "");

            HttpRequest request = get
                    .setHeader("Authorization", " Bearer " + oauthToken)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .uri(uri)
                    .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            getBody = response.body();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        JSONObject jsonObject = new JSONObject(getBody);
        JSONArray contacts = jsonObject.getJSONArray("collection");
        int length = contacts.length();
        Map<String, List<String>> locationPhoneNumbersMap = new HashMap<>(length);
        for (int i = 0; i < length; i++) {
            JSONObject contact = contacts.getJSONObject(i);
            String phoneNumber = contact.getString("phone_number");
            String locationsText = contact.getString("location");
            String[] locations = locationsText.split(";");

            for (String location : locations) {
                List<String> phoneNumbers = locationPhoneNumbersMap.get(location);
                if (phoneNumbers != null) {
                    phoneNumbers.add(phoneNumber);
                } else {
                    phoneNumbers = new ArrayList<>(1);
                    phoneNumbers.add(phoneNumber);
                    locationPhoneNumbersMap.put(location, phoneNumbers);
                }
            }

        }
        return locationPhoneNumbersMap;
    }

    public boolean sendToLocation(String location, String message) {
        List<String> phoneNumbers = locationWithPhoneNumbersMap.get(location);
        if (phoneNumbers == null) {
            logger.info("Location not found in contacts: " + location);
            return false;
        }
        for (String phoneNumber : phoneNumbers) {
            sendToPhone(message, phoneNumber);
        }
        return true;
    }

    private void sendToPhone(String message, String phoneNumber) {
        exception = null;
        boolean test = isTest();
        SMSSend action = smsApi.actionSend()
                .setTo(phoneNumber)
                .setText(message)
                .setTest(test);
        if (!test) {
            action.setSender(SENDER_NAME);
        }
        try {
            StatusResponse result = action.execute();

            Optional<MessageResponse> status = result.getList().stream().findFirst();
            if (status.isEmpty()) {
                throw new RuntimeException();
            }

            MessageResponse messageResponse = status.get();
            logger.debug("Phone number: " + messageResponse.getNumber());
            logger.debug("Shipment id: " + messageResponse.getId());
            logger.debug("Shipment status: " + messageResponse.getStatus());
            logger.debug("Points: " + messageResponse.getPoints());
            logger.error("Error: " + messageResponse.getError());
        } catch (SmsapiException e) {
            exception = e;
            logger.error("Exception: " + e);
        }
    }

    protected boolean isTest() {
        return false;
    }

    public Exception getException() {
        return exception;
    }
}
