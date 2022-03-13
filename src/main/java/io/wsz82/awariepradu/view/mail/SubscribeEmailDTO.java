package io.wsz82.awariepradu.view.mail;

import io.wsz82.awariepradu.model.contact.Contact;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SubscribeEmailDTO {
    public static final String CONTACT = "contact";
    public static final String CITY_INDICATOR = "cityIndicator";
    public static final String LINK = "link";

    private final Map<String, Object> variables;

    public SubscribeEmailDTO(Contact contact, String cityIndicator, String link) {
        this.variables = new HashMap<>();
        variables.put(CONTACT, contact);
        variables.put(CITY_INDICATOR, cityIndicator);
        variables.put(LINK, link);
    }

    public Map<String, Object> getVariables() {
        return Collections.unmodifiableMap(variables);
    }
}
