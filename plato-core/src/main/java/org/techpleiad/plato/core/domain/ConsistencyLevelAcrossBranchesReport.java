package org.techpleiad.plato.core.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
@Builder
@Getter
public class ConsistencyLevelAcrossBranchesReport {
    private final String service;
    private final List<BranchReport> branchReports;
}
