package org.asupg.asupgservice.client;

import org.asupg.asupgservice.client.model.request.BankConfigUpdateRequest;
import org.asupg.asupgservice.client.model.response.BankConfigResponse;
import org.asupg.asupgservice.client.model.response.BankStatusResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@FeignClient(
        name = "job-service",
        url = "${asupg-workers.url}",
        configuration = WorkersClientConfig.class
)
public interface WorkersClient {

    @PostMapping("/v1/monthly-charge/execute")
    ResponseEntity<Void> runMonthlyCharge(@RequestParam String billingMonth);

    @PostMapping("/v1/report-ingest/execute")
    ResponseEntity<Void> runReportIngest(
            @RequestParam LocalDate date
    );

    // Bank accounts
    @GetMapping("/v1/bank/status")
    ResponseEntity<BankStatusResponse> getBankStatus();

    // Bank config
    @GetMapping("/v1/bank-config")
    ResponseEntity<BankConfigResponse> getBankConfig();

    @PatchMapping("/v1/bank-config")
    ResponseEntity<BankConfigResponse> updateBankConfig(@RequestBody BankConfigUpdateRequest request);

    @GetMapping("/v1/bank-config/password")
    ResponseEntity<String> getBankPassword();

    @GetMapping("/v1/bank-config/username")
    ResponseEntity<String> getBankUsername();

}
