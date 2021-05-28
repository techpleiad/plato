package org.techpleiad.plato.core.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Builder
@Getter
@ToString
public class ConsistencyAcrossBranchesReport {
    private final String service;
    private final List<BranchProfileReport> report;
}
