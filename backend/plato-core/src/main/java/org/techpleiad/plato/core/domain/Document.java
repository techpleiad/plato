package org.techpleiad.plato.core.domain;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Builder
@Getter
public class Document {
    private final String branch;
    private final String profile;
    private final JsonNode document;
}
