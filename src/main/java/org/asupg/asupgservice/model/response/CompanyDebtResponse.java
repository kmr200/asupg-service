package org.asupg.asupgservice.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response class for requesting companies in debt")
public class CompanyDebtResponse {

    @Schema(description = "List of companies in debt")
    List<CompanyDebtDetails> companies;

    @Schema(description = "Amount of companies in debt")
    int count;

    @Schema(description = "Are there any more pages")
    boolean hasMore;

    @Schema(description = "Cursor token to retrieve next page", example = "token")
    String nextCursor;

    public CompanyDebtResponse(List<CompanyDebtDetails> companies, String nextCursor) {
        this.companies = companies;
        this.nextCursor = nextCursor;
        this.count = companies != null ? companies.size() : 0;
        this.hasMore = nextCursor != null && !nextCursor.isEmpty();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompanyDebtDetails {

        @Schema(description = "INN of the company to be created", example = "123456789")
        private String inn;

        @Schema(description = "Name of the company to be created", example = "OOO \"TEST\"")
        private String name;

        @Schema(description = "Current balance of the company", example = "200000")
        private BigDecimal balance;
    }

}
