package org.asupg.asupgservice.repository;

import org.asupg.asupgservice.model.AggregationResult;
import org.asupg.asupgservice.model.CompanyDTO;
import org.asupg.asupgservice.model.response.TotalDebt;
import org.asupg.asupgservice.repository.custom.CompanyRepositoryCustom;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface CompanyRepository extends MongoRepository<CompanyDTO, String>, CompanyRepositoryCustom {

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
