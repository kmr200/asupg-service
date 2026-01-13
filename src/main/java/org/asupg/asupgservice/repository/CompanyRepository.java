package org.asupg.asupgservice.repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import org.asupg.asupgservice.model.CompanyDTO;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends CosmosRepository<CompanyDTO, String> {
}
