package io.github.tfgcn.fieldguide.asset;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Paths;

import static io.github.tfgcn.fieldguide.render.TextureRenderer.multiplyImageByColor;

/**
 * desc:
 *
 * @author yanmaoyuan
 */
public class DrawGtceuIgnotTest {
    public static void main(String[] args) {
        String modpackPath = "Modpack-Modern";
        AssetLoader assetLoader = new AssetLoader(Paths.get(modpackPath));

        BufferedImage ingot = assetLoader.loadTexture("gtceu:item/material_sets/metallic/ingot");
        BufferedImage ingotOverlay = assetLoader.loadTexture("gtceu:item/material_sets/metallic/ingot_overlay");
        //BufferedImage ingot = assetLoader.loadTexture("gtceu:item/material_sets/metallic/ingot_hot");
        //BufferedImage ingotOverlay = assetLoader.loadTexture("gtceu:item/material_sets/metallic/ingot_hot_overlay");
        BufferedImage ingotSecondary = assetLoader.loadTexture("gtceu:item/material_sets/metallic/ingot_secondary");

        Color color = new Color(0xffc370);// color(0xffc370).secondaryColor(0x806752)
        Color secondary = new Color(0x806752);

        BufferedImage base = multiplyImageByColor(ingot, color);
        BufferedImage secondaryOverlay = multiplyImageByColor(ingotSecondary, secondary);

        BufferedImage combined = new BufferedImage(base.getWidth(), base.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = combined.createGraphics();
        g.drawImage(base, 0, 0, null);
        g.drawImage(ingotOverlay, 0, 0, null);
        g.drawImage(secondaryOverlay, 0, 0, null);
        g.dispose();
        System.out.println(ingot);
    }
}
