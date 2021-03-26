package org.techpleiad.plato.adapter.persistence.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.techpleiad.plato.core.domain.ValidationRule;

import java.util.List;

@Repository
public interface ValidationRuleDBRepository extends MongoRepository<ValidationRule, String> {

    List<ValidationRule> findValidationRuleByRuleOnPropertyEquals(String ruleOnProperty);
}
