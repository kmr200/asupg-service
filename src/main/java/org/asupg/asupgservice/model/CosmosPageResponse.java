package org.asupg.asupgservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CosmosPageResponse<T> {

    private List<T> items;
    private String continuationToken;

}
