package org.asupg.asupgservice.repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import com.azure.spring.data.cosmos.repository.Query;
import org.asupg.asupgservice.model.TransactionDTO;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TransactionRepository extends CosmosRepository<TransactionDTO, String> {

    @Query("SELECT VALUE SUM(c.amount) FROM c WHERE c.counterpartyInn = @inn")
    public List<BigDecimal> sumAmountByInn(String inn);

    public List<TransactionDTO> findAllByCounterpartyInnAndTransactionType(
            String counterpartyInn,
            TransactionDTO.TransactionType type
    );

}
