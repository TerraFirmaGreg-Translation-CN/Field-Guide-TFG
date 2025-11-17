package io.github.tfgcn.fieldguide.data.patchouli;

import org.junit.jupiter.api.Test;

/**
 * desc:
 *
 * @author yanmaoyuan
 */
public class ItemStackParserTest {

    @Test
    void testTag() {
        ItemStackParser.splitStacksFromSerializedIngredient("tag:forge/ingot,gtcet:copper_ingot");
        ItemStackParser.splitStacksFromSerializedIngredient("tfc:ceramic/ingot_mold{tank:{\"Amount\":100,\"FluidName\":\"tfc:metal/copper\"}}");
    }
}
