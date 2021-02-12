package org.techpleiad.plato.core.port.in;

import org.techpleiad.plato.core.domain.ConsistencyAcrossProfilesReport;
import org.techpleiad.plato.core.domain.Pair;
import org.techpleiad.plato.core.domain.ServiceSpec;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface IValidateAcrossProfileUseCase {

    ConsistencyAcrossProfilesReport validateYamlKeyInFiles(List<Pair<String, File>> mapProfileToFileContent,
                                                           Map<String, List<String>> suppressedProperties,
                                                           List<String> alteredProperties
    );

    List<ConsistencyAcrossProfilesReport> validateAcrossProfilesInServiceBatch(
            List<ServiceSpec> serviceSpecList,
            String branchName,
            boolean isSuppressed
    ) throws ExecutionException, InterruptedException;
}
