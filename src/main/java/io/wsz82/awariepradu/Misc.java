package io.wsz82.awariepradu;

public class Misc {
    public static final String URL = "https://energa-operator.pl/uslugi/awarie-i-wylaczenia/wylaczenia-planowane";
    public static final String CONTENT_LENGTH_HEADER = "Content-Length";
    public static final String PROXY_URL = "https://api.smsapi.pl/";
    public static final String PROXY_CONTACTS_URL = "https://api.smsapi.pl/contacts";

    public static String makeGroupName(String region, String location, Boolean isCity) {
        String groupName = region + " - " + location;
        if (isCity != null && isCity) {
            groupName += " - miasto";
        }
        return groupName;
    }

    public static final String[] REGIONS = new String[] {
            "Gdańsk",
            "Gdynia",
            "Kartuzy",
            "Starogard Gdański",
            "Tczew",
            "Wejherowo",
            "Jarocin",
            "Kalisz",
            "Kępno",
            "Koło",
            "Konin",
            "Ostrów Wielkopolski",
            "Słupca",
            "Turek",
            "Białogard",
            "Bytów",
            "Człuchów",
            "Kołobrzeg",
            "Koszalin",
            "Lębork",
            "Słupsk",
            "Szczecinek",
            "Braniewo",
            "Elbląg",
            "Iława",
            "Kętrzyn",
            "Kwidzyn",
            "Lidzbark Warmiński",
            "Ostróda",
            "Szczytno",
            "Ciechanów",
            "Gostynin",
            "Kutno",
            "Mława",
            "Płock",
            "Płońsk",
            "Sierpc",
            "Brodnica",
            "Grudziądz",
            "Radziejów",
            "Rypin",
            "Toruń",
            "Włocławek"
    };
}
