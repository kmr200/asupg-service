package org.asupg.asupgservice.repository;

import org.asupg.asupgservice.model.AggregationResult;
import org.asupg.asupgservice.model.DeviceDTO;
import org.asupg.asupgservice.repository.custom.DeviceRepositoryCustom;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends MongoRepository<DeviceDTO, String>, DeviceRepositoryCustom {

    List<DeviceDTO> findByCompanyInn(String companyInn);

    @Aggregation(pipeline = {
            "{ $group: { _id: null, result: { $sum: 1 } } }",
            "{ $project: { _id: 0, result: 1 } }"
    })
    AggregationResult getTotalDevices();

}
