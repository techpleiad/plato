package org.techpleiad.plato.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.techpleiad.plato.core.domain.ValidationRule;
import org.techpleiad.plato.core.domain.ValidationRuleScope;
import org.techpleiad.plato.core.exceptions.ValidationRuleAlreadyExistsException;
import org.techpleiad.plato.core.port.out.IValidationRulePersistencePort;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;


@ExtendWith(MockitoExtension.class)
class ValidationRuleServiceTest {

    @InjectMocks
    private ValidationRuleService validationRuleService;

    @Mock
    private IValidationRulePersistencePort validationRulePersistencePort;

    ValidationRuleServiceTest() throws JsonProcessingException {
    }

    final ValidationRule existingRule1 = ValidationRule.builder()
            .ruleOnProperty("mongo.property")
            .rule(new ObjectMapper().readTree("{\"name\":\"John\", \"age\":31, \"city\":\"New York\"}"))
            .scope(
                    ValidationRuleScope.builder()
                            .services(new HashSet<>(Arrays.asList("RM", "DM")))
                            .branches(new HashSet<>(Collections.singletonList("dev")))
                            .profiles(new HashSet<>(Collections.singletonList("prod")))
                            .build()
            )
            .build();

    final ValidationRule newRule1 = ValidationRule.builder()
            .ruleOnProperty("mongo.property")
            .rule(new ObjectMapper().readTree("{\"name\":\"John\", \"age\":31, \"city\":\"New York\"}"))
            .scope(
                    ValidationRuleScope.builder()
                            .services(new HashSet<>(Arrays.asList("RM", "DM")))
                            .branches(new HashSet<>(Collections.singletonList("dev")))
                            .profiles(new HashSet<>(Collections.singletonList("prod")))
                            .build()
            )
            .build();

    final ValidationRule newRule2 = ValidationRule.builder()
            .ruleOnProperty("mongo.property")
            .rule(new ObjectMapper().readTree("{\"name\":\"John\", \"age\":31, \"city\":\"New York\"}"))
            .scope(
                    ValidationRuleScope.builder()
                            .services(new HashSet<>(Collections.singletonList("RM")))
                            .branches(new HashSet<>(Collections.singletonList("dev")))
                            .profiles(new HashSet<>(Collections.singletonList("prod")))
                            .build()
            )
            .build();

    final ValidationRule newRule3 = ValidationRule.builder()
            .ruleOnProperty("mongo.property")
            .rule(new ObjectMapper().readTree("{\"name\":\"John\", \"age\":31, \"city\":\"New York\"}"))
            .scope(
                    ValidationRuleScope.builder()
                            .services(new HashSet<>(Collections.singletonList("RM")))
                            .branches(new HashSet<>())
                            .profiles(new HashSet<>(Collections.singletonList("prod")))
                            .build()
            )
            .build();

    final ValidationRule existingRule2 = ValidationRule.builder()
            .ruleOnProperty("mongo.property")
            .rule(new ObjectMapper().readTree("{\"name\":\"John\", \"age\":31, \"city\":\"New York\"}"))
            .scope(
                    ValidationRuleScope.builder()
                            .services(new HashSet<>())
                            .branches(new HashSet<>())
                            .profiles(new HashSet<>())
                            .build()
            ).build();

    final ValidationRule newRule4 = ValidationRule.builder()
            .ruleOnProperty("mongo.property")
            .rule(new ObjectMapper().readTree("{\"name\":\"John\", \"age\":31, \"city\":\"New York\"}"))
            .scope(
                    ValidationRuleScope.builder()
                            .services(new HashSet<>())
                            .branches(new HashSet<>(Collections.singletonList("dev")))
                            .profiles(new HashSet<>())
                            .build()
            )
            .build();

    final ValidationRule newRule6 = ValidationRule.builder()
            .ruleOnProperty("mongo.property")
            .rule(new ObjectMapper().readTree("{\"name\":\"John\", \"age\":31, \"city\":\"New York\"}"))
            .scope(
                    ValidationRuleScope.builder()
                            .services(new HashSet<>(Arrays.asList("RM", "DM")))
                            .branches(new HashSet<>(Collections.singletonList("dev")))
                            .profiles(new HashSet<>())
                            .build()
            )
            .build();

    final ValidationRule newRule5 = ValidationRule.builder()
            .ruleOnProperty("mongo.property")
            .rule(new ObjectMapper().readTree("{\"name\":\"John\", \"age\":31, \"city\":\"New York\"}"))
            .scope(
                    ValidationRuleScope.builder()
                            .services(new HashSet<>())
                            .branches(new HashSet<>(Collections.singletonList("dev")))
                            .profiles(new HashSet<>(Collections.singletonList("prod")))
                            .build()
            )
            .build();

    final ValidationRule existingRule3 = ValidationRule.builder()
            .ruleOnProperty("mongo.property")
            .rule(new ObjectMapper().readTree("{\"name\":\"John\", \"age\":31, \"city\":\"New York\"}"))
            .scope(
                    ValidationRuleScope.builder()
                            .services(new HashSet<>(Arrays.asList("DM", "RM")))
                            .branches(new HashSet<>(Arrays.asList("dev", "prod")))
                            .profiles(new HashSet<>(Arrays.asList("dev", "test")))
                            .build()
            )
            .build();

    final ValidationRule newRule7 = ValidationRule.builder()
            .ruleOnProperty("mongo.property")
            .rule(new ObjectMapper().readTree("{\"name\":\"John\", \"age\":31, \"city\":\"New York\"}"))
            .scope(
                    ValidationRuleScope.builder()
                            .services(new HashSet<>(Arrays.asList("DM", "RM")))
                            .branches(new HashSet<>(Arrays.asList("dev", "test")))
                            .profiles(new HashSet<>(Arrays.asList("dev", "test")))
                            .build()
            )
            .build();

