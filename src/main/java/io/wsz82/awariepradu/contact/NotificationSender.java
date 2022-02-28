package io.wsz82.awariepradu.contact;

import io.wsz82.awariepradu.Secrets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private final Logger logger = LoggerFactory.getLogger(NotificationSender.class);

    private final boolean isTest;
    private final SmsFactory smsApi;
    private String exceptionMessage;

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
        exceptionMessage = null;
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
            logger.info("Phone number: " + messageResponse.getNumber());
            logger.info("Shipment id: " + messageResponse.getId());
            logger.info("Shipment status: " + messageResponse.getStatus());
            logger.info("Points: " + messageResponse.getPoints());
            logger.error("Error: " + messageResponse.getError());
        } catch (SmsapiException e) {
            exceptionMessage = e.getMessage();
            boolean isGroupNotFoundException = exceptionMessage.contains("Groups not found");
            if (isGroupNotFoundException) {
                logger.info("Group not found: " + groupName);
            } else {
                logger.error("Exception: " + exceptionMessage);
            }
        }
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }
}
