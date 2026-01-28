package org.asupg.asupgservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Document(collection = "transactions")
@CompoundIndexes({
        @CompoundIndex(
                name = "inn_type_idx",
                def = "{ 'counterpartyInn': 1, 'transactionType': 1 }"
        )
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionDTO {

    @Id
    private String transactionId;

    private String counterpartyInn;

    private String deviceId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;

    private String counterpartyName;

    private String accountNumber;

    private String mfo;

    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal amount;

    private String description;

    private TransactionType transactionType;

    private ReconciliationDTO reconciliation;

    @Version
    @JsonIgnore
    private Long version;

    @NoArgsConstructor
    public enum TransactionType {
        BANK_PAYMENT,      // Payment from customer (external)
        MONTHLY_CHARGE
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
