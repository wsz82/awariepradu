package io.wsz82.awariepradu;

import io.wsz82.awariepradu.contact.InformationGetter;
import io.wsz82.awariepradu.contact.UpdateChecker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UpdateCheckerTest {

    @Test
    void isUpToDate() {
        InformationGetter informationGetter = new InformationGetter();
        String contentLength = informationGetter.getContentLength();
        UpdateChecker updateChecker = new UpdateChecker(contentLength);
        boolean upToDate = updateChecker.isUpToDate();

        assertTrue(upToDate);
    }
}