package org.asupg.asupgservice.service;

import org.asupg.asupgservice.exception.AppException;
import org.asupg.asupgservice.model.AggregationResult;
import org.asupg.asupgservice.model.CompanyDashboardResult;
import org.asupg.asupgservice.model.TransactionDashboardResult;
import org.asupg.asupgservice.model.response.DashboardResponse;
import org.asupg.asupgservice.repository.CompanyRepository;
import org.asupg.asupgservice.repository.DeviceRepository;
import org.asupg.asupgservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

@Service
public class ReportService {

    private final Executor dashboardExecutor;
    private final DeviceRepository deviceRepository;
    private final CompanyRepository companyRepository;
    private final TransactionRepository transactionRepository;

    public ReportService(
            @Qualifier("dashboardExecutor") Executor dashboardExecutor,
            DeviceRepository deviceRepository,
            CompanyRepository companyRepository,
            TransactionRepository transactionRepository
    ) {
        this.dashboardExecutor = dashboardExecutor;
        this.deviceRepository = deviceRepository;
        this.companyRepository = companyRepository;
        this.transactionRepository = transactionRepository;
    }

    public DashboardResponse getDashboard() {

        CompletableFuture<CompanyDashboardResult> companyFuture =
                CompletableFuture.supplyAsync(companyRepository::getCompanyDashboardAggregation, dashboardExecutor);
        CompletableFuture<TransactionDashboardResult> transactionFuture =
                CompletableFuture.supplyAsync(transactionRepository::getTransactionDashboardAggregation, dashboardExecutor);
        CompletableFuture<AggregationResult> deviceFuture =
                CompletableFuture.supplyAsync(deviceRepository::getTotalDevices, dashboardExecutor);

        try {
            CompletableFuture.allOf(companyFuture, transactionFuture, deviceFuture).join();
        } catch (CompletionException e) {
            throw new AppException(500, "Dashboard aggregation failed", e.getCause().getMessage());
        }

        return new DashboardResponse(
                transactionFuture.join(),
                companyFuture.join(),
                deviceFuture.join().getResult()
        );

    }

    public BigDecimal getCompaniesTotalDebt() {
        return companyRepository.getTotalNegativeBalance().getResult();
    }

}
