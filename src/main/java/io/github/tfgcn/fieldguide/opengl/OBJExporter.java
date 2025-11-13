package io.github.tfgcn.fieldguide.opengl;

import io.github.tfgcn.fieldguide.asset.AssetLoader;
import io.github.tfgcn.fieldguide.mc.BlockModel;
import io.github.tfgcn.fieldguide.mc.ElementFace;
import io.github.tfgcn.fieldguide.mc.ModelElement;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class OBJExporter {

    private final float SCALE_FACTOR = 1.0f / 16.0f; // Minecraft 坐标到标准坐标的缩放

    private AssetLoader loader;

    public OBJExporter(AssetLoader loader) {
        this.loader = loader;
    }

    /**
     * 将 BlockModel 导出为 OBJ 文件
     */
    public void exportToOBJ(String modeId, String filePath) throws IOException {
        BlockModel model = loader.loadModel(modeId);
        exportToOBJ(model, filePath);
    }

    /**
     * 将 BlockModel 导出为 OBJ 文件
     */
    public void exportToOBJ(BlockModel model, String filePath) throws IOException {
        exportToOBJ(model, filePath, "1");
    }

    /**
     * 将 BlockModel 导出为 OBJ 文件，指定材质名称
     */
    public void exportToOBJ(BlockModel model, String filePath, String materialName) throws IOException {
        StringBuilder objContent = new StringBuilder();
        StringBuilder mtlContent = new StringBuilder();

        // OBJ 文件头
        objContent.append("# Exported from Minecraft BlockModel\n");
        objContent.append("# Vertices, UVs and Faces\n\n");

        // 材质库引用
        objContent.append("mtllib ").append(materialName).append(".mtl\n\n");

        List<Float> vertices = new ArrayList<>();
        List<Float> texCoords = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        // 处理所有元素
        if (model.getElements() != null) {
            for (ModelElement element : model.getElements()) {
                processElement(element, vertices, texCoords, indices);
            }
        }

        // 写入顶点
        objContent.append("# Vertices\n");
        for (int i = 0; i < vertices.size(); i += 3) {
            objContent.append(String.format("v %.6f %.6f %.6f\n",
                    vertices.get(i), vertices.get(i+1), vertices.get(i+2)));
        }

        // 写入纹理坐标
        objContent.append("\n# Texture Coordinates\n");
        for (int i = 0; i < texCoords.size(); i += 2) {
            objContent.append(String.format("vt %.6f %.6f\n",
                    texCoords.get(i), texCoords.get(i)));
        }

        // 写入法线（简化版本，使用面法线）
        objContent.append("\n# Normals\n");
        objContent.append("vn 0.000000 1.000000 0.000000\n");  // up
        objContent.append("vn 0.000000 -1.000000 0.000000\n"); // down
        objContent.append("vn 0.000000 0.000000 -1.000000\n");  // north
        objContent.append("vn 0.000000 0.000000 1.000000\n"); // south
        objContent.append("vn 1.000000 0.000000 0.000000\n");  // east
        objContent.append("vn -1.000000 0.000000 0.000000\n"); // west

        // 使用材质
        objContent.append("\nusemtl ").append(materialName).append("\n");

        // 写入面
        objContent.append("\n# Faces\n");
        int vertexOffset = 1; // OBJ 索引从 1 开始
        int texCoordOffset = 1;

        for (int i = 0; i < indices.size(); i += 4) {
            int v1 = indices.get(i) + vertexOffset;
            int v2 = indices.get(i+1) + vertexOffset;
            int v3 = indices.get(i+2) + vertexOffset;
            int v4 = indices.get(i+3) + vertexOffset;

            // 每个面有 4 个顶点，对应 4 个纹理坐标
            int t1 = (i/4 * 4) + texCoordOffset;
            int t2 = t1 + 1;
            int t3 = t1 + 2;
            int t4 = t1 + 3;

            // 根据面类型确定法线索引
            int normalIndex = getNormalIndexForFace(i / 16);

            // 将四边形分为两个三角形
            objContent.append(String.format("f %d/%d/%d %d/%d/%d %d/%d/%d\n",
                    v1, t1, normalIndex,
                    v2, t2, normalIndex,
                    v3, t3, normalIndex));
            objContent.append(String.format("f %d/%d/%d %d/%d/%d %d/%d/%d\n",
                    v1, t1, normalIndex,
                    v3, t3, normalIndex,
                    v4, t4, normalIndex));
        }

        // 创建 MTL 文件内容
        createMTLContent(mtlContent, materialName, model.getTextures());

        // 写入文件
        FileUtils.createParentDirectories(new File(filePath));
        Files.write(Paths.get(filePath), objContent.toString().getBytes());
        Files.write(Paths.get(filePath.replace(".obj", ".mtl")), mtlContent.toString().getBytes());
    }

    /**
     * 处理单个模型元素
     */
    private void processElement(ModelElement element, List<Float> vertices,
                                       List<Float> texCoords, List<Integer> indices) {
        double[] from = element.getFrom();
        double[] to = element.getTo();

        // 转换 Minecraft 坐标到标准坐标
        float x1 = (float) from[0] * SCALE_FACTOR;
        float y1 = (float) from[1] * SCALE_FACTOR;
        float z1 = (float) from[2] * SCALE_FACTOR;
        float x2 = (float) to[0] * SCALE_FACTOR;
        float y2 = (float) to[1] * SCALE_FACTOR;
        float z2 = (float) to[2] * SCALE_FACTOR;

        // 创建立方体的 8 个顶点
        float[][] cubeVertices = {
                {x1, y1, z1}, {x2, y1, z1}, {x2, y1, z2}, {x1, y1, z2}, // 底面
                {x1, y2, z1}, {x2, y2, z1}, {x2, y2, z2}, {x1, y2, z2}  // 顶面
        };

        // 立方体的面定义（每个面4个顶点）
        int[][] faces = {
                {0, 1, 2, 3}, // 底面 (down)
                {4, 5, 6, 7}, // 顶面 (up)
                {0, 4, 7, 3}, // 西面 (west)
                {1, 5, 6, 2}, // 东面 (east)
                {0, 1, 5, 4}, // 北面 (north)
                {3, 2, 6, 7}  // 南面 (south)
        };

        // 标准纹理坐标（每个面4个UV点，基于 Minecraft 的 4 元素 UV 数组）
        float[][][] faceUVs = {
                // 每个面有 4 个 UV 坐标，每个 UV 坐标有 2 个值
                {{0, 0}, {1, 0}, {1, 1}, {0, 1}}, // 底面
                {{0, 0}, {1, 0}, {1, 1}, {0, 1}}, // 顶面
                {{0, 0}, {1, 0}, {1, 1}, {0, 1}}, // 西面
                {{0, 0}, {1, 0}, {1, 1}, {0, 1}}, // 东面
                {{0, 0}, {1, 0}, {1, 1}, {0, 1}}, // 北面
                {{0, 0}, {1, 0}, {1, 1}, {0, 1}}  // 南面
        };

        Map<String, ElementFace> elementFaces = element.getFaces();
        if (elementFaces != null) {
            // 如果有自定义面定义，使用自定义UV
            applyCustomUVs(elementFaces, faceUVs);
        }

        // 添加顶点和面索引
        int vertexStartIndex = vertices.size() / 3;

        for (int i = 0; i < faces.length; i++) {
            int[] face = faces[i];
            float[][] uv = faceUVs[i];

            // 检查面是否应该被渲染
            if (shouldRenderFace(elementFaces, i)) {
                // 添加面的4个顶点
                for (int vertexIndex : face) {
                    float[] vertex = cubeVertices[vertexIndex];
                    vertices.add(vertex[0]);
                    vertices.add(vertex[1]);
                    vertices.add(vertex[2]);
                }

                // 添加纹理坐标（每个顶点一个UV坐标）
                for (int j = 0; j < 4; j++) {
                    texCoords.add(uv[j][0]);
                    texCoords.add(1.0f - uv[j][1]); // 翻转V坐标（Minecraft 到 OBJ 的转换）
                }

                // 添加面索引（相对于当前面的顶点）
                int baseIndex = vertexStartIndex + (i * 4);
                indices.add(baseIndex);
                indices.add(baseIndex + 1);
                indices.add(baseIndex + 2);
                indices.add(baseIndex + 3);
            }
        }
    }

    /**
     * 应用自定义UV坐标
     */
    private void applyCustomUVs(Map<String, ElementFace> elementFaces, float[][][] faceUVs) {
        String[] faceNames = {"down", "up", "west", "east", "north", "south"};

        for (int i = 0; i < faceNames.length; i++) {
            String faceName = faceNames[i];
            if (elementFaces.containsKey(faceName)) {
                ElementFace face = elementFaces.get(faceName);
                if (face.getUv() != null && face.getUv().length == 4) {
                    double[] minecraftUV = face.getUv();

                    // 将 Minecraft 的 4 元素 UV 数组转换为 4 个 UV 坐标
                    // Minecraft UV 格式: [u1, v1, u2, v2]
                    // 转换为 4 个顶点的 UV:
                    // 左下: (u1, v2)
                    // 右下: (u2, v2)
                    // 右上: (u2, v1)
                    // 左上: (u1, v1)

                    float u1 = (float) minecraftUV[0] / 16.0f;
                    float v1 = (float) minecraftUV[1] / 16.0f;
                    float u2 = (float) minecraftUV[2] / 16.0f;
                    float v2 = (float) minecraftUV[3] / 16.0f;

                    faceUVs[i] = new float[][]{
                            {u1, v2}, // 左下
                            {u2, v2}, // 右下
                            {u2, v1}, // 右上
                            {u1, v1}  // 左上
                    };
                }
            }
        }
    }

    /**
     * 检查是否应该渲染该面
     */
    private boolean shouldRenderFace(Map<String, ElementFace> elementFaces, int faceIndex) {
        if (elementFaces == null) {
            return true; // 没有面定义，渲染所有面
        }

        String[] faceNames = {"down", "up", "west", "east", "north", "south"};
        String faceName = faceNames[faceIndex];

        return elementFaces.containsKey(faceName);
    }

    /**
     * 获取面的法线索引
     */
    private int getNormalIndexForFace(int faceIndex) {
        // 法线索引对应：up=1, down=2, north=3, south=4, east=5, west=6
        return faceIndex % 6 + 1;
    }

    /**
     * 创建 MTL 文件内容
     */
    private void createMTLContent(StringBuilder mtlContent,
                                  String materialName,
                                  Map<String, String> textures) {
        mtlContent.append("# Material file for Minecraft block\n");
        mtlContent.append("newmtl ").append(materialName).append("\n");

        // 材质属性
        mtlContent.append("Ka 1.000000 1.000000 1.000000\n"); // 环境光
        mtlContent.append("Kd 1.000000 1.000000 1.000000\n"); // 漫反射
        mtlContent.append("Ks 0.000000 0.000000 0.000000\n"); // 高光
        mtlContent.append("Ns 0.000000\n");                   // 高光指数
        mtlContent.append("illum 2\n");                       // 光照模型

        // 纹理映射
        if (textures != null && !textures.isEmpty()) {
            String texturePath = getTexture("all", textures);
            if (texturePath != null) {
                mtlContent.append("map_Kd ").append("all.png").append("\n");
                try {
                    BufferedImage image = loader.loadTexture(texturePath);
                    ImageIO.write(image, "png", new File("output/all.png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取纹理路径
     */
    private String getTexturePath(Map<String, String> textures) {
        // 优先使用 "all" 纹理，否则使用第一个可用的纹理
        if (textures.containsKey("all")) {
            return getTexture("all", textures);
        }
        return null;
    }

    private String getTexture(String key, Map<String, String> textures) {
        String value = textures.get(key);
        if (value == null) {
            return null;
        }
        if (value.equals("#")) {
            String ref = value.substring(1);
            return getTexture(ref, textures);
        }
        return value;
    }

    /**
     * 转换纹理路径格式
     */
    private String convertTexturePath(String key, String minecraftPath) {
        // 将 "minecraft:block/stone" 转换为 "textures/block/stone.png"
        if (minecraftPath.contains(":")) {
            String[] parts = minecraftPath.split(":", 2);
            return "textures/" + parts[1] + ".png";
        }
        return "textures/" + minecraftPath + ".png";
    }
}