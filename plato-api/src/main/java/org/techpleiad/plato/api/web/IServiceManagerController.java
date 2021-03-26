package org.techpleiad.plato.api.web;

import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.techpleiad.plato.api.constant.Constants;
import org.techpleiad.plato.api.request.ServiceRequestTO;
import org.techpleiad.plato.api.response.ServiceResponseTO;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

public interface IServiceManagerController {

    @ApiOperation("Get all service")
    @GetMapping(value = Constants.VERSION_SERVICES)
    ResponseEntity<List<ServiceResponseTO>> getServices();

    @ApiOperation("Get service by name")
    @GetMapping(value = Constants.VERSION_SERVICES + "/{serviceName}")
    ResponseEntity<ServiceResponseTO> getServicesByName(@PathVariable String serviceName);

    @ApiOperation("Delete service by name")
    @DeleteMapping(Constants.VERSION_SERVICES + "/{serviceName}")
    ResponseEntity deleteService(@PathVariable String serviceName) throws IOException;

    @ApiOperation("Add new service")
    @PostMapping(Constants.VERSION_SERVICES)
    ResponseEntity addService(@Valid @RequestBody ServiceRequestTO serviceRequestTO) throws Exception;


}
