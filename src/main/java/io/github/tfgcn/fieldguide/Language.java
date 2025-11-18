package io.github.tfgcn.fieldguide;

import lombok.Getter;

import java.util.List;

@Getter
public enum Language {
    EN_US("en_us", "English"),
    JA_JP("ja_jp", "日本語"),
    KO_KR("ko_kr", "한국어"),
    PT_BR("pt_br", "Portuguese (Brazil)"),
    RU_RU("ru_ru", "Русский"),
    UK_UA("uk_ua", "Українська мова"),
    ZH_CN("zh_cn", "简体中文(中国大陆)"),
    ZH_HK("zh_hk", "繁體中文（港澳)"),
    ZH_TW("zh_tw", "繁體中文(台灣)"),
    ;
    private final String code;
    private final String name;

    Language(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static List<Language> asList() {
        return List.of(values());
    }

    @Override
    public String toString() {
        return code;
    }
}
