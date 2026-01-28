package org.asupg.asupgservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "devices")
@CompoundIndexes({
        @CompoundIndex(def = "{ 'status': 1, 'freeUntil': 1, 'lastBilledMonth': 1, 'companyInn': 1 }")

})
public class DeviceDTO {

    @Id
    @Schema(description = "UUID of the device", example = "78asd6f8as9d78f689asdf")
    private String deviceId;

    @Schema(description = "Name of the device", example = "Test device")
    private String deviceName;

    @Schema(description = "INN of the company that owns the device", example = "123456789")
    private String companyInn;

    @Schema(description = "Type of the device", example = "Generic")
    private String deviceType;

    @Field(targetType = FieldType.DECIMAL128)
    @Schema(description = "Monthly subscription cost of the device", example = "100000")
    private BigDecimal monthlyRate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "When the device was activated", example = "2024-01-17")
    private LocalDate activatedAt;

    @Field(targetType = FieldType.STRING)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM")
    @Schema(description = "When does company start paying for the subscription. After 1 year from creation date if not specified", example = "2027-01")
    private YearMonth freeUntil;

    @Field(targetType = FieldType.STRING)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM")
    @Schema(description = "Last time when the company was charged for the device", example = "2026-01")
    private YearMonth lastBilledMonth;

    @Schema(description = "Status of the device", example = "ACTIVE")
    private DeviceStatus status;

}
