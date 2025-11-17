package io.github.tfgcn.fieldguide.data.tfc.page;

import com.google.gson.annotations.JsonAdapter;
import io.github.tfgcn.fieldguide.gson.TFCMultiblockDataAdapter;
import io.github.tfgcn.fieldguide.data.patchouli.page.PageMultiblockData;
import lombok.Data;

@Data
@JsonAdapter(TFCMultiblockDataAdapter.class)
public class TFCMultiblockData extends PageMultiblockData {
    private String multiblockId;

    public TFCMultiblockData() {
    }

    public TFCMultiblockData(String multiblockId) {
        this.multiblockId = multiblockId;
    }
}