package org.asupg.asupgservice.repository;

import org.asupg.asupgservice.model.CompanyDTO;
import org.asupg.asupgservice.model.CosmosPageResponse;
import org.asupg.asupgservice.model.SortOrder;

import java.math.BigDecimal;

public interface CompanyRepositoryCustom {

    CosmosPageResponse<CompanyDTO> findCompaniesInDebt(
            BigDecimal minDebt,
            BigDecimal maxDebt,
            int limit,
            String continuationToken,
            SortOrder sortOrder
    );

}
