package org.techpleiad.plato.api.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Builder
@Getter
@ToString
public class ServicesAcrossBranchValidateResponseTO {
    private String service;
    private List<BranchProfileReportResponseTO> report;

}
