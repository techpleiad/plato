package org.techpleiad.plato.core.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.util.Pair;

import java.util.List;

@ToString
@Builder
@Getter
public class BranchProfileReport {
    private final String profile;
    private final boolean fileEqual;
    private final Boolean propertyValueEqual;
    private final List<Pair<String, String>> propertyValuePair;
    private final List<Document> documents;
}
