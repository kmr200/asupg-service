package org.asupg.asupgservice.repository;

import org.asupg.asupgservice.model.UserDTO;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserDTO, String> {

}
