package org.techpleiad.plato.core.port.in;

import org.techpleiad.plato.core.domain.ServiceSpec;

import java.util.List;

public interface IValidateBranchUseCase {

    boolean validateBranchesInService(ServiceSpec serviceSpec, List<String> branchList);

    boolean validateBranchInService(ServiceSpec serviceSpec, String branch);

}
