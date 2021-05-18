package org.techpleiad.plato.api.web;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.techpleiad.plato.api.constant.Constants;

import javax.validation.Valid;

public interface IResolveInconsistencyController {

    @ApiOperation("Create Merge Request for resolving inconsistency")
    @PostMapping(Constants.VERSION_SERVICES + Constants.RESOLVE_INCONSISTENCY_ACROSS_PROFILES)
    ResponseEntity resolveInconsistencyAcrossProfiles(@Valid @RequestBody);


}
