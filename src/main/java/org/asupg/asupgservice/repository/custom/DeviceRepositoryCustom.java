package org.asupg.asupgservice.repository.custom;

import org.asupg.asupgservice.model.DeviceDTO;

import java.util.List;

public interface DeviceRepositoryCustom {

    void updateDevices(List<DeviceDTO> devices);

}
