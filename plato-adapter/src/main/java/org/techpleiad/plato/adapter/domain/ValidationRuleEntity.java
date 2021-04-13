package org.techpleiad.plato.adapter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.techpleiad.plato.core.domain.RuleScope;

@ToString
@Getter
@Builder
@AllArgsConstructor
@Document(collection = "rules")
public class ValidationRuleEntity {
    @Id
    private String ruleId;
    private RuleScope scope;
    private String ruleOnProperty;
    @Setter
    private String rule;
}
