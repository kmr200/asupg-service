package org.asupg.asupgservice.repository;

import org.asupg.asupgservice.model.AggregationResult;
import org.asupg.asupgservice.model.Company;
import org.asupg.asupgservice.repository.custom.CompanyRepositoryCustom;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends MongoRepository<Company, String>, CompanyRepositoryCustom {

    @Aggregation(pipeline = {
            "{ $match: { currentBalance: { $lt: { $numberDecimal: '0' } } } }",
            "{ $group: { _id: null, result: { $sum: '$currentBalance' } } }",
            "{ $project: { _id: 0, result: 1 } }"
    })
    AggregationResult getTotalNegativeBalance();

    @Aggregation(pipeline = {
            "{ $group: { _id: null, result: { $sum: '$currentBalance' } } }",
            "{ $project: { _id: 0, result: 1 } }"
    })
    AggregationResult getTotalBalance();
}
