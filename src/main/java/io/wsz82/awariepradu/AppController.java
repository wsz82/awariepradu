package io.wsz82.awariepradu;

import io.wsz82.awariepradu.contact.Contact;
import io.wsz82.awariepradu.contact.ContactResponse;
import io.wsz82.awariepradu.contact.ContactUnsubscribe;
import io.wsz82.awariepradu.contact.ContactUpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class AppController {
    private static final String CONTACT = "contact";
    private static final String CONTACT_UNSUBSCRIBE = "contactUnsubscribe";
    private static final String SERVER_RESPONSE = "serverResponse";
    private static final String UNSUBSCRIBE = "unsubscribe";
    private static final String INDEX = "index";
    private static final String PHONE_NUMBER_PREFIX = "48";

    private final Logger logger = LoggerFactory.getLogger(AppController.class);

    @Autowired
    private ApplicationContext appContext;

    @GetMapping({"/", "/" + INDEX})
    public String contactForm(Model model) {
        Contact contact = new Contact();
        contact.setPhoneNumber(PHONE_NUMBER_PREFIX);
        model.addAttribute(CONTACT, contact);
        return INDEX;
    }

    @PostMapping({"/", "/" + INDEX})
    public String addContact(@Valid Contact contact, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return INDEX;
        }
        ContactUpdateService updaterService = appContext.getBean(ContactUpdateService.class);
        ContactResponse contactResponse = updaterService.addContact(contact);
        logger.info("{} - contact add database response: {}", contact, contactResponse);
        model.addAttribute(SERVER_RESPONSE, contactResponse.message());
        return INDEX;
    }

    @GetMapping("/" + UNSUBSCRIBE)
    public String goUnsubscribe(Model model) {
        ContactUnsubscribe contactUnsubscribe = new ContactUnsubscribe();
        contactUnsubscribe.setPhoneNumber(PHONE_NUMBER_PREFIX);
        model.addAttribute(CONTACT_UNSUBSCRIBE, contactUnsubscribe);
        return UNSUBSCRIBE;
    }

    @PostMapping("/" + UNSUBSCRIBE)
    public String unsubscribe(@Valid ContactUnsubscribe contactUnsubscribe, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return UNSUBSCRIBE;
        }
        ContactUpdateService updaterService = appContext.getBean(ContactUpdateService.class);
        ContactResponse contactResponse = updaterService.deleteContact(contactUnsubscribe);
        logger.info("{} - contact delete database response: {}", contactUnsubscribe, contactResponse);
        model.addAttribute(SERVER_RESPONSE, contactResponse.message());
        return UNSUBSCRIBE;
    }
}
