package io.github.tfgcn.fieldguide.asset;

import io.github.tfgcn.fieldguide.data.fml.ModInfo;
import io.github.tfgcn.fieldguide.data.fml.ModLoader;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * desc:
 *
 * @author yanmaoyuan
 */
@Slf4j
public class ModLoaderTest {

    @Test
    void testLoader() throws IOException {
        Path path = Paths.get("Modpack-Modern", "mods");
        ModLoader loader = new ModLoader();
        loader.loadMods(path);
    }
}
