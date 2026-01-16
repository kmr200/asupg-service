package org.asupg.asupgservice.repository;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.FeedResponse;
import com.azure.cosmos.models.SqlParameter;
import com.azure.cosmos.models.SqlQuerySpec;
import com.azure.cosmos.util.CosmosPagedIterable;
import lombok.extern.slf4j.Slf4j;
import org.asupg.asupgservice.model.CompanyDTO;
import org.asupg.asupgservice.model.CompanyStatus;
import org.asupg.asupgservice.model.CosmosPageResponse;
import org.asupg.asupgservice.model.SortOrder;
import org.asupg.asupgservice.model.request.CompanySearchRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Repository
public class CompanyRepositoryImpl implements CompanyRepositoryCustom {

    private final CosmosClient cosmosClient;
    private final CosmosContainer container;

    public CompanyRepositoryImpl(
            CosmosClientBuilder clientBuilder,
            @Value("${azure.cosmos.database}") String databaseName,
            @Value("${azure.cosmos.container-companies}") String containerName
    ) {
        this.cosmosClient = clientBuilder
                .buildClient();
        this.container = cosmosClient
                .getDatabase(databaseName)
                .getContainer(containerName);
    }

    @Override
    public CosmosPageResponse<CompanyDTO> findCompaniesInDebt(
            BigDecimal minDebt,
            BigDecimal maxDebt,
            int limit,
            String continuationToken,
            SortOrder sortOrder
    ) {
        StringBuilder query = new StringBuilder(
                "SELECT * FROM c WHERE c.currentBalance < 0"
        );
        List<SqlParameter> parameters = new ArrayList<>();

        if (minDebt != null) {
            query.append(" AND c.currentBalance >= @minDebt ");
            parameters.add(new SqlParameter("@minDebt", minDebt));
        }
        if (maxDebt != null) {
            query.append(" AND c.currentBalance <= @maxDebt ");
            parameters.add(new SqlParameter("@maxDebt", maxDebt));
        }

        query.append(" ORDER BY c.currentBalance ");

        query.append(Objects.requireNonNullElse(sortOrder, SortOrder.DESC));

        SqlQuerySpec querySpec = new SqlQuerySpec(query.toString(), parameters);

        CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();

        CosmosPagedIterable<CompanyDTO> pagedIterable = container.queryItems(
                querySpec,
                options,
                CompanyDTO.class
        );

        List<CompanyDTO> results = new ArrayList<>();
        String newContinuationToken = null;

        for (FeedResponse<CompanyDTO> page : pagedIterable.iterableByPage(continuationToken, limit)) {
            results.addAll(page.getResults());
            newContinuationToken = page.getContinuationToken();
            break; // Only get first page
        }

        return new CosmosPageResponse<>(results,  newContinuationToken);
    }

    @Override
    public CosmosPageResponse<CompanyDTO> findCompanies(
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
    ) {

        StringBuilder query = new StringBuilder(
                "SELECT * FROM c WHERE 1=1"
        );
        List<SqlParameter> parameters = new ArrayList<>();

        if (minBalance != null) {
            query.append(" AND c.currentBalance >= @minBalance");
            parameters.add(new SqlParameter("@minBalance", minBalance));
        }
        if (maxBalance != null) {
            query.append(" AND c.currentBalance <= @maxBalance ");
            parameters.add(new SqlParameter("@maxBalance", maxBalance));
        }
        if (subscriptionStartDateFrom != null) {
            query.append(" AND c.subscriptionStartDate >= @subscriptionStartDateFrom");
            parameters.add(new SqlParameter("@subscriptionStartDateFrom", subscriptionStartDateFrom.toString()));
        }
        if (subscriptionStartDateTo != null) {
            query.append(" AND c.subscriptionStartDate <= @subscriptionStartDateTo");
            parameters.add(new SqlParameter("@subscriptionStartDateTo", subscriptionStartDateTo.toString()));
        }
        if (billingStartMonthFrom != null) {
            query.append(" AND c.billingStartMonth >= @billingStartMonthFrom");
            parameters.add(new SqlParameter("@billingStartMonthFrom", billingStartMonthFrom.toString()));
        }
        if (billingStartMonthTo != null) {
            query.append(" AND c.billingStartMonth <= @billingStartMonthTo");
            parameters.add(new SqlParameter("@billingStartMonthTo", billingStartMonthTo.toString()));
        }
        if (status != null) {
            query.append(" AND c.status = @status");
            parameters.add(new SqlParameter("@status", status));
        }

        CompanySearchRequest.SortBy effectiveSortBy =
                Objects.requireNonNullElse(sortBy, CompanySearchRequest.SortBy.NAME);

        SortOrder effectiveSortOrder =
                Objects.requireNonNullElse(sortOrder, SortOrder.DESC);

        query.append(" ORDER BY c.").append(effectiveSortBy.getValue()).append(" ").append(effectiveSortOrder.getValue());

        SqlQuerySpec querySpec = new SqlQuerySpec(query.toString(), parameters);

        CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();

        CosmosPagedIterable<CompanyDTO> pagedIterable = container.queryItems(
                querySpec,
                options,
                CompanyDTO.class
        );

        List<CompanyDTO> results = new ArrayList<>();
        String newContinuationToken = null;

        for (FeedResponse<CompanyDTO> page : pagedIterable.iterableByPage(continuationToken, limit)) {
            results.addAll(page.getResults());
            newContinuationToken = page.getContinuationToken();
            break; // Only get first page
        }

        return new CosmosPageResponse<>(results, newContinuationToken);

    }

}
