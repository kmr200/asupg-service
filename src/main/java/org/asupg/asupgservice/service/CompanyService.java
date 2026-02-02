package org.asupg.asupgservice.service;

import org.asupg.asupgservice.exception.AppException;
import org.asupg.asupgservice.model.*;
import org.asupg.asupgservice.model.request.CompanySearchRequest;
import org.asupg.asupgservice.model.response.CompanyBalanceResponse;
import org.asupg.asupgservice.model.response.CompanyDebtResponse;
import org.asupg.asupgservice.model.response.CompanySearchResponse;
import org.asupg.asupgservice.model.response.TotalDebt;
import org.asupg.asupgservice.repository.CompanyRepository;
import org.asupg.asupgservice.repository.DeviceRepository;
import org.asupg.asupgservice.repository.TransactionRepository;
import org.bson.types.Decimal128;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    private static final Logger logger = LoggerFactory.getLogger(CompanyService.class);

    private final CompanyRepository companyRepository;

    private final TransactionRepository transactionRepository;

    private final DeviceRepository deviceRepository;

    public CompanyService(CompanyRepository companyRepository, TransactionRepository transactionRepository, DeviceRepository deviceRepository) {
        this.companyRepository = companyRepository;
        this.transactionRepository = transactionRepository;
        this.deviceRepository = deviceRepository;
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
            String email,
            String phone
    ) {
        logger.debug("Creating company {}", name);

        if (companyRepository.existsById(inn)) {
            logger.warn("Company with id {} already exists", inn);
            throw new AppException(409, "Validation failed", "Company with id: " + inn + " already exists");
        }

        CompanyDTO companyDTO = new CompanyDTO(
                inn,
                name,
                CompanyStatus.ACTIVE,
                email,
                phone
        );

        try {
            return companyRepository.insert(companyDTO);
        } catch (DuplicateKeyException e) {
            logger.info("Company with id {} already exists", inn);
            throw new AppException(409, "Conflict", "Company with id: " + inn + " already exists");
        }
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

        List<DeviceDTO> devices = deviceRepository.findByCompanyInn(id);
        BigDecimal monthlyCharge = devices.stream()
                .map(DeviceDTO::getMonthlyRate)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<YearMonth, BigDecimal> totalsByMonth =
                monthlyChargeTransactions.stream()
                        .collect(Collectors.groupingBy(
                                tx -> YearMonth.from(tx.getDate()),
                                Collectors.reducing(
                                        BigDecimal.ZERO,
                                        TransactionDTO::getAmount,
                                        BigDecimal::add
                                )
                        ));

        YearMonth now = YearMonth.now(ZoneOffset.UTC);

        List<CompanyBalanceResponse.MonthlyCharge> monthlyBreakdown =
                totalsByMonth.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .map(entry -> {
                            YearMonth month = entry.getKey();
                            BigDecimal total = entry.getValue();

                            return new CompanyBalanceResponse.MonthlyCharge(
                                    month.toString(),
                                    total,
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
                monthlyCharge,
                devices
        );

    }

    public CompanyDebtResponse getCompaniesInDebt(
            BigDecimal minBalance,
            BigDecimal maxBalance,
            int limit,
            String continuationToken,
            SortOrder sortOrder
    ) {
        MongoPageResponse<CompanyDTO> page;

        try {
            page = companyRepository.findCompaniesInDebt(
                    minBalance,
                    maxBalance,
                    limit,
                    continuationToken,
                    sortOrder
            );
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Cursor sort field mismatch")) {
                throw new AppException(400, "Validation failed", "Invalid cursor");
            } else {
                logger.error(e.getMessage());
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
                page.getNextCursor()
        );
    }

    public CompanySearchResponse getCompanies(
            BigDecimal minBalance,
            BigDecimal maxBalance,
            CompanyStatus status,
            Integer limit,
            String cursor,
            CompanySearchRequest.SortBy sortBy,
            SortOrder sortOrder
    ) {

        MongoPageResponse<CompanyDTO> page;

        try {
            page = companyRepository.findCompanies(
                    minBalance,
                    maxBalance,
                    status,
                    limit,
                    cursor,
                    sortBy,
                    sortOrder
            );
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Cursor sort field mismatch")) {
                throw new AppException(400, "Validation failed", "Invalid cursor");
            } else {
                logger.error(e.getMessage());
                throw e;
            }
        }

        return new CompanySearchResponse(page.getItems(), page.getNextCursor());
    }

    public TotalDebt getCompaniesTotalDebt() {
        return companyRepository.getTotalNegativeBalance();
    }
}
