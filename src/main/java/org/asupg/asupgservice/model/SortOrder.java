package org.asupg.asupgservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public enum SortOrder {

    ASC("ASC"),
    DESC("DESC");
    public String value;

}
