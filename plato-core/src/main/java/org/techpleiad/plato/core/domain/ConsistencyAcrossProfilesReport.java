package org.techpleiad.plato.core.domain;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@ToString
@Builder
@Getter
public class ConsistencyAcrossProfilesReport {

    @Builder.Default
    private final HashMap<String, List<String>> missingProperty = new HashMap<>();
    @Builder.Default
    private final HashMap<String, JsonNode> profileDocument = new HashMap<>();

    @Setter
    private String service;

    public void addProfileToMissingProperties(final String profile, final List<String> missingProperties) {
        missingProperty.put(profile, missingProperties);
    }

    public void addProfileDocument(final String serviceProfile, final JsonNode document) {
        missingProperty.put(serviceProfile, new LinkedList<>());
        profileDocument.put(serviceProfile, document);
    }
}
