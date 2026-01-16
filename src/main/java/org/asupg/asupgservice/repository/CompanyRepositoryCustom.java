package org.asupg.asupgservice.repository;

import org.asupg.asupgservice.model.CompanyDTO;
import org.asupg.asupgservice.model.CompanyStatus;
import org.asupg.asupgservice.model.CosmosPageResponse;
import org.asupg.asupgservice.model.SortOrder;
import org.asupg.asupgservice.model.request.CompanySearchRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface CompanyRepositoryCustom {

    CosmosPageResponse<CompanyDTO> findCompaniesInDebt(
            BigDecimal minDebt,
            BigDecimal maxDebt,
            int limit,
            String continuationToken,
            SortOrder sortOrder
    );

    CosmosPageResponse<CompanyDTO> findCompanies(
            BigDecimal minBalance,
            BigDecimal maxBalance,
            LocalDate subscriptionStartDateFrom,
            LocalDate subscriptionStartDateTo,
            YearMonth billingStartMonthFrom,
            YearMonth billingStartMonthTo,
            CompanyStatus status,
            Integer limit,
            String continuationToken,
            CompanySearchRequest.SortBy sortBy,
            SortOrder sortOrder
    );
}
