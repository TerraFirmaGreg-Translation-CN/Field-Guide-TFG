package io.github.tfgcn.fieldguide.localization;

import io.github.tfgcn.fieldguide.asset.AssetLoader;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class LazyLocalizationManager implements LocalizationManager{

    private final AssetLoader assetLoader;

    private final Map<String, Map<String, String>> translationCache = new ConcurrentHashMap<>();
    private final Set<String> loadedNamespaces = ConcurrentHashMap.newKeySet();

    private Language currentLanguage = Language.EN_US;
    private final Map<String, String> currentTranslations = new ConcurrentHashMap<>();
    private final Map<String, String> fallbackTranslations = new ConcurrentHashMap<>();

    private final Set<String> essentialNamespaces;
    private final Set<String> discoveredNamespaces = ConcurrentHashMap.newKeySet();
    private final Set<String> excludedNamespaces;

    // 性能统计
    private int cacheHits = 0;
    private int cacheMisses = 0;

    public LazyLocalizationManager(AssetLoader assetLoader) {
        this.assetLoader = assetLoader;

        this.essentialNamespaces = Set.of(
                "minecraft", "forge", "tfc", "patchouli", "tfg", "beneath", "firmalife", "gtceu", "ae2"
        );
        this.excludedNamespaces = Set.of(
                "cloth_config", "config", "library", "api", "util", "lib",
                "jei", "rei", "emi", "jade", "appleskin", "journeymap", "xaeros_minimap",
                "xaeros_world_map", "worldedit", "litematica", "minihud", "tweakeroo",
                "optifine", "sodium", "iris", "rubidium", "oculus", "magnum", "embeddium",
                "google", "gson", "org_jetbrains", "com_google", "org_slf4j", "org_apache",
                "mixin", "spongepowered", "org_objectweb", "net_kyori", "com_mojang");
        loadEssentialNamespaces();
    }

    private void loadEssentialNamespaces() {
        for (String namespace : essentialNamespaces) {
            loadNamespaceTranslations(namespace, "en_us", fallbackTranslations);
            loadedNamespaces.add(namespace);
        }

        log.info("Pre-loaded essential namespaces: {}", essentialNamespaces);
    }

    @Override
    public void switchLanguage(Language lang) {
        String languageCode = lang.getKey();
        this.currentLanguage = lang;
        this.currentTranslations.clear();

        log.info("Loading translations for {} namespaces in {}", discoveredNamespaces.size(), languageCode);

        for (String namespace : discoveredNamespaces) {
            loadNamespaceTranslations(namespace, languageCode, currentTranslations);
            loadedNamespaces.add(namespace);
        }

        // 如果当前语言不是英语，确保回退翻译存在
        if (!"en_us".equals(languageCode)) {
            ensureFallbackTranslations(discoveredNamespaces);
        }

        log.info("Loaded {} translations for {}", currentTranslations.size(), languageCode);
    }

    private void ensureFallbackTranslations(Set<String> namespaces) {
        for (String namespace : namespaces) {
            if (!loadedNamespaces.contains(namespace)) {
                loadNamespaceTranslations(namespace, "en_us", fallbackTranslations);
                loadedNamespaces.add(namespace);
            }
        }
    }

    private void loadNamespaceTranslations(String namespace, String language, Map<String, String> target) {
        String cacheKey = namespace + ":" + language;

        Map<String, String> translations = translationCache.get(cacheKey);
        if (translations == null) {
            cacheMisses++;
            translations = assetLoader.loadLang(namespace, language);
            translationCache.put(cacheKey, translations);

            if (translations.isEmpty()) {
                log.debug("No translations found for {}:{}", namespace, language);
            }
        } else {
            cacheHits++;
        }

        target.putAll(translations);
    }

    @Override
    public Language getCurrentLanguage() {
        return null;
    }

    @Override
    public String translate(String... keys) {
        for (String key : keys) {
            // 首先检查当前语言
            String translation = currentTranslations.get(key);
            if (translation != null) {
                return translation;
            }

            // 然后检查回退语言
            translation = fallbackTranslations.get(key);
            if (translation != null) {
                return translation;
            }
        }

        // 记录缺失的键（可选）
        log.trace("Missing translation for: {}", Arrays.toString(keys));
        return "{" + keys[0] + "}";
    }

    @Override
    public String translate(String key, Object... args) {
        return String.format(translate(key), args);
    }

    @Override
    public Map<String, String> getKeybindings() {
        return Map.of();
    }

    private void lazyLoadNamespace(String namespace) {
        if (!loadedNamespaces.contains(namespace)) {
            log.debug("Lazy loading namespace: {}", namespace);
            loadNamespaceTranslations(namespace, currentLanguage.getKey(), currentTranslations);
            loadNamespaceTranslations(namespace, "en_us", fallbackTranslations);
            loadedNamespaces.add(namespace);
        }
    }
}
