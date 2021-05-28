package org.techpleiad.plato.core.domain;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Builder
@Getter
public class BranchReport {
    private final String fromBranch;
    private final String toBranch;
    private final ConsistencyAcrossBranchesReport consistencyAcrossBranchesReport;
}
