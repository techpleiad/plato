package org.techpleiad.plato.core.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ConsistencyAcrossBranchesReport {
    private String service;
    List<BranchProfileReport> report;
}
