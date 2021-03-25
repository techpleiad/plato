package org.techpleiad.plato.core.domain;


import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@ToString
@Getter
@Builder
@Document(collection = "rules")
public class ValidationRule {
    RuleScope scope;
    String ruleOnProperty;
    String rule;
}
