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
import org.techpleiad.plato.core.domain.RuleScope;
import org.techpleiad.plato.core.domain.ValidationRule;
import org.techpleiad.plato.core.exceptions.ValidationRuleAlreadyExistsException;
import org.techpleiad.plato.core.port.out.IValidationRulePersistencePort;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;


@ExtendWith(MockitoExtension.class)
class ValidationRuleManagerTest {

    @InjectMocks
    private ValidationRuleManager validationRuleManager;

    @Mock
    private IValidationRulePersistencePort validationRulePersistencePort;

    ValidationRuleManagerTest() throws JsonProcessingException {
    }


    @Test
    void givenExistingRules_whenAddRule_thenThrowsValidationRuleAlreadyExistsException() throws JsonProcessingException {

//      ---------------------------------------------
//      1        
        final ValidationRule existingRule1 = ValidationRule.builder()
                .ruleOnProperty("mongo.property")
                .rule(new ObjectMapper().readTree("{\"name\":\"John\", \"age\":31, \"city\":\"New York\"}"))
                .scope(
                        RuleScope.builder()
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
                        RuleScope.builder()
                                .services(new HashSet<>(Arrays.asList("RM", "DM")))
                                .branches(new HashSet<>(Collections.singletonList("dev")))
                                .profiles(new HashSet<>(Collections.singletonList("prod")))
                                .build()
                )
                .build();


        Mockito.when(validationRulePersistencePort.findExistingValidationRuleByScopeAndRuleOnProperty(Mockito.any())).thenReturn(Arrays.asList(existingRule1));

        Assertions.assertThrows(ValidationRuleAlreadyExistsException.class, () -> {
            validationRuleManager.addValidationRule(newRule1);
        });

//      ---------------------------------------------
//      2     
        final ValidationRule newRule2 = ValidationRule.builder()
                .ruleOnProperty("mongo.property")
                .rule(new ObjectMapper().readTree("{\"name\":\"John\", \"age\":31, \"city\":\"New York\"}"))
                .scope(
                        RuleScope.builder()
                                .services(new HashSet<>(Collections.singletonList("RM")))
                                .branches(new HashSet<>(Collections.singletonList("dev")))
                                .profiles(new HashSet<>(Collections.singletonList("prod")))
                                .build()
                )
                .build();
//
//        Assertions.assertDoesNotThrow(() -> {
//            validationRuleManager.addValidationRule(newRule1);
//        });

//      ---------------------------------------------
//      3
//        Mockito.when(validationRulePersistencePort.findExistingValidationRuleByScopeAndRuleOnProperty(Mockito.any())).thenReturn(Arrays.asList(newRule2));
//
//        Assertions.assertDoesNotThrow(() -> {
//            validationRuleManager.addValidationRule(newRule1);
//        });

//      ---------------------------------------------
//      4
        Mockito.when(validationRulePersistencePort.findExistingValidationRuleByScopeAndRuleOnProperty(Mockito.any())).thenReturn(Arrays.asList(existingRule1));

        final ValidationRule newRule3 = ValidationRule.builder()
                .ruleOnProperty("mongo.property")
                .rule(new ObjectMapper().readTree("{\"name\":\"John\", \"age\":31, \"city\":\"New York\"}"))
                .scope(
                        RuleScope.builder()
                                .services(new HashSet<>(Collections.singletonList("RM")))
                                .branches(new HashSet<>())
                                .profiles(new HashSet<>(Collections.singletonList("prod")))
                                .build()
                )
                .build();

        Assertions.assertDoesNotThrow(() -> {
            validationRuleManager.addValidationRule(newRule3);
        });

//      ---------------------------------------------
//      5
        final ValidationRule existingRule2 = ValidationRule.builder()
                .ruleOnProperty("mongo.property")
                .rule(new ObjectMapper().readTree("{\"name\":\"John\", \"age\":31, \"city\":\"New York\"}"))
                .scope(
                        RuleScope.builder()
                                .services(new HashSet<>())
                                .branches(new HashSet<>())
                                .profiles(new HashSet<>())
                                .build()
                )
                .build();
        Mockito.when(validationRulePersistencePort.findExistingValidationRuleByScopeAndRuleOnProperty(Mockito.any())).thenReturn(Arrays.asList(existingRule2));

        Assertions.assertThrows(ValidationRuleAlreadyExistsException.class, () -> {
            validationRuleManager.addValidationRule(existingRule2);
        });

//      ---------------------------------------------
//      6
        final ValidationRule newRule4 = ValidationRule.builder()
                .ruleOnProperty("mongo.property")
                .rule(new ObjectMapper().readTree("{\"name\":\"John\", \"age\":31, \"city\":\"New York\"}"))
                .scope(
                        RuleScope.builder()
                                .services(new HashSet<>())
                                .branches(new HashSet<>(Collections.singletonList("dev")))
                                .profiles(new HashSet<>())
                                .build()
                )
                .build();

        Assertions.assertDoesNotThrow(() -> {
            validationRuleManager.addValidationRule(newRule4);
        });

//      ---------------------------------------------
//      7
        final ValidationRule newRule5 = ValidationRule.builder()
                .ruleOnProperty("mongo.property")
                .rule(new ObjectMapper().readTree("{\"name\":\"John\", \"age\":31, \"city\":\"New York\"}"))
                .scope(
                        RuleScope.builder()
                                .services(new HashSet<>())
                                .branches(new HashSet<>(Collections.singletonList("dev")))
                                .profiles(new HashSet<>(Collections.singletonList("prod")))
                                .build()
                )
                .build();

        Assertions.assertDoesNotThrow(() -> {
            validationRuleManager.addValidationRule(newRule5);
        });

//      ---------------------------------------------
//      8
        final ValidationRule newRule6 = ValidationRule.builder()
                .ruleOnProperty("mongo.property")
                .rule(new ObjectMapper().readTree("{\"name\":\"John\", \"age\":31, \"city\":\"New York\"}"))
                .scope(
                        RuleScope.builder()
                                .services(new HashSet<>(Arrays.asList("RM", "DM")))
                                .branches(new HashSet<>(Collections.singletonList("dev")))
                                .profiles(new HashSet<>())
                                .build()
                )
                .build();

        Assertions.assertDoesNotThrow(() -> {
            validationRuleManager.addValidationRule(newRule6);
        });
    }
}