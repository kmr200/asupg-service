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
public class MongoPageResponse<T> {

    private List<T> items;
    private String nextCursor;

}
