package org.asupg.asupgservice.client.asupg.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AsupgDevice {

    @JsonProperty("object_guid")
    private String objectGuid;

    @JsonProperty("object_name")
    private String objectName;

    @JsonProperty("correctortype_name")
    private String correctorTypeName;

    @JsonProperty("corrector_number")
    private String correctorNumber;

    @JsonProperty("tube_order")
    private Integer tubeOrder;

    @JsonProperty("tube_guid")
    private String tubeGuid;

    @JsonProperty("connecttype_guid")
    private String connectTypeGuid;

    @JsonProperty("object_state")
    private String objectState;

    @JsonProperty("object_state_date")
    private LocalDateTime objectStateDate;

    @JsonProperty("status_guid")
    private String statusGuid;

    private String address;

    @JsonProperty("service_guid")
    private String serviceGuid;

    @JsonProperty("objectvolume_guid")
    private String objectVolumeGuid;

    @JsonProperty("objecttype_guid")
    private String objectTypeGuid;

    @JsonProperty("objectkind_guid")
    private String objectKindGuid;

    @JsonProperty("object_date")
    private LocalDateTime objectDate;

    @JsonProperty("contractasupg_name")
    private String contractAsupgName;

    @JsonProperty("contractasupg_date")
    private LocalDateTime contractAsupgDate;

    @JsonProperty("contractasupg_number")
    private String contractAsupgNumber;

    @JsonProperty("contractasupg_clientid")
    private String contractAsupgClientId;
}
