package io.github.tfgcn.fieldguide.asset;

import io.github.tfgcn.fieldguide.data.gtceu.MaterialIconSet;
import io.github.tfgcn.fieldguide.data.gtceu.MaterialIconType;
import io.github.tfgcn.fieldguide.data.minecraft.ResourceLocation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * desc:
 *
 * @author yanmaoyuan
 */
public class AssetLoaderTest {

    @Test
    void test() {
        Assertions.assertTrue(true);
    }

    public static void main(String[] args) {
        Path path = Paths.get("Modpack-Modern");
        AssetLoader assetLoader = new AssetLoader(path);
        ResourceLocation location = MaterialIconType.dust.getItemTexturePath(MaterialIconSet.METALLIC, true);
        System.out.println(location);
    }
}
