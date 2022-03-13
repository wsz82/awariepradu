package io.wsz82.awariepradu.model.sms;

import io.wsz82.awariepradu.DisplayKey;
import io.wsz82.awariepradu.PropertyKey;
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

public class SmsService {
    private static final String SENDER_NAME = DisplayKey.of("Sender.Name");

    private final Logger logger = LoggerFactory.getLogger(SmsService.class);
    private final SmsFactory smsApi;

    private Exception exception;

    public SmsService() throws Exception {
        String oauthToken = System.getenv("SMS_TOKEN");
        OAuthClient client = new OAuthClient(oauthToken);
        ProxyNative proxy = new ProxyNative(PropertyKey.of("proxy.url"));
        this.smsApi = new SmsFactory(client, proxy);
    }

    public void sendSubscribeSms(String phoneNumber, String confirmationUrl) {
        String message = DisplayKey.of("Contact.Subscribe.SmsMessage.Confirm", confirmationUrl);
        sendSms(phoneNumber, message);
    }

    public void sendSms(String phoneNumber, String message) {
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
            logger.info("Sent sms to: {}", phoneNumber);
            logger.debug("Phone number: {}", messageResponse.getNumber());
            logger.debug("Shipment id: {}", messageResponse.getId());
            logger.debug("Shipment status: {}", messageResponse.getStatus());
            logger.debug("Points: {}", messageResponse.getPoints());
            logger.error("Error: {}", messageResponse.getError());
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
