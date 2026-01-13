package org.asupg.asupgservice.service;

import lombok.extern.slf4j.Slf4j;
import org.asupg.asupgservice.exception.AppException;
import org.asupg.asupgservice.model.CompanyDTO;
import org.asupg.asupgservice.model.CompanyStatus;
import org.asupg.asupgservice.model.response.CompanyBalanceResponse;
import org.asupg.asupgservice.repository.CompanyRepository;
import org.asupg.asupgservice.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

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
            LocalDate billingStartDate,
            String email,
            String phone
    ) {
        logger.debug("Creating company {}", name);

        if (companyRepository.existsById(inn)) {
            logger.warn("Company with id {} already exists", inn);
            throw new AppException(409, "Validation failed", "Company with id: " + inn + " already exists");
        }

        if (billingStartDate == null) {
            billingStartDate = LocalDate.now().plusYears(billingFreePeriod);
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
                        billingStartDate,
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

        // Calculate total paid from RECONCILED transactions
        BigDecimal totalPaid = calculateTotalPaidFromTransactions(id);

        // Calculate charges
        BigDecimal totalCharges = calculateTotalCharges(
                company.getBillingStartDate(),
                company.getMonthlyRate()
        );

        // Calculate balance
        BigDecimal balance = totalPaid.subtract(totalCharges)
                .setScale(2, RoundingMode.HALF_UP);

        logger.debug("Balance calculation for {}: Paid={}, Charges={}, Balance={}",
                id, totalPaid, totalCharges, balance);

        // Calculate monthly breakdown
        List<CompanyBalanceResponse.MonthlyCharge> breakdown =
                calculateMonthlyBreakdown(company.getBillingStartDate(), company.getMonthlyRate());

        // Create billing info
        CompanyBalanceResponse.BillingInfo billingInfo = new CompanyBalanceResponse.BillingInfo(
                company.getBillingStartDate(),
                company.getMonthlyRate()
        );

        return new CompanyBalanceResponse(
                company.getInn(),
                company.getName(),
                balance,
                totalPaid,
                totalCharges,
                calculateMonthsElapsed(company.getBillingStartDate()),
                breakdown,
                billingInfo
        );
    }

    private List<CompanyBalanceResponse.MonthlyCharge> calculateMonthlyBreakdown(
            LocalDate billingStartDate,
            BigDecimal monthlyRate) {

        if (billingStartDate == null || monthlyRate == null) {
            return List.of();
        }

        LocalDate now = LocalDate.now();

        // If billing hasn't started yet
        if (now.isBefore(billingStartDate)) {
            return List.of();
        }

        List<CompanyBalanceResponse.MonthlyCharge> breakdown = new ArrayList<>();
        LocalDate currentMonth = billingStartDate.withDayOfMonth(1);
        LocalDate today = LocalDate.now();

        while (!currentMonth.isAfter(today)) {
            LocalDate periodStart = currentMonth.isBefore(billingStartDate)
                    ? billingStartDate
                    : currentMonth;

            LocalDate periodEnd = currentMonth.plusMonths(1).minusDays(1);
            if (periodEnd.isAfter(today)) {
                periodEnd = today;
            }

            // Determine status
            String status;
            if (currentMonth.isAfter(today.withDayOfMonth(1))) {
                status = "FUTURE";
            } else if (currentMonth.isBefore(today.withDayOfMonth(1))) {
                status = "PAST";
            } else {
                status = "CURRENT";
            }

            // Only add if billing has started for this month
            if (!periodStart.isAfter(today)) {
                breakdown.add(new CompanyBalanceResponse.MonthlyCharge(
                        currentMonth.format(DateTimeFormatter.ofPattern("yyyy-MM")),
                        monthlyRate.setScale(2, RoundingMode.HALF_UP),
                        periodStart,
                        periodEnd,
                        status
                ));
            }

            currentMonth = currentMonth.plusMonths(1);
        }

        logger.debug("Generated {} monthly charge entries", breakdown.size());
        return breakdown;
    }

    private Long calculateMonthsElapsed(LocalDate billingStartDate) {
        if (billingStartDate == null) {
            return null;
        }

        LocalDate now = LocalDate.now();
        if (now.isBefore(billingStartDate)) {
            return 0L;
        }

        return ChronoUnit.MONTHS.between(billingStartDate, now);
    }

    private BigDecimal calculateTotalPaidFromTransactions(String inn) {
        logger.debug("Calculating total paid from transactions for INN: {}", inn);

        List<BigDecimal> result = transactionRepository.sumAmountByInn(inn);
        BigDecimal total = (result != null && !result.isEmpty() && result.getFirst() != null)
                ? result.getFirst()
                : BigDecimal.ZERO;

        logger.debug("Total reconciled payments for {}: {}", inn, total);
        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTotalCharges(
            LocalDate billingStartDate,
            BigDecimal monthlyRate
    ) {
        LocalDate now = LocalDate.now();

        if (now.isBefore(billingStartDate)) {
            return BigDecimal.ZERO;
        }

        long monthCount = ChronoUnit.MONTHS.between(billingStartDate, now);

        return monthlyRate
                .multiply(BigDecimal.valueOf(monthCount))
                .setScale(2, RoundingMode.HALF_UP);
    }

}
