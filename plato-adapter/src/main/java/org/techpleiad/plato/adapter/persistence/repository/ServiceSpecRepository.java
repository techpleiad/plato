package org.techpleiad.plato.adapter.persistence.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.techpleiad.plato.core.domain.ServiceSpec;

import java.util.List;

@Repository
public interface ServiceSpecRepository extends MongoRepository<ServiceSpec, String> {

    List<ServiceSpec> findServiceSpecsByServiceIn(List<String> serviceList);
}
