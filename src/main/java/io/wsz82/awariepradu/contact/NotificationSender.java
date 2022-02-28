package io.wsz82.awariepradu.contact;

import io.wsz82.awariepradu.Secrets;
import pl.smsapi.OAuthClient;
import pl.smsapi.api.SmsFactory;
import pl.smsapi.api.action.sms.SMSSend;
import pl.smsapi.api.response.MessageResponse;
import pl.smsapi.api.response.StatusResponse;
import pl.smsapi.exception.SmsapiException;
import pl.smsapi.proxy.ProxyNative;

import java.util.Optional;

public class NotificationSender {
    private static final String PROXY_URL = "https://api.smsapi.pl/";
    private static final String SENDER_NAME = "AwariePradu";

    private final boolean isTest;
    private final SmsFactory smsApi;
    private String exception;

    public NotificationSender(boolean isTest) throws Exception {
        this.isTest = isTest;
        String oauthToken = Secrets.OAUTH_TOKEN;
        OAuthClient client = new OAuthClient(oauthToken);
        ProxyNative proxy = new ProxyNative(PROXY_URL);
        smsApi = new SmsFactory(client, proxy);
    }

    public void sendToGroup(String groupName, String message) {
        SMSSend action = smsApi.actionSend()
                .setGroup(groupName);
        exception = null;
        action.setTest(isTest)
                .setText(message);
//        if (!isTest) {
//            action.setSender(SENDER_NAME);
//        }
        try {
            StatusResponse result = action.execute();

            Optional<MessageResponse> status = result.getList().stream().findFirst();
            if (status.isEmpty()) {
                throw new RuntimeException();
            }

            MessageResponse messageResponse = status.get();
            System.out.println("Phone number: " + messageResponse.getNumber());
            System.out.println("Shipment id: " + messageResponse.getId());
            System.out.println("Shipment status: " + messageResponse.getStatus());
            System.out.println("Error: " + messageResponse.getError());
            System.out.println("Points: " + messageResponse.getPoints());
        } catch (SmsapiException e) {
            exception = e.getMessage();
            boolean isGroupNotFoundException = exception.contains("Groups not found");
            if (isGroupNotFoundException) {
                System.out.println("Group not found: " + groupName);
            } else {
                System.out.println("Exception: " + exception);
            }
        }
    }

    public String getException() {
        return exception;
    }
}
