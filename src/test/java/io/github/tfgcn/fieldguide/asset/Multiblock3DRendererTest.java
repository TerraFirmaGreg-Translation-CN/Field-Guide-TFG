package io.github.tfgcn.fieldguide.asset;

import io.github.tfgcn.fieldguide.export.ObjExporter;
import io.github.tfgcn.fieldguide.render.Multiblock3DRenderer;
import io.github.tfgcn.fieldguide.render3d.scene.Node;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * desc:
 *
 * @author yanmaoyuan
 */
@Slf4j
public class Multiblock3DRendererTest {

    public static void main(String[] args) throws IOException {
        String modpackPath = "Modpack-Modern";
        AssetLoader assetLoader = new AssetLoader(Paths.get(modpackPath));
        Multiblock3DRenderer renderer = new Multiblock3DRenderer(assetLoader, 512, 512);

        String[][] pattern = new String[][] {
                {"  G  ", "  G  ", "GGCGG", "  G  ", "  G  "},
                {"XXXXX", "XXXXX", "XX0XX", "XXBXX", "XXXXX"}
        };

        pattern = new String[][] {
                {"XXX", "XXX", "XXX"},
                {"GGG", "G G", "GGG"},
                {"GGG", "G G", "GGG"},
                {"GGG", "G G", "GGG"},
                {"XXX", "XXX", "XXX"}
        };

        Map<String, String> mapping = new HashMap<>();
        mapping.put("X", "tfc:rock/smooth/gabbro");
        mapping.put("G", "minecraft:light_blue_stained_glass");
        mapping.put("0", "tfc:charcoal_forge[heat_level=7]");
        mapping.put("C", "tfc:crucible");
        mapping.put("B", "tfc:bellows");

        BufferedImage image = renderer.render(pattern, mapping);

        ImageIO.write(image, "png", Paths.get("output", "multiblock.png").toFile());

        try {
            Node rootNode = renderer.buildMultiblock(pattern, mapping);
            ObjExporter exporter = new ObjExporter();
            exporter.export(rootNode, "output/multiblock.obj");
        } catch (Exception e) {
            log.error("Error exporting OBJ file:", e);
        }

    }
}
