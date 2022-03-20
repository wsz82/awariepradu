package io.wsz82.awariepradu;

import io.wsz82.awariepradu.model.contact.Contact;
import io.wsz82.awariepradu.model.contact.ContactUnsubscribe;
import io.wsz82.awariepradu.model.contact.SubscriptionType;
import io.wsz82.awariepradu.model.email.OnSubscribeCompleteEvent;
import io.wsz82.awariepradu.model.email.OnUnsubscribeCompleteEvent;
import io.wsz82.awariepradu.model.email.VerificationToken;
import io.wsz82.awariepradu.model.email.VerificationTokenRepository;
import io.wsz82.awariepradu.model.phonebook.ContactResponse;
import io.wsz82.awariepradu.model.phonebook.PhonebookContact;
import io.wsz82.awariepradu.model.phonebook.PhonebookContactService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
public class AppController {
    private static final String CONTACT = "contact";
    private static final String CONTACT_UNSUBSCRIBE = "contactUnsubscribe";
    private static final String SERVER_RESPONSE = "serverResponse";

    private static final String INDEX = "index";
    private static final String TOKEN_MESSAGE = "tokenMessage";
    private static final String UNSUBSCRIBE = "unsubscribe";
    private static final String UNSUBSCRIBE_CONFIRM = "unsubscribe-confirm";
    private static final String SUBSCRIBE_CONFIRM = "subscribe-confirm";
    private static final String BAD_USER = "bad-user";

    private static final long MILLIS_ONE_DAY = 24*60*60*1000;
    private static final long MILLIS_5_MIN = 5*60*1000;

    private final Logger logger = LoggerFactory.getLogger(AppController.class);

    @Autowired
    PhonebookContactService phonebookContactService;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    VerificationTokenRepository verificationTokenRepository;

    @GetMapping({"/", "/" + INDEX})
    public String contactForm(Model model) {
        Contact contact = new Contact();
        contact.setSubscriptionType(SubscriptionType.EMAIL.ordinal());
        model.addAttribute(CONTACT, contact);
        return INDEX;
    }

    @PostMapping({"/", "/" + INDEX})
    public String subscribe(@Valid Contact contact, BindingResult result, Model model, HttpServletRequest request) {
        if (result.hasErrors()) {
            return INDEX;
        }

        String phoneNumber = contact.getPhoneNumber();
        String email = contact.getEmail();
        SubscriptionType subscriptionType = contact.parsedSubscriptionType();

        if (subscriptionType == SubscriptionType.ALL || subscriptionType == SubscriptionType.PHONE) {
            model.addAttribute(SERVER_RESPONSE, DisplayKey.of("Contact.Subscribe.Unavailable"));
            return INDEX;
        }

        if (email.isBlank()) {
            model.addAttribute(SERVER_RESPONSE, DisplayKey.of("Contact.Subscribe.AddEmail"));
            return INDEX;
        }
        if ((phoneNumber == null || phoneNumber.isBlank()) && (subscriptionType == SubscriptionType.PHONE || subscriptionType == SubscriptionType.ALL)) {
            model.addAttribute(SERVER_RESPONSE, DisplayKey.of("Contact.Subscribe.AddPhoneNumber"));
            return INDEX;
        }

        boolean isAnyValidToken = isAnyValidToken(email);
        if (isAnyValidToken) {
            model.addAttribute(SERVER_RESPONSE, DisplayKey.of("Contact.Subscribe.TimeLimitExceeded"));
            return INDEX;
        }

        boolean contactExists = phonebookContactService.doContactExist(email);
        logger.info("Subscribe: do contact ({}) exist in phone book: {}", email, contactExists);
        if (contactExists) {
            model.addAttribute(SERVER_RESPONSE, DisplayKey.of("Contact.Subscribe.Exists"));
        } else {
            String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                    .replacePath(null)
                    .build()
                    .toUriString();
            eventPublisher.publishEvent(new OnSubscribeCompleteEvent(contact, baseUrl));
            if (subscriptionType == SubscriptionType.EMAIL) {
                model.addAttribute(SERVER_RESPONSE, DisplayKey.of("Contact.Subscribe.Email.Confirm"));
            } else {
                model.addAttribute(SERVER_RESPONSE, DisplayKey.of("Contact.Subscribe.Sms.Confirm"));
            }
        }

        return INDEX;
    }

