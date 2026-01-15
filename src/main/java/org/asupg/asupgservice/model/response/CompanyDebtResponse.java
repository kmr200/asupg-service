package org.asupg.asupgservice.model.response;

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
public class CompanyDebtResponse {

    List<CompanyDebtDetails> companies;
    int count;
    boolean hasMore;
    String continuationToken;

    public CompanyDebtResponse(List<CompanyDebtDetails> companies, String continuationToken) {
        this.companies = companies;
        this.continuationToken = continuationToken;
        this.count = companies != null ? companies.size() : 0;
        this.hasMore = continuationToken != null && !continuationToken.isEmpty();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompanyDebtDetails {
        private String inn;
        private String name;
        private BigDecimal balance;
    }

}
