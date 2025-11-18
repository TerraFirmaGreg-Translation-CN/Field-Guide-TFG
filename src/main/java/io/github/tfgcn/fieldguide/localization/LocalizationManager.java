package io.github.tfgcn.fieldguide.localization;

import java.util.Map;

public interface LocalizationManager {

    void switchLanguage(Language lang);

    Language getCurrentLanguage();

    String translate(String ... keys);

    String translate(String key, Object ... args);

    Map<String, String> getKeybindings();
}
