package org.asupg.asupgservice.service;

import lombok.RequiredArgsConstructor;
import org.asupg.asupgservice.client.asupg.AsupgCoreClient;
import org.asupg.asupgservice.client.asupg.model.response.AsupgDataResponse;
import org.asupg.asupgservice.client.asupg.model.response.AsupgDevice;
import org.asupg.asupgservice.client.asupg.model.response.AsupgLoginResponse;
import org.asupg.asupgservice.exception.AppException;
import org.asupg.asupgservice.model.DeviceStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AsupgCoreService {

    private final AsupgCoreClient asupgCoreClient;

    private static final String ACTIVE_STATUS_GUID = "56ce2b66-1c31-43a7-bedb-4f56b164a807";

    public List<AsupgDevice> retrieveDevices() {
        String token = retrieveToken();
        return retrieveDevices(token);
    }

    public DeviceStatus mapStatusGuidToDeviceStatus(String guid) {
        return guid.equals(ACTIVE_STATUS_GUID) ? DeviceStatus.ACTIVE : DeviceStatus.INACTIVE;
    }

    private String retrieveToken() {
        AsupgLoginResponse loginResponse = asupgCoreClient.login();

        if (loginResponse.getAccessToken() == null || loginResponse.getAccessToken().isBlank()) {
            throw new AppException(400, "Не удалось авторизоваться в http://asupg.uz", loginResponse.getErrorMessage());
        }

        return loginResponse.getAccessToken();
    }

    private List<AsupgDevice> retrieveDevices(String token) {
        AsupgDataResponse dataResponse = asupgCoreClient.retrieveObjects("Bearer " + token);

        if (dataResponse.getErrorMessage() != null && !dataResponse.getErrorMessage().isBlank()) {
            throw new AppException(400, "Не удалось запросить список устройств", dataResponse.getErrorMessage());
        }

        return dataResponse.getValue();
    }

}
