package io.github.tfgcn.fieldguide.item;

import lombok.Data;

@Data
public class ItemImageResult {
    private final String path;
    private final String name;
    
    public ItemImageResult(String path, String name) {
        this.path = path;
        this.name = name;
    }
    
    /**
     * 获取路径（用于向后兼容）
     */
    public String getPath() {
        return path;
    }
    
    /**
     * 获取名称（用于向后兼容）
     */
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return String.format("ItemImage[path=%s, name=%s]", path, name);
    }
}