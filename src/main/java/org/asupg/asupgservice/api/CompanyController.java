package org.asupg.asupgservice.api;

import org.asupg.asupgservice.model.CompanyDTO;
import org.asupg.asupgservice.model.request.CreateCompanyRequest;
import org.asupg.asupgservice.model.response.CompanyBalanceResponse;
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

}
