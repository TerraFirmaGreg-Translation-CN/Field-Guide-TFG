package io.github.tfgcn.fieldguide;

import io.github.tfgcn.fieldguide.asset.AssetLoader;
import lombok.extern.slf4j.Slf4j;
import picocli.CommandLine;

import java.nio.file.Paths;
import java.util.concurrent.Callable;

@Slf4j
public class Main implements Callable<Integer>  {

    @CommandLine.Option(
            names = {"-i", "--tfg-dir"},
            required = true,
            description = {"The dir of TerraFirmaGreg modpack.",
                    "Support environment TFG_DIR",
                    "e.g. \"/Users/yanmaoyuan/games/tfg-0.11.7\""},
            defaultValue = "${env:TFG_DIR}",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS
    )
    protected String inputDir;

    @CommandLine.Option(
            names = {"-o", "--out-dir"},
            description = "The dir of output. e.g. \"./output\"",
            defaultValue = "./output",
            showDefaultValue = CommandLine.Help.Visibility.ALWAYS
    )
    protected String outputDir;

    public static void main(String[] args) {
        CommandLine cmd = new CommandLine(new Main());
        System.exit(cmd.execute(args));
    }

    @Override
    public Integer call() throws Exception {
        log.info("Start parsing book..., tfg: {}, out: {}", inputDir, outputDir);

        // The TerraFirmaGreg modpack directory
        String modpackPath = inputDir.replace("\\", "/");

        AssetLoader assetLoader = new AssetLoader(Paths.get(modpackPath));

        Context context = new Context(assetLoader, outputDir, "/Field-Guide-TFG", false);

        BookParser bookParser = new BookParser();
        bookParser.processAllLanguages(context);
        return 0;
    }
}