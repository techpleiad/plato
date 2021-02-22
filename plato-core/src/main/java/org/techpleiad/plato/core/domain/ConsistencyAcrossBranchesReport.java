package org.techpleiad.plato.core.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Builder
@Getter
@ToString
public class ConsistencyAcrossBranchesReport {
    private String service;
    List<BranchProfileReport> report;
}
