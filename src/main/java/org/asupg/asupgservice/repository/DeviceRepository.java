package org.asupg.asupgservice.repository;

import org.asupg.asupgservice.model.DeviceDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends MongoRepository<DeviceDTO, String> {

    List<DeviceDTO> findByCompanyInn(String companyInn);

}
