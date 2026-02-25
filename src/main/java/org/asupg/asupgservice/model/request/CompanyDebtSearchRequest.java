package org.asupg.asupgservice.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
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
@Schema(description = "Request class for searching companies in debt")
public class CompanyDebtSearchRequest {

    @Schema(description = "Minimum currentBalance of the company. Automatically converted to negative value", example = "-100000", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    BigDecimal minBalance;

    @Schema(description = "Maximum currentBalance of the company", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    BigDecimal maxBalance;

    @Min(1)
    @Max(100)
    @JsonProperty(defaultValue = "10")
    @Schema(description = "Maximum elements per page", defaultValue = "10", example = "5", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    Integer limit = 10;

    @Schema(description = "Cursor to retrieve next page", example = "token", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String cursor;

    @JsonProperty(defaultValue = "DESC")
    @Schema(description = "Sorting order by balance of the company", defaultValue = "DESC", example = "ASC", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    SortOrder sortOrder = SortOrder.DESC;

    @Size(max = 100)
    @Schema(description = "Search field for searching from company INN or company name, case insensitive", example = "123", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String search;

}
