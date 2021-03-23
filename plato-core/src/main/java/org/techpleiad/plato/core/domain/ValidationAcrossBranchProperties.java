package org.techpleiad.plato.core.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;


@ToString
@Getter
@Builder
public class ValidationAcrossBranchProperties {
    String fromBranch;
    String toBranch;
    boolean propertyValueEqual;
}
