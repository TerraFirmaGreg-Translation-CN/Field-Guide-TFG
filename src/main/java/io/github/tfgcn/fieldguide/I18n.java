package io.github.tfgcn.fieldguide;

import java.util.Locale;
import java.util.ResourceBundle;

public final class I18n {
    private static ResourceBundle bundle;

    public static final String TITLE = key("title");
    public static final String SHORT_TITLE = key("short_title");
    public static final String INDEX = key("index");
    public static final String CONTENTS = key("contents");
    public static final String VERSION = key("version");
    public static final String API_DOCS = key("api_docs");
    public static final String GITHUB = key("github");
    public static final String DISCORD = key("discord");
    public static final String CATEGORIES = key("categories");
    public static final String HOME = key("home");
    public static final String MULTIBLOCK = key("multiblock");
    public static final String MULTIBLOCK_ONLY_IN_GAME = key("multiblock_only_in_game");
    public static final String RECIPE = key("recipe");
    public static final String RECIPE_ONLY_IN_GAME = key("recipe_only_in_game");
    public static final String ITEM = key("item");
    public static final String ITEMS = key("items");
    public static final String ITEM_ONLY_IN_GAME = key("item_only_in_game");
    public static final String ADDON = key("addon");
    public static final String TICKS = key("ticks");
    public static final String TAG = key("tag");

    public static final String KEY_INVENTORY = key("key.inventory");
    public static final String KEY_ATTACK = key("key.attack");
    public static final String KEY_USE = key("key.use");
    public static final String KEY_DROP = key("key.drop");
    public static final String KEY_SNEAK = key("key.sneak");
    public static final String KEY_CYCLE_CHISEL_MODE = key("tfc.key.cycle_chisel_mode");
    public static final String KEY_PLACE_BLOCK = key("tfc.key.place_block");

    public static final String[] KEYS = {KEY_INVENTORY, KEY_ATTACK, KEY_USE, KEY_DROP, KEY_SNEAK, KEY_CYCLE_CHISEL_MODE, KEY_PLACE_BLOCK};

    public static final String LANGUAGE_NAME = key("language.%s");

    static {
        setLocale(Locale.getDefault());
    }

    private I18n() {
    }

    public static void setLocale(Locale locale) {
        try {
            bundle = ResourceBundle.getBundle("message", locale);
        } catch (Exception e) {
            bundle = ResourceBundle.getBundle("message", Locale.ROOT);
        }
    }

    public static String getString(String key) {
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            return "!" + key + "!";
        }
    }

    public static String getString(String key, Object... args) {
        try {
            String pattern = bundle.getString(key);
            return String.format(pattern, args);
        } catch (Exception e) {
            return "!" + key + "!";
        }
    }

    public static String key(String text) {
        return "field_guide." + text;
    }
}