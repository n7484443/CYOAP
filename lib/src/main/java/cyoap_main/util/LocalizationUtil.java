package cyoap_main.util;

import java.io.IOException;
import java.util.Properties;

public class LocalizationUtil {
    static final String localizationFolder = "lib/i18n/";
    public static String i18n_current = "ko_kr";
    public static String i18n_public = "en_us";
    public static Properties localized_properties_current;
    public static Properties localized_properties_public;
    static final LocalizationUtil instance = new LocalizationUtil();

    public static LocalizationUtil getInstance() {
        return instance;
    }

    public String getLocalization(String unLocalizedString) {
        if (localized_properties_current.containsKey(unLocalizedString)) {
            return localized_properties_current.getProperty(unLocalizedString);
        } else {
            if (localized_properties_public.containsKey(unLocalizedString)) {
                return localized_properties_public.getProperty(unLocalizedString);
            }
            System.out.println("can't find Localized String");
            return unLocalizedString;
        }
    }

    public void loadLocalization() {
        var stream_current = LoadUtil.class.getClassLoader().getResourceAsStream(localizationFolder + i18n_current + ".properties");
        var stream_public = LoadUtil.class.getClassLoader().getResourceAsStream(localizationFolder + i18n_public + ".properties");

        localized_properties_current = new Properties();
        localized_properties_public = new Properties();
        try {
            localized_properties_current.load(stream_current);
            localized_properties_public.load(stream_public);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void changeLanguage(String str) {
        i18n_current = str;
        loadLocalization();
    }
}
