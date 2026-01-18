package org.asupg.asupgservice.model;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Objects;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Container(containerName = "Companies", autoCreateContainer = false)
public class CompanyDTO {

    @Id
    @JsonProperty("id")
    @JsonIgnore
    private String id;

    @PartitionKey
    @Schema(description = "INN of the company to be created", example = "123456789")
    private String inn;

    @Schema(description = "Name of the company to be created", example = "OOO \"TEST\"")
    private String name;

    @Schema(description = "Monthly subscription cost for the company", example = "100000")
    private BigDecimal monthlyRate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "When the company was registered", example = "2024-01-17")
    private LocalDate subscriptionStartDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM")
    @Schema(description = "When does company start paying for the subscription. After 1 year from creation date if not specified", example = "2027-01")
    private YearMonth billingStartMonth;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM")
    @Schema(description = "Last time when the company was charged", example = "2026-01")
    private YearMonth lastBilledMonth;

    @Schema(description = "Current balance of the company", example = "200000")
    private BigDecimal currentBalance;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @Schema(description = "When was the balance of the company was updated")
    private LocalDateTime balanceUpdatedAt;

    @Schema(description = "Status of the company", example = "ACTIVE")
    private CompanyStatus status;

    @Schema(description = "Email contact point of the company", example = "user@example.com")
    private String email;

    @Schema(description = "Phone contact point of the company")
    private String phone;

    @JsonProperty("_etag")
    @JsonIgnore
    private String etag;

    public CompanyDTO(
            String inn,
            String name,
            BigDecimal monthlyRate,
            LocalDate subscriptionStartDate,
            YearMonth billingStartMonth,
            CompanyStatus status,
            String email,
            String phone
    ) {
        this.id = inn;
        this.inn = inn;
        this.name = name;
        this.monthlyRate = monthlyRate;
        this.subscriptionStartDate = subscriptionStartDate;
        this.billingStartMonth = billingStartMonth;
        this.status = status;
        this.email = email;
        this.phone = phone;
    }

    public void setId(String id) {
        this.id = id;
        this.inn = id;
    }

    public void setInn(String inn) {
        this.id = inn;
        this.inn = inn;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CompanyDTO that = (CompanyDTO) o;
        return Objects.equals(inn, that.inn);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(inn);
    }
}
