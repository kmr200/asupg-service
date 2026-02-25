package org.asupg.asupgservice.repository;

import org.asupg.asupgservice.model.TransactionDTO;
import org.asupg.asupgservice.repository.custom.TransactionRepositoryCustom;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<TransactionDTO, String>, TransactionRepositoryCustom {

    List<TransactionDTO> findAllByCounterpartyInnAndTransactionType(
            String counterpartyInn,
            TransactionDTO.TransactionType type
    );

}
