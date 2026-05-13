package org.asupg.asupgservice.repository.custom;

import org.asupg.asupgservice.model.*;
import org.asupg.asupgservice.model.request.CompanySearchRequest;

import java.math.BigDecimal;

public interface CompanyRepositoryCustom {

    MongoPageResponse<Company> findCompaniesInDebt(
            BigDecimal minDebt,
            BigDecimal maxDebt,
            int limit,
            String cursor,
            SortOrder sortOrder,
            String search
    );

    MongoPageResponse<Company> findCompanies(
            BigDecimal minBalance,
            BigDecimal maxBalance,
            CompanyStatus status,
            Integer limit,
            String cursor,
            CompanySearchRequest.SortBy sortBy,
            SortOrder sortOrder,
            String search
    );

    CompanyDashboardResult getCompanyDashboardAggregation();
}
