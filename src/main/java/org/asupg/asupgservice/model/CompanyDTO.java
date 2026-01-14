package org.asupg.asupgservice.model;

import com.azure.spring.data.cosmos.core.mapping.Container;
import com.azure.spring.data.cosmos.core.mapping.PartitionKey;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    private String inn;

    private String name;

    private BigDecimal monthlyRate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate subscriptionStartDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM")
    private YearMonth billingStartMonth;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM")
    private YearMonth lastBilledMonth;

    private BigDecimal currentBalance;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime balanceUpdatedAt;

    private CompanyStatus status;

    private String email;

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
