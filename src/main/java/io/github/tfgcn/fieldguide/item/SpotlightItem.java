package io.github.tfgcn.fieldguide.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpotlightItem {
    private String type;// tag or item
    private String id;
}
