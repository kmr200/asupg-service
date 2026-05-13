package org.asupg.asupgservice.repository;

import org.asupg.asupgservice.model.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserEntity, String> {

}
