package org.asupg.asupgservice.model.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DeviceSyncResponse {

    private int total;
    private int updated;
    private int failed;
    private List<DeviceSyncFailure> failures;

    @JsonIgnore
    private SyncStatus syncStatus;

    public SyncStatus getStatus() {
        if (syncStatus == SyncStatus.FAILED) return SyncStatus.FAILED;
        if (failed == 0) return SyncStatus.SUCCESS;
        return SyncStatus.PARTIAL_SUCCESS;
    }

    public enum SyncStatus {
        SUCCESS, PARTIAL_SUCCESS, FAILED
    }

}
