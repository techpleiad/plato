package org.techpleiad.plato.adapter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.techpleiad.plato.core.domain.ValidationRuleScope;

@ToString
@Getter
@Builder
@AllArgsConstructor
@Document(collection = "rules")
@Setter
public class ValidationRuleEntity {
    @Id
    private String ruleId;
    private ValidationRuleScope scope;
    private String ruleOnProperty;
    private String rule;
}
