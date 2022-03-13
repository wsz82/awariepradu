package io.wsz82.awariepradu.model.notification;

import io.wsz82.awariepradu.DisplayKey;
import io.wsz82.awariepradu.PropertyKey;
import io.wsz82.awariepradu.model.contact.SubscriptionType;
import io.wsz82.awariepradu.model.email.EmailService;
import io.wsz82.awariepradu.model.phonebook.PhonebookContact;
import io.wsz82.awariepradu.model.sms.SmsService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationService {

    public Map<String, List<PhonebookContact>> makeContactGroups() {
        String oauthToken = System.getenv("SMS_TOKEN");
        Map<String, List<PhonebookContact>> contactGroups = makeLocationWithPhonebookContactMap(oauthToken);
        if (contactGroups == null) {
            throw new NullPointerException("Location with phone numbers map is null (no contact with sms-api)");
        }
        return contactGroups;
    }

    private Map<String, List<PhonebookContact>> makeLocationWithPhonebookContactMap(String oauthToken) {
        String getBody;
        try {
            HttpRequest.Builder get = HttpRequest.newBuilder();
            URI uri = new URI(PropertyKey.of("proxy.url.contacts") + "");

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
        Map<String, List<PhonebookContact>> locationPhoneNumbersMap = new HashMap<>(length);
        SubscriptionType[] subscriptionTypes = SubscriptionType.values();
        String locationName = DisplayKey.of("Request.Param.Location");
        String typeName = DisplayKey.of("Request.Param.SubscriptionType");
        for (int i = 0; i < length; i++) {
            JSONObject contact = contacts.getJSONObject(i);
            String id = contact.getString("id");
            String phoneNumber = "";
            try {
                phoneNumber = contact.getString("phone_number");
            } catch (JSONException ignored) {}
            String email = contact.getString("email");
            String locationsText = contact.getString(locationName);
            String type = contact.getString(typeName);
            SubscriptionType subscriptionType = subscriptionTypes[Integer.parseInt(type)];
            String[] locations = locationsText.split(";");

            for (String location : locations) {
                List<PhonebookContact> phoneNumbers = locationPhoneNumbersMap.get(location);
                if (phoneNumbers != null) {
                    phoneNumbers.add(new PhonebookContact(id, phoneNumber, email, subscriptionType));
                } else {
                    phoneNumbers = new ArrayList<>(1);
                    phoneNumbers.add(new PhonebookContact(id, phoneNumber, email, subscriptionType));
                    locationPhoneNumbersMap.put(location, phoneNumbers);
                }
            }

        }
        return locationPhoneNumbersMap;
    }

    public boolean sendToContacts(EmailService emailService, SmsService smsService, List<PhonebookContact> contacts, String message) {
        for (PhonebookContact contact : contacts) {
            switch (contact.subscriptionType()) {
                case EMAIL -> emailService.sendNotificationEmail(contact.email(), message);
                case PHONE -> smsService.sendSms(contact.phoneNumber(), message);
                case ALL -> {
                    emailService.sendNotificationEmail(contact.email(), message);
                    smsService.sendSms(contact.phoneNumber(), message);
                }
            }

        }
        return true;
    }
}
