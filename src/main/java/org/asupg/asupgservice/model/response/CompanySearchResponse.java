package org.asupg.asupgservice.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.asupg.asupgservice.model.CompanyDTO;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompanySearchResponse {

    List<CompanyDTO> companies;
    int count;
    boolean hasMore;
    String continuationToken;

    public CompanySearchResponse(List<CompanyDTO> companies, String continuationToken) {
        this.companies = companies;
        this.continuationToken = continuationToken;
        this.count = companies != null ? companies.size() : 0;
        this.hasMore = continuationToken != null && !continuationToken.isEmpty();
    }

}