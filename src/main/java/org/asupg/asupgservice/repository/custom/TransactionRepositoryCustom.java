package org.asupg.asupgservice.repository.custom;

import org.asupg.asupgservice.model.MongoPageResponse;
import org.asupg.asupgservice.model.TransactionDTO;

public interface TransactionRepositoryCustom {

    MongoPageResponse<TransactionDTO> findTransactions(
    );

}
