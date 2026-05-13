package org.asupg.asupgservice.service;

import org.asupg.asupgservice.exception.AppException;
import org.asupg.asupgservice.model.*;
import org.asupg.asupgservice.model.request.CompanySearchRequest;
import org.asupg.asupgservice.model.response.CompanyBalanceResponse;
import org.asupg.asupgservice.model.response.CompanyDebtResponse;
import org.asupg.asupgservice.model.response.CompanySearchResponse;
import org.asupg.asupgservice.repository.CompanyRepository;
import org.asupg.asupgservice.repository.DeviceRepository;
import org.asupg.asupgservice.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public Company getCompany(String id) {
        logger.debug("Get company with id {}", id);
        return companyRepository.findById(id).orElseThrow(
                () -> new AppException(404, "Ресурс не найден", "Компания с id: " + id + " не найдена")
        );
    }

    public Company createCompany (
            String inn,
            String name,
            String email,
            String phone
    ) {
        logger.debug("Creating company {}", name);

        if (companyRepository.existsById(inn)) {
            logger.warn("Company with id {} already exists", inn);
            throw new AppException(409, "Конфликт", "Компания с id: " + inn + " уже зарегистрирована");
        }

        Company company = new Company(
                inn,
                name,
                CompanyStatus.ACTIVE,
                email,
                phone
        );

        try {
            return companyRepository.insert(company);
        } catch (DuplicateKeyException e) {
            logger.info("Company with id {} already exists", inn);
            throw new AppException(409, "Конфликт", "Компания с id: " + inn + " уже зарегистрирована");
        }
    }

    @Transactional
    public Company updateCompany(
            String inn,
            String name,
            BigDecimal currentBalance,
            CompanyStatus status,
            String email,
            String phone
    ) {
        Company company = getCompany(inn);

        if (name != null && !name.isBlank()) company.setName(name);
        if (currentBalance != null) company.setCurrentBalance(currentBalance);
        if (status != null) company.setStatus(status);
        if (email != null && !email.isBlank()) company.setEmail(email);
        if (phone != null && !phone.isBlank()) company.setPhone(phone);

        return companyRepository.save(company);
    }

    public Company deleteCompany(String inn) {
        Company company = getCompany(inn);
        companyRepository.delete(company);

        return company;
    }

    public CompanyBalanceResponse getCompanyBalance(String id) {
        logger.debug("Get company balance with id {}", id);

        Company company = companyRepository.findById(id).orElseThrow(
                () -> new AppException(404, "Ошибка валидации", "Компания с id: " + id + " не найдена")
        );

        List<Transaction> monthlyChargeTransactions = transactionRepository.findAllByCounterpartyInnAndTransactionType(
                id,
                Transaction.TransactionType.MONTHLY_CHARGE
        );

        List<Device> devices = deviceRepository.findByCompanyInn(id);
        BigDecimal monthlyCharge = devices.stream()
                .map(Device::getMonthlyRate)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<YearMonth, BigDecimal> totalsByMonth =
                monthlyChargeTransactions.stream()
                        .collect(Collectors.groupingBy(
                                tx -> YearMonth.from(tx.getDate()),
                                Collectors.reducing(
                                        BigDecimal.ZERO,
                                        Transaction::getAmount,
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
            String cursor,
            SortOrder sortOrder,
            String search
    ) {
        MongoPageResponse<Company> page;

        page = companyRepository.findCompaniesInDebt(
                minBalance,
                maxBalance,
                limit,
                cursor,
                sortOrder,
                search
        );

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
            SortOrder sortOrder,
            String search
    ) {

        MongoPageResponse<Company> page;

        page = companyRepository.findCompanies(
                minBalance,
                maxBalance,
                status,
                limit,
                cursor,
                sortBy,
                sortOrder,
                search
        );

        return new CompanySearchResponse(page.getItems(), page.getNextCursor());
    }

}
