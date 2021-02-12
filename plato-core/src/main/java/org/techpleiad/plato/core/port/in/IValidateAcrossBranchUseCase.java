package org.techpleiad.plato.core.port.in;

import org.techpleiad.plato.core.domain.ConsistencyAcrossBranchesReport;
import org.techpleiad.plato.core.domain.ServiceBranchData;
import org.techpleiad.plato.core.domain.ServiceSpec;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface IValidateAcrossBranchUseCase {

    ConsistencyAcrossBranchesReport validateProfilesAcrossBranch(
            final ServiceBranchData fromServiceBranchData,
            final ServiceBranchData toServiceBranchData,
            final ServiceSpec serviceSpec
    ) throws ExecutionException, InterruptedException;

    List<ConsistencyAcrossBranchesReport> validateAcrossBranchesInServiceBatch(
            List<ServiceSpec> serviceSpecList,
            String fromBranch, String toBranch
    ) throws ExecutionException, InterruptedException;
}
