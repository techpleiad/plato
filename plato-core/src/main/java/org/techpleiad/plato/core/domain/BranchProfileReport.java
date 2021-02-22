package org.techpleiad.plato.core.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
@Builder
@Getter
public class BranchProfileReport {
    private String profile;
    private boolean fileEqual;
    private Boolean propertyValueEqual;
    private List<Document> documents;
}
