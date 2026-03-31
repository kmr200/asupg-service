package org.asupg.asupgservice.repository.custom;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.asupg.asupgservice.model.DeviceDTO;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class DeviceRepositoryCustomImpl implements DeviceRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    public void updateDevices(List<DeviceDTO> devices) {
        if (devices == null || devices.isEmpty()) {
            log.info("Device list is null or empty");
            return;
        }

        log.info("Starting bulk replace of {} devices", devices.size());

        BulkOperations bulkOps =
                mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, DeviceDTO.class);

        for (DeviceDTO device : devices) {
            Query query = Query.query(Criteria.where("_id").is(device.getDeviceId()));

            device.setVersion(device.getVersion() != null ? device.getVersion() : 0L);
            bulkOps.replaceOne(query, device);
        }

        bulkOps.execute();

        log.info("Bulk replace completed");
    }
}
