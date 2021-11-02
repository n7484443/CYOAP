package cyoap_main.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

public class LocalizationUtil {
    public static final String localizationFolder = "lib/i18n/";
    public static String i18n_current = "en_us";
    public static Properties localized_properties;
    public LocalizationUtil instance;

    public LocalizationUtil() {
        instance = this;
    }

    public static String getLocalization(String unLocalizedString) {
        if (localized_properties.containsKey(unLocalizedString)) {
            return localized_properties.getProperty(unLocalizedString);
        } else {
            System.out.println("can't find Localized String");
            return null;
        }
    }

    public void loadLocalization() {
        var stream = LoadUtil.class.getClassLoader().getResourceAsStream(localizationFolder + i18n_current + ".properties");

        localized_properties = new Properties();
        try {
            localized_properties.load(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void changeLocalization(String str) {
        i18n_current = str;
        loadLocalization();
    }
}
