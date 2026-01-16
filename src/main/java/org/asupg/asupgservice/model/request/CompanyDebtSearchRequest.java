package org.asupg.asupgservice.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    @Min(1)
    @Max(100)
    @JsonProperty(defaultValue = "10")
    Integer limit = 10;

    String continuationToken;

    @JsonProperty(defaultValue = "DESC")
    SortOrder sortOrder = SortOrder.DESC;

}
