package org.asupg.asupgservice.api;

import org.asupg.asupgservice.model.CompanyDTO;
import org.asupg.asupgservice.model.request.CompanyDebtSearchRequest;
import org.asupg.asupgservice.model.request.CompanySearchRequest;
import org.asupg.asupgservice.model.request.CreateCompanyRequest;
import org.asupg.asupgservice.model.response.CompanyBalanceResponse;
import org.asupg.asupgservice.model.response.CompanyDebtResponse;
import org.asupg.asupgservice.model.response.CompanySearchResponse;
import org.asupg.asupgservice.service.CompanyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping
    public ResponseEntity<CompanyDTO> createCompany(
            @Validated @RequestBody CreateCompanyRequest createCompanyRequest
    ) {
        CompanyDTO companyDTO = companyService.createCompany(
                createCompanyRequest.getInn(),
                createCompanyRequest.getName(),
                createCompanyRequest.getMonthlyRate(),
                createCompanyRequest.getBillingStartMonth(),
                createCompanyRequest.getEmail(),
                createCompanyRequest.getPhone()
        );

        return new ResponseEntity<>(companyDTO, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<CompanySearchResponse> getCompanies(
            @RequestBody @Validated CompanySearchRequest companyDebtSearchRequest
    ) {
        CompanySearchResponse response = companyService.getCompanies(
                companyDebtSearchRequest.getMinBalance(),
                companyDebtSearchRequest.getMaxBalance(),
                companyDebtSearchRequest.getSubscriptionStartDateFrom(),
                companyDebtSearchRequest.getSubscriptionStartDateTo(),
                companyDebtSearchRequest.getBillingStartMonthFrom(),
                companyDebtSearchRequest.getBillingStartMonthTo(),
                companyDebtSearchRequest.getStatus(),
                companyDebtSearchRequest.getLimit(),
                companyDebtSearchRequest.getContinuationToken(),
                companyDebtSearchRequest.getSortBy(),
                companyDebtSearchRequest.getSortOrder()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyDTO> getCompany(@PathVariable String id) {
        CompanyDTO company = companyService.getCompany(id);

        return new ResponseEntity<>(company, HttpStatus.OK);
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<CompanyBalanceResponse> getCompanyBalance(@PathVariable String id) {
        CompanyBalanceResponse companyBalance = companyService.getCompanyBalance(id);

        return new ResponseEntity<>(companyBalance, HttpStatus.OK);
    }

    @GetMapping("/debtors")
    public ResponseEntity<CompanyDebtResponse> getCompanyDebtors(
            @RequestBody @Validated CompanyDebtSearchRequest companyDebtSearchRequest
    ) {

        var minDebt = companyDebtSearchRequest.getMinDebt();
        var maxDebt = companyDebtSearchRequest.getMaxDebt();

        minDebt = minDebt == null ? minDebt : minDebt.abs().negate();
        maxDebt = maxDebt == null ? maxDebt : maxDebt.abs().negate();

        CompanyDebtResponse companiesInDebt = companyService.getCompaniesInDebt(
                minDebt,
                maxDebt,
                companyDebtSearchRequest.getLimit() == null ? 50 : companyDebtSearchRequest.getLimit(),
                companyDebtSearchRequest.getContinuationToken(),
                companyDebtSearchRequest.getSortOrder()
        );

        return new ResponseEntity<>(companiesInDebt, HttpStatus.OK);
    }

}
