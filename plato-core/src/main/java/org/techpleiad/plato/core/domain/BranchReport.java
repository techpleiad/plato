package org.techpleiad.plato.core.domain;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Builder
@Getter
public class BranchReport {
    String fromBranch;
    String toBranch;
    ConsistencyAcrossBranchesReport consistencyAcrossBranchesReport;
}
