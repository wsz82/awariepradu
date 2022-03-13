package io.wsz82.awariepradu;

import java.util.Properties;

public class DisplayKey {
    public static final String[] REGIONS = new String[]{
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
    private static final Properties properties = Config.readProperties("display.properties");

    public static String of(String key, Object... args) {
        return properties.getProperty(key).formatted(args);
    }
}
