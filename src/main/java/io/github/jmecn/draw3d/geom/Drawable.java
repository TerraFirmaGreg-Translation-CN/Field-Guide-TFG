package io.github.jmecn.draw3d.geom;

import io.github.jmecn.draw3d.renderer.ImageRaster;

/**
 * 代表一个可渲染物体。
 * 
 * @author yanmaoyuan
 *
 */
public interface Drawable {

    public void draw(ImageRaster imageRaster);
    
}
