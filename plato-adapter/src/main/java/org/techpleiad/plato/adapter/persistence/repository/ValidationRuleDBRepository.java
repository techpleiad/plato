package org.techpleiad.plato.adapter.persistence.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.techpleiad.plato.core.domain.RuleScope;
import org.techpleiad.plato.core.domain.ValidationRule;

import java.util.List;

@Repository
public interface ValidationRuleDBRepository extends MongoRepository<ValidationRule, String> {

    @Query("{ 'scope' : ?0, ruleOnProperty: ?1 }")
    List<ValidationRule> findValidationRuleByScopeAndRuleOnProperty(RuleScope scope, String ruleOnProperty);
}
