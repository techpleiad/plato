package org.techpleiad.plato.api.web;

import io.swagger.annotations.ApiOperation;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.techpleiad.plato.api.constant.Constants;
import org.techpleiad.plato.api.request.ResolveInconsistencyRequestTO;

import javax.validation.Valid;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface IResolveInconsistencyController {

    @ApiOperation("Create Merge Request for resolving inconsistency")
    @PostMapping(Constants.VERSION_SERVICES + Constants.RESOLVE_INCONSISTENCY_ACROSS_PROFILES)
    ResponseEntity resolveInconsistencyAcrossProfiles(@Valid @RequestBody ResolveInconsistencyRequestTO resolveInconsistencyRequestTO) throws ExecutionException, InterruptedException, GitAPIException, IOException;

}
