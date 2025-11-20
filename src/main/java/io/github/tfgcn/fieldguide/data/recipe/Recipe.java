package io.github.tfgcn.fieldguide.data.recipe;

import lombok.Data;

// 基础配方接口
public interface Recipe {
    String getType();
    String getId();
}