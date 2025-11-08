package io.github.tfgcn.fieldguide.asset;

import lombok.Getter;

@Getter
public enum SourceType {
    KUBE_JS(100),
    MOD_JAR(10);

    private final int priority;
    
    SourceType(int priority) {
        this.priority = priority;
    }
}