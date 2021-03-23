package org.techpleiad.plato.api.response;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Builder
@Getter
@ToString
public class ConsistencyLevelValidateResponseTO {
    String service;
    List<BranchReportResponseTO> branchReports;
}
