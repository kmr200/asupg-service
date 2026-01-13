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
import java.util.Objects;

@Container(containerName = "Transactions", autoCreateContainer = false)
@Getter
@Setter
@NoArgsConstructor
@ToString
public class TransactionDTO {

    @Id
    @JsonProperty("id")
    @JsonIgnore
    private String id;

    @PartitionKey
    private String counterpartyInn;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    private String transactionId;

    private String counterpartyName;

    private String accountNumber;

    private String mfo;

    private BigDecimal amount;

    private String description;

    @JsonProperty("_etag")
    @JsonIgnore
    private String etag;

    public TransactionDTO(
            LocalDate date,
            String transactionId,
            String counterpartyName,
            String counterpartyInn,
            String accountNumber,
            String mfo,
            BigDecimal amount,
            String description
    ) {
        this.id = transactionId;
        this.date = date;
        this.transactionId = transactionId;
        this.counterpartyName = counterpartyName;
        this.counterpartyInn = counterpartyInn;
        this.accountNumber = accountNumber;
        this.mfo = mfo;
        this.amount = amount;
        this.description = description;
    }

    public void setId(String id) {
        this.id = id;
        this.transactionId = id;
    }

    public void setTransactionId(String transactionId) {
        this.id = id;
        this.transactionId = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TransactionDTO that = (TransactionDTO) o;
        return Objects.equals(transactionId, that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(transactionId);
    }

}