    final ValidationRule existingRule4 = ValidationRule.builder()
            .ruleOnProperty("mongo.property")
            .rule(new ObjectMapper().readTree("{\"name\":\"John\", \"age\":31, \"city\":\"New York\"}"))
            .scope(
                    ValidationRuleScope.builder()
                            .services(new HashSet<>(Arrays.asList("DM", "RM")))
                            .branches(new HashSet<>(Arrays.asList("dev", "prod")))
                            .profiles(new HashSet<>(Arrays.asList("dev", "test")))
                            .build()
            )
            .build();

    final ValidationRule newRule8 = ValidationRule.builder()
            .ruleOnProperty("mongo.property")
            .rule(new ObjectMapper().readTree("{\"name\":\"John\", \"age\":31, \"city\":\"New York\"}"))
            .scope(
                    ValidationRuleScope.builder()
                            .services(new HashSet<>(Arrays.asList("DM", "RM")))
                            .branches(new HashSet<>(Arrays.asList("uat", "test")))
                            .profiles(new HashSet<>(Arrays.asList("dev", "test")))
                            .build()
            )
            .build();


    @Test
    void givenExistingRules_whenAddRule_thenThrowsValidationRuleAlreadyExistsException_1() {


        Mockito.when(validationRulePersistencePort.findExistingValidationRuleByScopeAndRuleOnProperty(Mockito.any())).thenReturn(Collections.singletonList(existingRule1));

        Assertions.assertThrows(ValidationRuleAlreadyExistsException.class, () -> validationRuleService.addValidationRule(newRule1));
    }

    @Test
    void givenExistingRules_whenAddRule_thenThrowsValidationRuleAlreadyExistsException_2() {

        Mockito.when(validationRulePersistencePort.findExistingValidationRuleByScopeAndRuleOnProperty(Mockito.any())).thenReturn(Collections.singletonList(existingRule1));

        Assertions.assertThrows(ValidationRuleAlreadyExistsException.class, () -> validationRuleService.addValidationRule(newRule2));
    }

    @Test
    void givenExistingRules_whenAddRule_thenThrowsValidationRuleAlreadyExistsException_3() {

        Mockito.when(validationRulePersistencePort.findExistingValidationRuleByScopeAndRuleOnProperty(Mockito.any())).thenReturn(Collections.singletonList(newRule2));

        Assertions.assertThrows(ValidationRuleAlreadyExistsException.class, () -> validationRuleService.addValidationRule(newRule1));
    }

    @Test
    void givenExistingRules_whenAddRule_thenDoesNotThrowsValidationRuleAlreadyExistsException_1() {

        Mockito.when(validationRulePersistencePort.findExistingValidationRuleByScopeAndRuleOnProperty(Mockito.any())).thenReturn(Collections.singletonList(existingRule1));

        Assertions.assertDoesNotThrow(() -> {
            validationRuleService.addValidationRule(newRule3);
        });
    }

    @Test
    void givenExistingRules_whenAddRule_thenThrowsValidationRuleAlreadyExistsException_4() {

        Mockito.when(validationRulePersistencePort.findExistingValidationRuleByScopeAndRuleOnProperty(Mockito.any())).thenReturn(Collections.singletonList(existingRule2));

        Assertions.assertThrows(ValidationRuleAlreadyExistsException.class, () -> validationRuleService.addValidationRule(existingRule2));
    }

    @Test
    void givenExistingRules_whenAddRule_thenDoesNotThrowsValidationRuleAlreadyExistsException_2() {

        Mockito.when(validationRulePersistencePort.findExistingValidationRuleByScopeAndRuleOnProperty(Mockito.any())).thenReturn(Collections.singletonList(existingRule2));

        Assertions.assertDoesNotThrow(() -> {
            validationRuleService.addValidationRule(newRule4);
        });
    }

    @Test
    void givenExistingRules_whenAddRule_thenDoesNotThrowsValidationRuleAlreadyExistsException_3() {

        Mockito.when(validationRulePersistencePort.findExistingValidationRuleByScopeAndRuleOnProperty(Mockito.any())).thenReturn(Collections.singletonList(existingRule2));

        Assertions.assertDoesNotThrow(() -> {
            validationRuleService.addValidationRule(newRule5);
        });
    }

    @Test
    void givenExistingRules_whenAddRule_thenDoesNotThrowsValidationRuleAlreadyExistsException_4() {

        Mockito.when(validationRulePersistencePort.findExistingValidationRuleByScopeAndRuleOnProperty(Mockito.any())).thenReturn(Collections.singletonList(existingRule2));

        Assertions.assertDoesNotThrow(() -> {
            validationRuleService.addValidationRule(newRule6);
        });
    }

    @Test
    void givenExistingRules_whenAddRule_thenThrowsValidationRuleAlreadyExistsException_5() {

        Mockito.when(validationRulePersistencePort.findExistingValidationRuleByScopeAndRuleOnProperty(Mockito.any())).thenReturn(Collections.singletonList(existingRule3));

        Assertions.assertThrows(ValidationRuleAlreadyExistsException.class, () -> validationRuleService.addValidationRule(newRule7));
    }

    @Test
    void givenExistingRules_whenAddRule_thenDoesNotThrowsValidationRuleAlreadyExistsException_5() {

        Mockito.when(validationRulePersistencePort.findExistingValidationRuleByScopeAndRuleOnProperty(Mockito.any())).thenReturn(Collections.singletonList(existingRule4));

        Assertions.assertDoesNotThrow(() -> validationRuleService.addValidationRule(newRule8));
    }


}