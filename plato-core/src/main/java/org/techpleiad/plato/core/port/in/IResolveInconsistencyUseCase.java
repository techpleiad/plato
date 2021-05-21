package org.techpleiad.plato.core.port.in;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.techpleiad.plato.core.domain.ConsistencyAcrossProfilesReport;
import org.techpleiad.plato.core.domain.ServiceSpec;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface IResolveInconsistencyUseCase {
    String resolveInconsistencyAcrossProfiles(ServiceSpec serviceSpec, String branchName, ConsistencyAcrossProfilesReport updatedFilesReport) throws ExecutionException, InterruptedException, GitAPIException, IOException;
}
