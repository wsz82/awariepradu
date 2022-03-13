package io.wsz82.awariepradu.view.mail;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class NotificationDTO {
    public static final String LOCATION = "location";
    public static final String MESSAGE = "message";
    public static final String TIME = "time";

    private final Map<String, Object> variables;

    public NotificationDTO(String location, String message, String time) {
        this.variables = new HashMap<>();
        variables.put(LOCATION, location);
        variables.put(MESSAGE, message);
        variables.put(TIME, time);
    }

    public Map<String, Object> getVariables() {
        return Collections.unmodifiableMap(variables);
    }
}
