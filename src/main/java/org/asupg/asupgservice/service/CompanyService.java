package org.asupg.asupgservice.service;

import com.azure.core.http.rest.PagedResponse;
import com.azure.cosmos.CosmosException;
import org.asupg.asupgservice.exception.AppException;
import org.asupg.asupgservice.model.*;
import org.asupg.asupgservice.model.response.CompanyBalanceResponse;
import org.asupg.asupgservice.model.response.CompanyDebtResponse;
import org.asupg.asupgservice.repository.CompanyRepository;
import org.asupg.asupgservice.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    private static final Logger logger = LoggerFactory.getLogger(CompanyService.class);

    @Value("${asupg.billing.free-period}")
    private Integer billingFreePeriod;

    @Value("${asupg.billing.monthly-rate}")
    private Long billingMonthlyRate;

    private final CompanyRepository companyRepository;

    private final TransactionRepository transactionRepository;

    public CompanyService(CompanyRepository companyRepository, TransactionRepository transactionRepository) {
        this.companyRepository = companyRepository;
        this.transactionRepository = transactionRepository;
    }

    public CompanyDTO getCompany(String id) {
        logger.debug("Get company with id {}", id);
        return companyRepository.findById(id).orElseThrow(
                () -> new AppException(404, "Validation failed", "Company with id: " + id + " not found")
        );
    }

    public CompanyDTO createCompany(
            String inn,
            String name,
            Long monthlyRate,
            YearMonth billingStartMonth,
            String email,
            String phone
    ) {
        logger.debug("Creating company {}", name);

        if (companyRepository.existsById(inn)) {
            logger.warn("Company with id {} already exists", inn);
            throw new AppException(409, "Validation failed", "Company with id: " + inn + " already exists");
        }

        if (billingStartMonth == null) {
            billingStartMonth = YearMonth.now(ZoneOffset.UTC).plusYears(billingFreePeriod);
        }

        if (monthlyRate == null) {
            monthlyRate = billingMonthlyRate;
        }

        CompanyDTO company = companyRepository.save(
                new CompanyDTO(
                        inn,
                        name,
                        BigDecimal.valueOf(monthlyRate),
                        LocalDate.now(),
                        billingStartMonth,
                        CompanyStatus.ACTIVE,
                        email,
                        phone
                )
        );
        logger.info("Successfully created company {}", name);

        return company;
    }

    public CompanyBalanceResponse getCompanyBalance(String id) {
        logger.debug("Get company balance with id {}", id);

        CompanyDTO company = companyRepository.findById(id).orElseThrow(
                () -> new AppException(404, "Validation failed", "Company with id: " + id + " not found")
        );

        List<TransactionDTO> monthlyChargeTransactions = transactionRepository.findAllByCounterpartyInnAndTransactionType(
                id,
                TransactionDTO.TransactionType.MONTHLY_CHARGE
        );

        Map<YearMonth, List<TransactionDTO>> groupedByMonth =
                monthlyChargeTransactions.stream()
                        .collect(Collectors.groupingBy(
                                transaction -> YearMonth.from(transaction.getDate())
                        ));

        YearMonth now = YearMonth.now(ZoneOffset.UTC);

        List<CompanyBalanceResponse.MonthlyCharge> monthlyBreakdown =
                groupedByMonth.entrySet().stream()
                        .map(entry -> {
                            YearMonth month = entry.getKey();
                            BigDecimal totalForMonth = entry.getValue().stream()
                                    .map(TransactionDTO::getAmount)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                            return new CompanyBalanceResponse.MonthlyCharge(
                                    month.toString(),
                                    totalForMonth,
                                    month.atDay(1),
                                    month.atEndOfMonth(),
                                    month.equals(now) ? "CURRENT" : "PAST"
                            );
                        })
                        .toList();

        long monthsElapsed = monthlyBreakdown.size();

        return new CompanyBalanceResponse(
                company.getInn(),
                company.getName(),
                company.getCurrentBalance(),
                monthsElapsed,
                monthlyBreakdown,
                new CompanyBalanceResponse.BillingInfo(
                        company.getBillingStartMonth(),
                        company.getMonthlyRate()
                )
        );

    }

    public CompanyDebtResponse getCompaniesInDebt(
            BigDecimal minDebt,
            BigDecimal maxDebt,
            int limit,
            String continuationToken,
            SortOrder sortOrder
    ) {
        CosmosPageResponse<CompanyDTO> page;
        try {
             page = companyRepository.findCompaniesInDebt(
                    minDebt,
                    maxDebt,
                    limit,
                    continuationToken,
                    sortOrder
            );
        } catch (CosmosException e) {
            if (e.getMessage().contains("INVALID JSON in continuation token")) {
                throw new AppException(400, "Validation failed", "Invalid continuation token");
            } else {
                logger.warn(e.getMessage());
                throw e;
            }
        }
        List<CompanyDebtResponse.CompanyDebtDetails> data = page.getItems().stream()
                .map(
                        company -> new CompanyDebtResponse.CompanyDebtDetails(
                                company.getInn(),
                                company.getName(),
                                company.getCurrentBalance()
                        )
                ).toList();

        return new CompanyDebtResponse(
                data,
                page.getContinuationToken()
        );
    }
}
