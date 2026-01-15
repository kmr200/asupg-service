package org.asupg.asupgservice.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.asupg.asupgservice.model.SortOrder;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CompanyDebtSearchRequest {

    BigDecimal minDebt;
    BigDecimal maxDebt;
    Integer limit;
    String continuationToken;
    SortOrder sortOrder;

}
