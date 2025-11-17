package io.github.tfgcn.fieldguide.data.mc.blockstate;

import lombok.Data;

@Data
public class ModelApply {
    public String model;
    public int x;
    public int y;
    public Boolean uvlock;
}