package io.github.jmecn.draw3d.light;

import io.github.jmecn.draw3d.math.Vector4f;

/**
 * 环境光
 * @author yanmaoyuan
 *
 */
public class AmbientLight extends Light {

    public AmbientLight() {
        super();
    }

    public AmbientLight(Vector4f color) {
        super(color);
    }
    
}
