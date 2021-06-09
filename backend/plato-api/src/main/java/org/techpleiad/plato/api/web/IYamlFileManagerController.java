package org.techpleiad.plato.api.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.techpleiad.plato.api.constant.Constants;

import java.util.concurrent.ExecutionException;

public interface IYamlFileManagerController {

    @ApiOperation("Get configuration file by name")
    @GetMapping(value = Constants.VERSION_SERVICES + "/{serviceName}" + Constants.BRANCHES + "/{branchName}")
    ResponseEntity getFileByName(@PathVariable String serviceName, @PathVariable String branchName, @RequestParam(defaultValue = "yaml") String format, @RequestParam(defaultValue = "individual") String type, @RequestParam(defaultValue = "") String profile) throws InterruptedException, ExecutionException, JsonProcessingException;


    @ApiOperation("Get all configuration files of service of a branch and profile")
    @GetMapping(value = Constants.VERSION_SERVICES + Constants.GET_FILES + "/{serviceName}" + Constants.BRANCHES + "/{branchName}")
    ResponseEntity getFilesByProfile(@PathVariable String serviceName, @PathVariable String branchName, @RequestParam(defaultValue = "") String profile) throws InterruptedException, ExecutionException, JsonProcessingException;
}
