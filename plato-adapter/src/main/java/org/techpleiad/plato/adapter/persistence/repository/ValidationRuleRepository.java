package org.techpleiad.plato.adapter.persistence.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.techpleiad.plato.adapter.domain.ValidationRuleEntity;

import java.util.List;

@Repository
public interface ValidationRuleRepository extends MongoRepository<ValidationRuleEntity, String> {

    List<ValidationRuleEntity> findValidationRuleByRuleOnPropertyEquals(String ruleOnProperty);
}
