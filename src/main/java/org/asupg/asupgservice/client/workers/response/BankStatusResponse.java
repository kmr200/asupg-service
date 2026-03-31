package org.asupg.asupgservice.client.workers.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BankStatusResponse {

    private boolean locked;

    private String lockReason;

    private List<AccountResponse> accounts;
}
