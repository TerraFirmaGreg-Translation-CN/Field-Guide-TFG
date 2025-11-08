package io.github.tfgcn.fieldguide;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class InternalError extends RuntimeException {
    private final boolean quiet;
    
    public InternalError(String reason, boolean quiet) {
        super(reason);
        this.quiet = quiet;
    }
    
    public InternalError(String reason) {
        this(reason, false);
    }
    
    /**
     * 记录警告日志
     */
    public void warning() {
        warning(false);
    }
    
    public void warning(boolean loud) {
        if (quiet && !loud) {
            log.debug(getMessage());
        } else {
            log.warn(getMessage());
        }
    }
    
    /**
     * 添加前缀信息
     */
    public InternalError prefix(String otherReason) {
        return new InternalError(otherReason + " : " + getMessage(), quiet);
    }
    
    @Override
    public String toString() {
        return getMessage();
    }
}