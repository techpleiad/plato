package org.techpleiad.plato.adapter.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.techpleiad.plato.adapter.domain.ValidationRuleEntity;
import org.techpleiad.plato.api.request.RuleScopeRequestTO;
import org.techpleiad.plato.api.request.ValidationRuleRequestTO;
import org.techpleiad.plato.core.domain.ValidationRule;
import org.techpleiad.plato.core.domain.ValidationRuleScope;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

class ValidationRuleMapperTest {

    private ValidationRuleMapper validationRuleMapper = Mappers.getMapper(ValidationRuleMapper.class);

    final RuleScopeRequestTO ruleScopeRequestTO_1 = RuleScopeRequestTO.builder()
            .services(new HashSet<>(Arrays.asList("custom-manger", "service-manager")))
            .branches(new HashSet<>(Arrays.asList("dev", "local")))
            .profiles(new HashSet<>(Arrays.asList("dev", "local")))
            .build();

    final ValidationRuleScope validationRuleScope = ValidationRuleScope.builder()
            .services(new HashSet<>(Collections.singletonList("device-manger")))
            .branches(new HashSet<>(Arrays.asList("main", "prod")))
            .profiles(new HashSet<>(Arrays.asList("dev", "local")))
            .build();

    final String json = "{ \"f1\" : \"v1\" } ";

    ObjectMapper objectMapper = new ObjectMapper();


    final ValidationRuleRequestTO validationRuleRequestTO = ValidationRuleRequestTO.builder()
            .ruleOnProperty("mongo.spring.data")
            .rule(objectMapper.readTree(json))
            .scope(ruleScopeRequestTO_1)
            .build();

    final ValidationRule validationRule = ValidationRule.builder()
            .ruleOnProperty("mongo.spring.data")
            .rule(objectMapper.readTree(json))
            .scope(validationRuleScope)
            .build();

    final ValidationRuleEntity validationRuleEntity = ValidationRuleEntity.builder()
            .ruleOnProperty("mongo.spring.data")
            .rule(json)
            .scope(validationRuleScope)
            .build();

    ValidationRuleMapperTest() throws JsonProcessingException {
    }

    @Test
    void convertValidationRuleEntityListToValidationRuleList() {
    }

    @Test
    void convertValidationRuleRequestTOtoValidationRule() {
        ValidationRule validationRule = validationRuleMapper.convertValidationRuleRequestTOtoValidationRule(validationRuleRequestTO);
        Assertions.assertEquals(validationRule.getRule(), validationRuleRequestTO.getRule());
        Assertions.assertNull(validationRuleMapper.convertValidationRuleRequestTOtoValidationRule(null));
    }

    @Test
    void convertRuleScopeRequestToRuleScope() {
        ValidationRuleScope validationRuleScope = validationRuleMapper.convertRuleScopeRequestToRuleScope(ruleScopeRequestTO_1);
        Assertions.assertEquals(ruleScopeRequestTO_1.getProfiles(), validationRuleScope.getProfiles());
        Assertions.assertNull(validationRuleMapper.convertRuleScopeRequestToRuleScope(null));
    }

    @Test
    void convertValidationRuleToValidationRuleEntity() {
        ValidationRuleEntity validationRuleEntity = validationRuleMapper.convertValidationRuleToValidationRuleEntity(validationRule);
        Assertions.assertEquals(validationRule.getRuleOnProperty(), validationRuleEntity.getRuleOnProperty());
        Assertions.assertNull(validationRuleMapper.convertValidationRuleToValidationRuleEntity(null));
    }

    @Test
    void convertValidationRuleEntityToValidationRule() {
        ValidationRule validationRule = validationRuleMapper.convertValidationRuleEntityToValidationRule(validationRuleEntity);
        Assertions.assertEquals(validationRule.getScope().getProfiles(), validationRuleEntity.getScope().getProfiles());
        Assertions.assertNull(validationRuleMapper.convertValidationRuleEntityToValidationRule(null));
    }
}