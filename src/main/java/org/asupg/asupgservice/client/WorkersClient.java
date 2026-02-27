package org.asupg.asupgservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
            @RequestParam LocalDate from,
            @RequestParam LocalDate to
    );

}