    private boolean isAnyValidToken(String email) {
        return verificationTokenRepository.findAll().stream()
                .filter(t -> email.equals(t.getContact().getEmail()))
                .map(VerificationToken::getExpiryTimeMillis)
                .anyMatch(expireTimeMillis -> {
                    long exceedTime = expireTimeMillis - MILLIS_ONE_DAY + MILLIS_5_MIN;
                    long time = System.currentTimeMillis();
                    return time < exceedTime;
                });
    }

    @GetMapping("/" + SUBSCRIBE_CONFIRM)
    public String subscribeConfirm(@RequestParam("token") String token, Model model) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken == null) {
            model.addAttribute(TOKEN_MESSAGE, DisplayKey.of("Contact.Subscribe.Token.No"));
            return BAD_USER;
        }

        Contact contact = verificationToken.getContact();
        long expireTime = verificationToken.getExpiryTimeMillis();
        long time = System.currentTimeMillis();
        if ((expireTime - time) <= 0) {
            model.addAttribute(TOKEN_MESSAGE, DisplayKey.of("Contact.Subscribe.Token.Expired"));
            return BAD_USER;
        }

        ContactResponse contactResponse = phonebookContactService.addContact(contact);
        logger.info("{} - contact add database response: {}", contact, contactResponse);
        model.addAttribute(SERVER_RESPONSE, contactResponse.message());

        return SUBSCRIBE_CONFIRM;
    }

    @GetMapping("/" + UNSUBSCRIBE)
    public String goUnsubscribe(Model model) {
        ContactUnsubscribe contact = new ContactUnsubscribe();
        model.addAttribute(CONTACT_UNSUBSCRIBE, contact);
        return UNSUBSCRIBE;
    }

    @PostMapping("/" + UNSUBSCRIBE)
    public String unsubscribe(@Valid ContactUnsubscribe formContact, BindingResult result, Model model, HttpServletRequest request) {
        if (result.hasErrors()) {
            return UNSUBSCRIBE;
        }

        String email = formContact.getEmail();

        boolean isAnyValidToken = isAnyValidToken(email);
        if (isAnyValidToken) {
            model.addAttribute(SERVER_RESPONSE, DisplayKey.of("Contact.Subscribe.TimeLimitExceeded"));
            return UNSUBSCRIBE;
        }

        PhonebookContact phonebookContact = phonebookContactService.getContactByEmail(email);
        boolean contactExists = phonebookContact != null;
        logger.info("Unsubscribe: do contact exist in phone book: {}", contactExists);
        if (contactExists) {
            String baseUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                    .replacePath(null)
                    .build()
                    .toUriString();
            eventPublisher.publishEvent(new OnUnsubscribeCompleteEvent(phonebookContact, baseUrl));
            model.addAttribute(SERVER_RESPONSE, DisplayKey.of("Contact.Unsubscribe.Confirm"));
        } else {
            model.addAttribute(SERVER_RESPONSE, DisplayKey.of("Contact.Unsubscribe.NotExists"));
        }
        return UNSUBSCRIBE;
    }

    @GetMapping("/" + UNSUBSCRIBE_CONFIRM)
    public String unsubscribeConfirm(@RequestParam("token") String token, Model model) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken == null) {
            model.addAttribute(TOKEN_MESSAGE, DisplayKey.of("Contact.Unsubscribe.Token.No"));
            return BAD_USER;
        }

        Contact contact = verificationToken.getContact();
        long expireTime = verificationToken.getExpiryTimeMillis();
        long time = System.currentTimeMillis();
        if ((expireTime - time) <= 0) {
            model.addAttribute(TOKEN_MESSAGE, DisplayKey.of("Contact.Subscribe.Token.Expired"));
            return BAD_USER;
        }

        ContactResponse contactResponse = phonebookContactService.deleteContact(contact);
        logger.info("{} - contact delete database response: {}", contact, contactResponse);
        model.addAttribute(SERVER_RESPONSE, contactResponse.message());

        return UNSUBSCRIBE_CONFIRM;
    }
}
