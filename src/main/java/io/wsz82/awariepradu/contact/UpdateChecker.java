package io.wsz82.awariepradu.contact;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class UpdateChecker {
    private final Logger logger = LoggerFactory.getLogger(UpdateChecker.class);

    private final String lastUpdateLength;

    public UpdateChecker(String lastUpdateLength) {
        this.lastUpdateLength = lastUpdateLength;
    }

    public boolean isUpToDate() {
        String contentLength = requestContentLength();
        logger.info("Checked content length: " + contentLength);
        return lastUpdateLength.equals(contentLength);
    }

    private String requestContentLength() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(Misc.URL))
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpClient client = HttpClient.newHttpClient();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            HttpHeaders headers = response.headers();
            Optional<String> contentLengthOpt = headers.firstValue(Misc.CONTENT_LENGTH_HEADER);

            return contentLengthOpt.get();
        } catch (URISyntaxException | InterruptedException | IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
