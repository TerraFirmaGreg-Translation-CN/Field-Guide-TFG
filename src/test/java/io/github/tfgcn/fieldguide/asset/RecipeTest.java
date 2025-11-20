package io.github.tfgcn.fieldguide.asset;

import io.github.tfgcn.fieldguide.data.recipe.Recipe;
import io.github.tfgcn.fieldguide.gson.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * desc:
 *
 * @author yanmaoyuan
 */
@Slf4j
public class RecipeTest {

    @Test
    void load() {
        Map<String, Map<String, Object>> data;
        try (InputStream in = RecipeTest.class.getResourceAsStream("/recipes.json")) {
            data = JsonUtils.readFile(in, Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Set<String> types = new TreeSet<>();
        for (Map.Entry<String, Map<String, Object>> entry : data.entrySet()) {
            String recipeId = entry.getKey();
            Map<String, Object> map = entry.getValue();
            String type = (String) map.get("type");
            types.add(type);

            map.remove("__comment__");
            String json = JsonUtils.toJson(map);

            try {
                Recipe recipe = JsonUtils.fromJson(json, Recipe.class);
                String json2 = JsonUtils.toJson(recipe);
                log.debug("{}", json2);
            } catch (Exception e) {
                log.error("解析失败, recipeId: {}, type: {}, recipe: {}", recipeId, type, json, e);
            }
        }

        System.out.println(types);
    }

    @Test
    void testLoadRecipe() {
        AssetLoader loader = new AssetLoader(Paths.get("Modpack-Modern"));
        Map<String, Object> recipe = loader.loadRecipe("tfc:barrel/cheese");
        log.info("{}", recipe);
        AssetKey assetKey = new AssetKey("tfc:barrel/cheese", "recipes", "data", ".json");
        List<Asset> assets = loader.getAssets(assetKey);
        for (Asset asset : assets) {
            log.info("{}", asset);
        }
    }
}
