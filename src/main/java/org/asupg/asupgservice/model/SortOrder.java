package org.asupg.asupgservice.model;

import lombok.*;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public enum SortOrder {

    ASC("ASC"),
    DESC("DESC");
    public String value;

}
