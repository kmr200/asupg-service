package org.asupg.asupgservice.client.asupg.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AsupgLoginResponse {

    private String errorMessage;

    @JsonProperty("access_token")
    private String accessToken;

}