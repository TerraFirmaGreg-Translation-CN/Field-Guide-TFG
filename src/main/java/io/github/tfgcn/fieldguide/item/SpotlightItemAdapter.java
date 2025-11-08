package io.github.tfgcn.fieldguide.item;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * // vanilla format, single item
 * {
 *   "type": "patchouli:spotlight",
 *   "item": {
 *     "item": "minecraft:diamond_sword"
 *   }
 * }
 *
 * // vanilla format, tag
 * {
 *   "type": "patchouli:spotlight",
 *   "item": {
 *     "tag": "minecraft:axes"
 *   }
 * }
 *
 * // patchouli string format, allowing interspersed items and tags
 * {
 *   "type": "patchouli:spotlight",
 *   "item": "minecraft:diamond_sword,tag:minecraft:axes,minecraft:diamond_shovel"
 * }
 *
 * @author yanmaoyuan
 */
public class SpotlightItemAdapter extends TypeAdapter<List<SpotlightItem>> {
    @Override
    public void write(JsonWriter out, List<SpotlightItem> value) {
        throw new UnsupportedOperationException("Serialization is not supported");
    }

    @Override
    public List<SpotlightItem> read(JsonReader in) throws IOException {
        List<SpotlightItem> list = new ArrayList<>();

        switch (in.peek()) {
            case BEGIN_OBJECT:
                // 读取一个对象，这个对象应该有一个键，要么是"item"，要么是"tag"
                in.beginObject();
                while (in.hasNext()) {
                    String name = in.nextName();
                    if ("item".equals(name)) {
                        String itemId = in.nextString();
                        list.add(new SpotlightItem("item", itemId));
                    } else if ("tag".equals(name)) {
                        String tagId = in.nextString();
                        list.add(new SpotlightItem("tag", tagId));
                    } else {
                        in.skipValue();
                    }
                }
                in.endObject();
                break;
            case STRING:
                // 读取字符串，然后按逗号分割
                String[] parts = in.nextString().split(",");
                for (String part : parts) {
                    part = part.trim();
                    if (part.startsWith("tag:")) {
                        list.add(new SpotlightItem("tag", part.substring(4)));
                    } else {
                        list.add(new SpotlightItem("item", part));
                    }
                }
                break;
            default:
                in.skipValue();
                break;
        }
        return list;
    }
}
