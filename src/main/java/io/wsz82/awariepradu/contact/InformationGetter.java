package io.wsz82.awariepradu.contact;

import io.wsz82.awariepradu.ScheduledUpdateTask;
import io.wsz82.awariepradu.database.Notification;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class InformationGetter {
    private final static String[] WORDS_TO_REMOVE_FROM_LOCATION = makeWordsToRemove();
    private final static Map<String, String> WORDS_TO_REPLACE_IN_LOCATION = makeWordsToReplace();
    private final static int NUMBER_OF_REGIONS = 45;

    private final Logger logger = LoggerFactory.getLogger(InformationGetter.class);

    private static String[] makeWordsToRemove() {
        return new String[]{"gmina wiejska", "obszar wiejski"};
    }

    private static Map<String, String> makeWordsToReplace() {
        Map<String, String> map = new HashMap<>();
        map.put("gmina miejska", "- miasto");
        return map;
    }

    private List<Notification> notifications;
    private String contentLength;

    public InformationGetter() {
        notifications = new ArrayList<>(NUMBER_OF_REGIONS);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(Misc.URL))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String body = response.body();

            Optional<String> contentLengthOpt = response.headers().firstValue(Misc.CONTENT_LENGTH_HEADER);
            contentLength = contentLengthOpt.get();

            Document doc = Jsoup.parse(body);

            Elements regions = doc.select("div.breakdown__region");
            for (Element region : regions) {
                Element regionName = region.select(".breakdown__region-name").first();
                String regionNameText = regionName.text().trim();

                Elements areas = region.select(".breakdown__area");
                for (Element area : areas) {
                    Element areaName = area.select(".breakdown__area-name").first();
                    String areaNameText = areaName.text();
                    for (String word : WORDS_TO_REMOVE_FROM_LOCATION) {
                        areaNameText = areaNameText.replace(word, "");
                    }
                    for (Map.Entry<String, String> entry : WORDS_TO_REPLACE_IN_LOCATION.entrySet()) {
                        areaNameText = areaNameText.replace(entry.getKey(), entry.getValue());
                    }
                    areaNameText = areaNameText.trim();

                    Element areaMessage = area.select(".breakdown__area-message").first();
                    String messageText = areaMessage.text();

                    Element areaDate = area.select(".breakdown__area-date").first();
                    String dateText = areaDate.text();

                    notifications.add(new Notification(regionNameText, areaNameText, messageText, dateText));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            notifications = null;
        }
        logger.info("Notifications: " + notifications);
    }

    public List<Notification> getNotifications() {
        return notifications;
    }

    public String getContentLength() {
        return contentLength;
    }

    @Override
    public String toString() {
        return "InformationGetter{" +
                "notifications=" + notifications +
                ", contentLength='" + contentLength + '\'' +
                '}';
    }
}
