package org.asupg.asupgservice.repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import org.asupg.asupgservice.model.UserDTO;

public interface UserRepository extends CosmosRepository<UserDTO, String> {
}
