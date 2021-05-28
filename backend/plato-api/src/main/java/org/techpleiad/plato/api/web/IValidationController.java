package org.techpleiad.plato.api.web;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.techpleiad.plato.api.constant.Constants;
import org.techpleiad.plato.api.request.ServicesAcrossBranchValidateRequestTO;
import org.techpleiad.plato.api.request.ServicesAcrossProfileValidateRequestTO;
import org.techpleiad.plato.api.request.ServicesConsistencyLevelAcrossBranchValidateRequestTO;

import javax.validation.Valid;
import java.util.concurrent.ExecutionException;

public interface IValidationController {

    @ApiOperation("Validate across profiles")
    @PostMapping(Constants.VERSION_SERVICES_BRANCHES + "/{branchName}" + Constants.ACROSS_PROFILES_VALIDATE)
    ResponseEntity validateAcrossProfiles(@Valid @RequestBody ServicesAcrossProfileValidateRequestTO servicesAcrossProfileValidateRequestTO,
                                          @Valid @PathVariable String branchName) throws ExecutionException, InterruptedException;

    @ApiOperation("Validate across branches")
    @PostMapping(Constants.VERSION_SERVICES_BRANCHES + Constants.ACROSS_BRANCHES_VALIDATE)
    ResponseEntity validateAcrossBranches(@Valid @RequestBody ServicesAcrossBranchValidateRequestTO servicesAcrossBranchValidateRequestTO) throws ExecutionException, InterruptedException;

    @PostMapping(Constants.VERSION_SERVICES_BRANCHES + Constants.CONSISTENCY_LEVEL_VALIDATE)
    ResponseEntity validateAcrossBranchesConsistencyLevel(@Valid @RequestBody ServicesConsistencyLevelAcrossBranchValidateRequestTO servicesConsistencyLevelAcrossBranchValidateRequestTO) throws ExecutionException, InterruptedException;
}
