package io.github.tfgcn.fieldguide.data.fml;

import lombok.extern.slf4j.Slf4j;
import net.vieiro.toml.TOML;
import net.vieiro.toml.TOMLParser;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.TopologicalOrderIterator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

@Slf4j
public class ModLoader {
    private final List<ModInfo> loadedMods = new ArrayList<>();
    private final Map<String, ModInfo> modIdMap = new HashMap<>();
    
    public void loadMods(Path modsDir) throws IOException {
        if (!Files.exists(modsDir)) {
            log.info("Mods directory not found: {}", modsDir);
            return;
        }

        // 1 scan all modes
        List<ModInfo> allMods = scanMods(modsDir);
        
        // 2- build mod dependencies
        List<ModInfo> sortedMods = sortModsByDependencies(allMods);
        
        // 3- load mods
        loadSortedMods(sortedMods);
        
        log.info("Loaded {} mods in correct dependency order", loadedMods.size());
    }
    
    private List<ModInfo> scanMods(Path modsDir) throws IOException {
        List<ModInfo> mods = new ArrayList<>();


        // add root
        ModInfo mc = new ModInfo("minecraft", "Minecraft", Collections.emptyList(), null, 0);
        mods.add(mc);
        modIdMap.put("minecraft", mc);
        ModInfo forge = new ModInfo("forge", "Forge", Collections.emptyList(), null, 0);
        mods.add(forge);
        modIdMap.put("forge", forge);

        try (Stream<Path> jars = Files.list(modsDir)) {
            List<Path> jarList = jars
                .filter(p -> p.toString().endsWith(".jar"))
                .toList();
            
            for (Path jar : jarList) {
                try {
                    ModInfo modInfo = parseModInfo(jar);
                    if (modInfo != null) {
                        mods.add(modInfo);
                        modIdMap.put(modInfo.getModId(), modInfo);
                    }
                } catch (Exception e) {
                    log.error("Failed to parse mod info from: {}", jar.getFileName(), e);
                }
            }
        }
        
        return mods;
    }
    
    private ModInfo parseModInfo(Path jarPath) throws IOException {
        try (JarFile jar = new JarFile(jarPath.toFile())) {
            JarEntry modsTomlEntry = jar.getJarEntry("META-INF/mods.toml");
            if (modsTomlEntry == null) {
                log.warn("No mods.toml found in: {}", jarPath.getFileName());
                return null;
            }
            
            try (InputStream is = jar.getInputStream(modsTomlEntry)) {
                return parseModsToml(is, jarPath);
            }
        }
    }
    
    private ModInfo parseModsToml(InputStream is, Path jarPath) throws IOException {
        TOML toml = TOMLParser.parseFromInputStream(is);

        String modId = toml.getString("mods/0/modId").orElse(null);
        String name = toml.getString("mods/0/displayName").orElse(null);
        int loadOrder = parseLoadOrder(toml.getString("mods/0/loadOrder").orElse("NONE"));
        if (modId == null) {
            log.warn("No modId found in mods.toml for: {}", jarPath.getFileName());
            return null;
        }
        
        // 设置默认值
        if (name == null) {
            name = modId;
        }

        int size = toml.getArray("/dependencies/" + modId).orElse(List.of()).size();
        List<Dependency> dependencies = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String id = toml.getString("/dependencies/" + modId + "/" + i + "/modId").orElse(null);
            String ordering = toml.getString("/dependencies/" + modId + "/" + i + "/ordering").orElse("NONE");
            Boolean mandatory = toml.getBoolean("/dependencies/" + modId + "/" + i + "/mandatory").orElse(false);

            dependencies.add( new Dependency(id, mandatory, parseDependencyOrdering(ordering)) );
        }

        return new ModInfo(modId, name, dependencies, jarPath, loadOrder);
    }

    private int parseLoadOrder(String order) {
        return switch (order.toLowerCase()) {
            case "before" -> -1;
            case "after" -> 1;
            default -> 0;
        };
    }

    private DependencyOrdering parseDependencyOrdering(String order) {
        return switch (order.toLowerCase()) {
            case "before" -> DependencyOrdering.BEFORE;
            case "after" -> DependencyOrdering.AFTER;
            default -> DependencyOrdering.NONE;
        };
    }

    private List<ModInfo> sortModsByDependencies(List<ModInfo> mods) {
        DirectedAcyclicGraph<ModInfo, DefaultEdge> graph = new DirectedAcyclicGraph<>(DefaultEdge.class);

        for (ModInfo mod : mods) {
            graph.addVertex(mod);
        }
        
        for (ModInfo mod : mods) {
            for (Dependency dep : mod.getDependencies()) {
                ModInfo targetMod = modIdMap.get(dep.getModId());
                if (targetMod != null) {
                    try {
                        switch (dep.getOrdering()) {
                            case BEFORE -> graph.addEdge(mod, targetMod);
                            case AFTER -> graph.addEdge(targetMod, mod);
                            case NONE -> {
                                if (dep.isMandatory()) {
                                    graph.addEdge(targetMod, mod);
                                }
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        log.warn("Cycle detected in mod dependencies: {} -> {}", mod.getModId(), targetMod.getModId());
                    }
                } else if (dep.isMandatory()) {
                    log.warn("Missing mandatory dependency: {} requires {}", mod.getModId(), dep.getModId());
                }
            }
        }
        
        TopologicalOrderIterator<ModInfo, DefaultEdge> iterator = new TopologicalOrderIterator<>(graph);
        
        List<ModInfo> sortedMods = new ArrayList<>();
        while (iterator.hasNext()) {
            sortedMods.add(iterator.next());
        }

        sortedMods.sort(Comparator.comparingInt(ModInfo::getLoadOrder).thenComparing(ModInfo::getModId));
        return sortedMods;
    }
    
    private void loadSortedMods(List<ModInfo> sortedMods) {
        for (ModInfo mod : sortedMods) {
            try {
                log.info("Loading mod: {} - {}", mod.getModId(), mod.getName());
                loadedMods.add(mod);
            } catch (Exception e) {
                log.error("Failed to load mod: {}", mod.getModId(), e);
            }
        }
    }

    public List<ModInfo> getLoadedMods() {
        return Collections.unmodifiableList(loadedMods);
    }

    public ModInfo getMod(String modId) {
        return modIdMap.get(modId);
    }
}