package io.github.tfgcn.fieldguide.data.gtceu.utils;

import io.github.tfgcn.fieldguide.asset.AssetLoader;
import io.github.tfgcn.fieldguide.data.minecraft.ResourceLocation;

public class ResourceHelper {

    public static AssetLoader assetLoader;

    public static boolean isResourceExist(ResourceLocation rs) {
        return assetLoader.getAsset(String.format("assets/%s/%s", rs.getNamespace(), rs.getPath())) != null;
    }
}
