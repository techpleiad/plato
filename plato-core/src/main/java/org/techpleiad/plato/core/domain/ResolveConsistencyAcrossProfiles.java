package org.techpleiad.plato.core.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

@Builder
@Setter
@Getter
public class ResolveConsistencyAcrossProfiles {
    @Builder.Default
    private final HashMap<String, String> profileDocument = new HashMap<>();
    private String service;
}
