package org.techpleiad.plato.core.port.in;

import org.techpleiad.plato.core.domain.ConsistencyLevelAcrossBranchesReport;
import org.techpleiad.plato.core.domain.ServiceSpec;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface IValidateAcrossBranchConsistencyLevelUseCase {
    List<ConsistencyLevelAcrossBranchesReport> validateAcrossBranchesConsistencyLevelServiceBatch(
            List<ServiceSpec> serviceSpecList,
            boolean isPropertyValueEqual,
            String targetBranch
    ) throws ExecutionException, InterruptedException;
}
