package org.techpleiad.plato.adapter.mapper;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.techpleiad.plato.adapter.domain.ValidationRuleEntity;
import org.techpleiad.plato.api.request.RuleScopeRequestTO;
import org.techpleiad.plato.api.request.ValidationRuleRequestTO;
import org.techpleiad.plato.core.domain.RuleScope;
import org.techpleiad.plato.core.domain.ValidationRule;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ValidationRuleMapper {

    List<ValidationRule> convertValidationRuleEntityListToValidationRuleList(final List<ValidationRuleEntity> validationRuleEntities);

    ValidationRule convertValidationRuleRequestTOtoValidationRule(final ValidationRuleRequestTO validationRuleRequestTO);

    RuleScope convertRuleScopeRequestToRuleScope(final RuleScopeRequestTO ruleScopeRequestTO);

    @Mapping(source = "rule", target = "rule", qualifiedByName = "jsonRuleToString")
    ValidationRuleEntity convertValidationRuleToValidationRuleEntity(final ValidationRule validationRule);

    @Mapping(source = "rule", target = "rule", qualifiedByName = "stringToJsonNode")
    ValidationRule convertValidationRuleEntityToValidationRule(final ValidationRuleEntity validationRuleEntity);

    @Named("jsonRuleToString")
    static String jsonRuleToString(final JsonNode rule) {
        return rule.toString();
    }

    @Named("stringToJsonNode")
    static JsonNode stringToJsonNode(final String rule) throws JsonProcessingException {
        return new ObjectMapper().readTree(rule);
    }
}
