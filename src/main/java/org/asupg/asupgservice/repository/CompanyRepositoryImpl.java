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
import org.asupg.asupgservice.model.CosmosPageResponse;
import org.asupg.asupgservice.model.SortOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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
}
