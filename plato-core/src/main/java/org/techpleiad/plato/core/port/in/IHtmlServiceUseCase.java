package org.techpleiad.plato.core.port.in;

import org.techpleiad.plato.core.domain.ConsistencyAcrossBranchesReport;
import org.techpleiad.plato.core.domain.ConsistencyAcrossProfilesReport;

import java.util.List;

public interface IHtmlServiceUseCase {

    String createBranchReportMailBody(List<ConsistencyAcrossBranchesReport> reportList, String fromBranch, String toBranch);

    String createProfileReportMailBody(List<ConsistencyAcrossProfilesReport> reportList, String branchName);
}