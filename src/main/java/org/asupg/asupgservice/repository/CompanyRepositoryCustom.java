package org.asupg.asupgservice.repository;

import org.asupg.asupgservice.model.CompanyDTO;
import org.asupg.asupgservice.model.CompanyStatus;
import org.asupg.asupgservice.model.MongoPageResponse;
import org.asupg.asupgservice.model.SortOrder;
import org.asupg.asupgservice.model.request.CompanySearchRequest;

import java.math.BigDecimal;

public interface CompanyRepositoryCustom {

    MongoPageResponse<CompanyDTO> findCompaniesInDebt(
            BigDecimal minDebt,
            BigDecimal maxDebt,
            int limit,
            String cursor,
            SortOrder sortOrder
    );

    MongoPageResponse<CompanyDTO> findCompanies(
            BigDecimal minBalance,
            BigDecimal maxBalance,
            CompanyStatus status,
            Integer limit,
            String cursor,
            CompanySearchRequest.SortBy sortBy,
            SortOrder sortOrder
    );
}
