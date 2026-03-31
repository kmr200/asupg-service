package org.asupg.asupgservice.client.asupg.model.response;

import lombok.Data;

import java.util.List;

@Data
public class AsupgDataResponse {

    private String errorMessage;
    private Integer count;
    private List<AsupgDevice> value;

}
