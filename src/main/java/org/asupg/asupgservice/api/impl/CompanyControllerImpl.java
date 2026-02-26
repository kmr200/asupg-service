package org.asupg.asupgservice.api.impl;

import org.asupg.asupgservice.api.CompanyController;
import org.asupg.asupgservice.model.CompanyDTO;
import org.asupg.asupgservice.model.request.CompanyDebtSearchRequest;
import org.asupg.asupgservice.model.request.CompanySearchRequest;
import org.asupg.asupgservice.model.request.CreateCompanyRequest;
import org.asupg.asupgservice.model.response.CompanyBalanceResponse;
import org.asupg.asupgservice.model.response.CompanyDebtResponse;
import org.asupg.asupgservice.model.response.CompanySearchResponse;
import org.asupg.asupgservice.model.response.TotalDebt;
import org.asupg.asupgservice.service.CompanyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/v1/companies")
public class CompanyControllerImpl implements CompanyController {

    private final CompanyService companyService;

    public CompanyControllerImpl(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CompanyDTO> createCompany(
            @Validated @RequestBody CreateCompanyRequest createCompanyRequest
    ) {
        CompanyDTO companyDTO = companyService.createCompany(
                createCompanyRequest.getInn(),
                createCompanyRequest.getName(),
                createCompanyRequest.getEmail(),
                createCompanyRequest.getPhone()
        );

        return new ResponseEntity<>(companyDTO, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<CompanySearchResponse> getCompanies(
            @RequestBody @Validated CompanySearchRequest companyDebtSearchRequest
    ) {
        CompanySearchResponse response = companyService.getCompanies(
                companyDebtSearchRequest.getMinBalance(),
                companyDebtSearchRequest.getMaxBalance(),
                companyDebtSearchRequest.getStatus(),
                companyDebtSearchRequest.getLimit(),
                companyDebtSearchRequest.getCursor(),
                companyDebtSearchRequest.getSortBy(),
                companyDebtSearchRequest.getSortOrder(),
                companyDebtSearchRequest.getSearch()
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<CompanyDTO> getCompany(@PathVariable String id) {
        CompanyDTO company = companyService.getCompany(id);

        return new ResponseEntity<>(company, HttpStatus.OK);
    }

    @GetMapping("/{id}/balance")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<CompanyBalanceResponse> getCompanyBalance(@PathVariable String id) {
        CompanyBalanceResponse companyBalance = companyService.getCompanyBalance(id);

        return new ResponseEntity<>(companyBalance, HttpStatus.OK);
    }

    @GetMapping("/debtors")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<CompanyDebtResponse> getCompanyDebtors(
            @RequestBody @Validated CompanyDebtSearchRequest companyDebtSearchRequest
    ) {

        // User's max debt -> least negative floor
        BigDecimal minBalance = toInternalBalance(companyDebtSearchRequest.getMaxDebt());
        // User's min debt -> most negative ceiling
        BigDecimal maxBalance = toInternalBalance(companyDebtSearchRequest.getMinDebt());

        CompanyDebtResponse companiesInDebt = companyService.getCompaniesInDebt(
                minBalance,
                maxBalance,
                companyDebtSearchRequest.getLimit() == null ? 50 : companyDebtSearchRequest.getLimit(),
                companyDebtSearchRequest.getCursor(),
                companyDebtSearchRequest.getSortOrder(),
                companyDebtSearchRequest.getSearch()
        );

        return new ResponseEntity<>(companiesInDebt, HttpStatus.OK);
    }

    private BigDecimal toInternalBalance(BigDecimal debtAmount) {
        return debtAmount == null ? null : debtAmount.abs().negate();
    }

}
