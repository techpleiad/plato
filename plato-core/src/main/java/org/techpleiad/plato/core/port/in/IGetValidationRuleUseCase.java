package org.techpleiad.plato.core.port.in;

import org.techpleiad.plato.core.domain.ValidationRule;

import java.util.Map;

public interface IGetValidationRuleUseCase {

    Map<String, ValidationRule> getValidationRuleMapByScope(String service, String branch, String profile);

}
