package org.asupg.asupgservice.client.asupg;

import org.asupg.asupgservice.client.asupg.model.response.AsupgDataResponse;
import org.asupg.asupgservice.client.asupg.model.response.AsupgLoginResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "asupgCoreClient",
        url = "http://asupg.uz",
        configuration = AsupgCoreFeignConfig.class
)
public interface AsupgCoreClient {

    /**
     * POST /login
     */
    @PostMapping(value = "/asupg_odata/aut/GetToken(smartup,123456)", consumes = "application/json")
    AsupgLoginResponse login();

    @PostMapping(value = "/asupg_odata/data/objects", consumes = "application/json")
    AsupgDataResponse retrieveObjects(@RequestHeader("Authorization") String authHeader);

}
