package org.asupg.asupgservice.service;

import lombok.extern.slf4j.Slf4j;
import org.asupg.asupgservice.client.WorkersClient;
import org.asupg.asupgservice.client.model.response.BankStatusResponse;
import org.asupg.asupgservice.exception.AppException;
import org.asupg.asupgservice.model.AggregationResult;
import org.asupg.asupgservice.model.CompanyDashboardResult;
import org.asupg.asupgservice.model.TransactionDashboardResult;
import org.asupg.asupgservice.model.response.DashboardResponse;
import org.asupg.asupgservice.repository.CompanyRepository;
import org.asupg.asupgservice.repository.DeviceRepository;
import org.asupg.asupgservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

@Slf4j
@Service
public class ReportService {

    private final Executor dashboardExecutor;
    private final DeviceRepository deviceRepository;
    private final CompanyRepository companyRepository;
    private final TransactionRepository transactionRepository;
    private final WorkersClient workersClient;

    public ReportService(
            @Qualifier("dashboardExecutor") Executor dashboardExecutor,
            DeviceRepository deviceRepository,
            CompanyRepository companyRepository,
            TransactionRepository transactionRepository,
            WorkersClient workersClient) {
        this.dashboardExecutor = dashboardExecutor;
        this.deviceRepository = deviceRepository;
        this.companyRepository = companyRepository;
        this.transactionRepository = transactionRepository;
        this.workersClient = workersClient;
    }

    public DashboardResponse getDashboard() {

        CompletableFuture<CompanyDashboardResult> companyFuture =
                CompletableFuture.supplyAsync(companyRepository::getCompanyDashboardAggregation, dashboardExecutor);
        CompletableFuture<TransactionDashboardResult> transactionFuture =
                CompletableFuture.supplyAsync(transactionRepository::getTransactionDashboardAggregation, dashboardExecutor);
        CompletableFuture<AggregationResult> deviceFuture =
                CompletableFuture.supplyAsync(deviceRepository::getTotalDevices, dashboardExecutor);
        CompletableFuture<BankStatusResponse> bankStatusFuture =
                CompletableFuture.supplyAsync(
                                () -> {
                                    ResponseEntity<BankStatusResponse> response = workersClient.getBankStatus();
                                    return response.getBody();
                                }, dashboardExecutor)
                        .exceptionally(ex -> {
                            log.warn("Failed to fetch bank status for dashboard: {}", ex.getMessage());
                            return BankStatusResponse.builder()
                                    .locked(false)
                                    .accounts(Collections.emptyList())
                                    .build();
                        });

        try {
            CompletableFuture.allOf(companyFuture, transactionFuture, deviceFuture, bankStatusFuture).join();
        } catch (CompletionException e) {
            throw new AppException(500, "Dashboard aggregation failed", e.getCause().getMessage());
        }

        return new DashboardResponse(
                transactionFuture.join(),
                companyFuture.join(),
                deviceFuture.join().getResult(),
                bankStatusFuture.join()
        );
    }

    public BigDecimal getCompaniesTotalDebt() {
        return companyRepository.getTotalNegativeBalance().getResult();
    }

}
