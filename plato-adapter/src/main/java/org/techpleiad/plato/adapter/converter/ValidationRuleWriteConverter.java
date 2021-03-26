package org.techpleiad.plato.adapter.converter;

import org.bson.Document;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;
import org.techpleiad.plato.core.domain.RuleScope;
import org.techpleiad.plato.core.domain.ValidationRule;

@Component
@WritingConverter
public class ValidationRuleWriteConverter implements Converter<ValidationRule, Document> {

    @Override
    public Document convert(@NotNull final ValidationRule validationRule) {
        final Document dbObject = new Document();
        dbObject.put("scope", convertScopeToDocument(validationRule.getScope()));
        dbObject.put("ruleOnProperty", validationRule.getRuleOnProperty());
        dbObject.put("rule", validationRule.getRule().toString());
        return dbObject;
    }

    Document convertScopeToDocument(final RuleScope ruleScope) {
        final Document document = new Document();
        document.put("services", ruleScope.getServices());
        document.put("branches", ruleScope.getBranches());
        document.put("profiles", ruleScope.getProfiles());
        return document;
    }
}


