package org.techpleiad.plato.core.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;


@ToString
@Getter
@Builder
public class ValidationAcrossBranchProperties {
    private final String fromBranch;
    private final String toBranch;
    private final boolean propertyValueEqual;
}
