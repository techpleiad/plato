package org.techpleiad.plato.core.domain;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@ToString
@Getter
@Builder
@Document(collection = "rules")
public class ValidationRule {
    @Id
    private final String ruleId;
    private final RuleScope scope;
    private final String ruleOnProperty;
    private final JsonNode rule;

}
