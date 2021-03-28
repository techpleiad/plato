package org.techpleiad.plato.api.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class BranchReportResponseTO {
    private final String fromBranch;
    private final String toBranch;
    private final ServicesAcrossBranchValidateResponseTO consistencyAcrossBranchesReport;
}
