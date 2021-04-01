package org.techpleiad.plato.core.domain;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class PropertyNodeDetail {
    private final JsonNode rootNode;
    private final String pathRegex;
    private final boolean isPropertyArray;
    private final PropertyTreeNode alteredPropertyRoot;
}
