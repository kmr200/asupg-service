package org.asupg.asupgservice.client.model.request;

import lombok.Data;

@Data
public class BankConfigUpdateRequest {

    private String host;
    private String username;
    private String password;
    private String account;
    private String branch;

}
