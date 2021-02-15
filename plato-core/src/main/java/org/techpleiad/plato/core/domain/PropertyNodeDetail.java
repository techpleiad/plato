package org.techpleiad.plato.core.domain;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class PropertyNodeDetail {
    private JsonNode rootNode;
    private String pathRegex;
    private boolean isPropertyArray;
    private PropertyTreeNode alteredPropertyRoot;
}
