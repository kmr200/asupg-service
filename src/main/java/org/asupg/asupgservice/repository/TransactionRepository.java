package org.asupg.asupgservice.repository;

import org.asupg.asupgservice.model.AggregationResult;
import org.asupg.asupgservice.model.Transaction;
import org.asupg.asupgservice.repository.custom.TransactionRepositoryCustom;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String>, TransactionRepositoryCustom {

    List<Transaction> findAllByCounterpartyInnAndTransactionType(
            String counterpartyInn,
            Transaction.TransactionType type
    );

    @Aggregation(pipeline = {
            "{ $match: { 'reconciliation.status': 'NOT_FOUND' } }",
            "{ $group: { _id: null, result: { $sum: '$amount' } } }",
            "{ $project: { _id: 0, result: 1 } }"
    })
    AggregationResult getTotalNotFoundTransactions();
}
