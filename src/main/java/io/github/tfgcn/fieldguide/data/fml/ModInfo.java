package io.github.tfgcn.fieldguide.data.fml;

import lombok.Data;

import java.nio.file.Path;
import java.util.List;

@Data
public class ModInfo {
    private final String modId;
    private final String name;
    private final List<Dependency> dependencies;
    private final Path jarPath;
    private final int loadOrder;
}