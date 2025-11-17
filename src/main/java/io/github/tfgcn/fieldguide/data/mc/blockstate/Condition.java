package io.github.tfgcn.fieldguide.data.mc.blockstate;

import java.util.Map;

public interface Condition {
    boolean check(Map<String, String> properties);
}