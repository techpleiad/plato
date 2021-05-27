package org.techpleiad.plato.core.port.in;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.techpleiad.plato.core.domain.ResolveConsistencyAcrossProfiles;
import org.techpleiad.plato.core.domain.ServiceSpec;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface IResolveInconsistencyUseCase {
    List<String> resolveInconsistencyAcrossProfiles(ServiceSpec serviceSpec, String branchName, ResolveConsistencyAcrossProfiles updatedFilesReport) throws ExecutionException, InterruptedException, GitAPIException, IOException;
}
