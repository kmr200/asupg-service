package org.asupg.asupgservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeviceSyncFailure {
    private String deviceId;
    private String deviceName;
    private String reason;
}