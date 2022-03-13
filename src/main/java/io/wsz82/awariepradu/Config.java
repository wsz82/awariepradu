package io.wsz82.awariepradu;

import io.wsz82.awariepradu.model.FileResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    public static JavaMailSender javaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        String username = System.getenv("AP_USERNAME");
        String password = System.getenv("AP_PASSWORD");
        javaMailSender.setUsername(username);
        javaMailSender.setPassword(password);
        Properties prop = readProperties("spring.properties");
        javaMailSender.setJavaMailProperties(prop);
        return javaMailSender;
    }

    public static Properties readProperties(String fileName) {
        logger.info("Started reading properties file: " + fileName);
        Properties properties = new Properties();
        FileResourceUtils fileResourceUtils = new FileResourceUtils();
        try (
                InputStream in = fileResourceUtils.getFileFromResourceAsStream(fileName);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in))
        ) {
            properties.load(reader);
            logger.info("Properties file read successfully: " + fileName);
        } catch (IOException e) {
            logger.info("Properties file read failed: " + fileName);
            e.printStackTrace();
        }
        return properties;
    }
}
