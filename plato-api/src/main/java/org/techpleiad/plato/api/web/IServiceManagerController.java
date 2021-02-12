package org.techpleiad.plato.api.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.techpleiad.plato.api.constant.Constants;
import org.techpleiad.plato.api.request.ServiceRequestTO;
import org.techpleiad.plato.api.request.ServicesAcrossBranchValidateRequestTO;
import org.techpleiad.plato.api.request.ServicesAcrossProfileValidateRequestTO;
import org.techpleiad.plato.api.response.ServiceResponseTO;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface IServiceManagerController {

    @GetMapping(value = Constants.VERSION_SERVICES)
    ResponseEntity<List<ServiceResponseTO>> getServices();

    @GetMapping(value = Constants.VERSION_SERVICES + "/{serviceName}")
    ResponseEntity<ServiceResponseTO> getServicesByName(@PathVariable String serviceName);

    @DeleteMapping(Constants.VERSION_SERVICES + "/{serviceName}")
    ResponseEntity deleteService(@PathVariable String serviceName) throws IOException;

    @PostMapping(Constants.VERSION_SERVICES)
    ResponseEntity addService(@Valid @RequestBody ServiceRequestTO serviceRequestTO) throws Exception;

    @PostMapping(Constants.VERSION_SERVICES_BRANCHES + "/{branchName}" + Constants.ACROSS_PROFILES_VALIDATE)
    ResponseEntity validateAcrossProfiles(@Valid @RequestBody ServicesAcrossProfileValidateRequestTO servicesAcrossProfileValidateRequestTO,
                                          @Valid @PathVariable String branchName) throws ExecutionException, InterruptedException;

    @PostMapping(Constants.VERSION_SERVICES_BRANCHES + Constants.ACROSS_BRANCHES_VALIDATE)
    ResponseEntity validateAcrossBranches(@Valid @RequestBody ServicesAcrossBranchValidateRequestTO servicesAcrossBranchValidateRequestTO) throws ExecutionException, InterruptedException;
}
