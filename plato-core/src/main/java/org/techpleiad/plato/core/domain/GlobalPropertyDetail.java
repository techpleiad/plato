package org.techpleiad.plato.core.domain;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GlobalPropertyDetail {
    private JsonNode rootNode;
    private String prefix;
    private boolean isPropertyDefaultArray;
    private PropertyTreeNode alteredPropertyRoot;
}
