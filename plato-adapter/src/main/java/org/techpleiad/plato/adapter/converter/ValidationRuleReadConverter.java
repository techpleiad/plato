package org.techpleiad.plato.adapter.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;
import org.techpleiad.plato.core.domain.RuleScope;
import org.techpleiad.plato.core.domain.ValidationRule;

import java.util.HashSet;
import java.util.List;

@Component
@ReadingConverter
public class ValidationRuleReadConverter implements Converter<Document, ValidationRule> {

    @SneakyThrows
    @Override
    public ValidationRule convert(final Document dbObject) {
        final Document docs = (Document) dbObject.get("scope");
        final ObjectId objectId = (ObjectId) dbObject.get("_id");
        return ValidationRule.builder()
                .rule(new ObjectMapper().readTree((String) dbObject.get("rule")))
                .ruleId(objectId.toHexString())
                .ruleOnProperty((String) dbObject.get("ruleOnProperty"))
                .scope(RuleScope.builder()
                        .branches(new HashSet<String>((List<String>) docs.get("branches")))
                        .services(new HashSet<String>((List<String>) docs.get("services")))
                        .profiles(new HashSet<String>((List<String>) docs.get("profiles")))
                        .build())
                .build();
    }
}
