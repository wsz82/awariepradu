package io.wsz82.awariepradu;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import java.io.*;
import java.util.Properties;

public class DisplayKey {
    private static final Properties properties = readProperties();

    private static Properties readProperties() {
        ClassLoader classLoader = Application.class.getClassLoader();
        String propertiesFilePath = classLoader.getResource("display.properties").getFile();
        File propertiesFile = new File(propertiesFilePath);
        Properties properties = new Properties();
        try (Reader r = new FileReader(propertiesFile)) {
            properties.load(r);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static String of(String key) {
        return properties.get(key).toString();
    }
}
