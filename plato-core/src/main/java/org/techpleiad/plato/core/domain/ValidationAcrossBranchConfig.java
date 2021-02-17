package org.techpleiad.plato.core.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;


@ToString
@Getter
@Builder
public class ValidationAcrossBranchConfig {
    String fromBranch;
    String toBranch;
    boolean propertyValueEqual;
}
