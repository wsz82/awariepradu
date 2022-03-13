package io.wsz82.awariepradu;

import io.wsz82.awariepradu.model.contact.Contact;
import io.wsz82.awariepradu.model.contact.ContactRepository;
import io.wsz82.awariepradu.model.email.VerificationToken;
import io.wsz82.awariepradu.model.email.VerificationTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class ScheduledCleanTokensTask {
    private final Logger logger = LoggerFactory.getLogger(ScheduledCleanTokensTask.class);

    @Autowired
    ContactRepository contactRepository;

    @Autowired
    VerificationTokenRepository tokenRepository;

    @Transactional
    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.DAYS)
    public void runUpdate() {
        logger.info("Started cleaning tokens and contacts tables");

        long time = Calendar.getInstance().getTime().getTime();
        List<VerificationToken> tokens = tokenRepository.findAll();

        List<VerificationToken> expiredTokens = tokens.stream()
                .filter(t -> {
                    Date expiryDate = t.getExpiryDate();
                    if (expiryDate == null) return true;
                    long expireTime = expiryDate.getTime();
                    return expireTime - time <= 0;
                })
                .toList();
        int expiredTokensSize = expiredTokens.size();
        if (expiredTokensSize > 0) {
            tokenRepository.deleteAll(expiredTokens);
            logger.info("Deleted {} tokens of {}", expiredTokensSize, tokens.size());
        }

        ArrayList<VerificationToken> nonExpiredTokens = new ArrayList<>(tokens);
        nonExpiredTokens.removeAll(expiredTokens);
        logger.info("Non expired tokens: {}", nonExpiredTokens.size());

        Set<Contact> contactsWithNonExpiredTokens = nonExpiredTokens.stream()
                .map(VerificationToken::getContact)
                .collect(Collectors.toSet());
        logger.info("Contacts with non expired tokens: {}", contactsWithNonExpiredTokens.size());

        List<Contact> contacts = contactRepository.findAll();
        List<Contact> contactsToDelete = new ArrayList<>(contacts);
        contactsToDelete.removeAll(contactsWithNonExpiredTokens);
        int contactsToDeleteSize = contactsToDelete.size();
        if (contactsToDeleteSize > 0) {
            contactRepository.deleteAll(contactsToDelete);
            logger.info("Deleted {} contacts of {} all contacts", contactsToDeleteSize, contacts.size());
        }
    }
}
