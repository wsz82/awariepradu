package io.wsz82.awariepradu;

import java.util.Properties;

public class PropertyKey {
    private static final Properties properties = Config.readProperties("links.properties");

    public static String of(String key, Object... args) {
        return properties.getProperty(key).formatted(args);
    }
}
