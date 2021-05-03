package org.techpleiad.plato.adapter.web.in;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.techpleiad.plato.api.request.ServiceCustomValidateRequestTO;
import org.techpleiad.plato.api.response.CustomValidateResponseTO;
import org.techpleiad.plato.api.web.ICustomValidationController;
import org.techpleiad.plato.core.advice.ExecutionTime;
import org.techpleiad.plato.core.domain.ServiceSpec;
import org.techpleiad.plato.core.domain.ValidationRule;
import org.techpleiad.plato.core.port.in.ICustomValidateUseCase;
import org.techpleiad.plato.core.port.in.IGetServiceUseCase;
import org.techpleiad.plato.core.port.in.IGetValidationRuleUseCase;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@RestController
@Slf4j
public class CustomValidationController implements ICustomValidationController {

    @Autowired
    private IGetServiceUseCase getServiceUseCase;

    @Autowired
    private IGetValidationRuleUseCase getValidationRuleUseCase;

    @Autowired
    private ICustomValidateUseCase customValidateUseCase;

    @ExecutionTime
    @Override
    public ResponseEntity<List<CustomValidateResponseTO>> customValidate(@Valid ServiceCustomValidateRequestTO serviceCustomValidateRequestTO) throws ExecutionException, InterruptedException {
        ServiceSpec serviceSpec = getServiceUseCase.getService(serviceCustomValidateRequestTO.getService());
        Map<String, List<JsonNode>> yamlPropertyToJsonNodeList = customValidateUseCase
                .customValidateYamlFile(serviceSpec, serviceCustomValidateRequestTO.getService(), serviceCustomValidateRequestTO.getBranch(), serviceCustomValidateRequestTO
                        .getProfile());

        Map<String, ValidationRule> validationRuleMap = getValidationRuleUseCase
                .getValidationRuleMapByScope(serviceCustomValidateRequestTO.getService(), serviceCustomValidateRequestTO.getBranch(), serviceCustomValidateRequestTO
                        .getProfile());

        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);

        List<CustomValidateResponseTO> responseList = new ArrayList<>();

        for (Map.Entry<String, ValidationRule> validationRuleEntry : validationRuleMap.entrySet()) {
            if (yamlPropertyToJsonNodeList.get(validationRuleEntry.getKey()) != null) {
                List<JsonNode> jsonNodes = yamlPropertyToJsonNodeList.get(validationRuleEntry.getKey());
                for (JsonNode jsonNode : jsonNodes) {
                    JsonSchema schemaToValidationJsonSchema = factory.getSchema(validationRuleEntry.getValue().getRule());
                    Set<ValidationMessage> errors = schemaToValidationJsonSchema.validate(jsonNode);
                    if (!errors.isEmpty()) {
                        responseList.add(
                                CustomValidateResponseTO.builder()
                                        .property(validationRuleEntry.getKey())
                                        .value(jsonNode)
                                        .validationMessages(errors)
                                        .build()
                        );
                    }
                }
            }
        }
        return ResponseEntity.ok(responseList);
    }
}
