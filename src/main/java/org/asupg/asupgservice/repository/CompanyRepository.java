package org.asupg.asupgservice.repository;

import org.asupg.asupgservice.model.CompanyDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends MongoRepository<CompanyDTO, String>, CompanyRepositoryCustom {

}
