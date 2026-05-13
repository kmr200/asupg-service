package org.asupg.asupgservice.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.asupg.asupgservice.model.DeviceStatus;
import org.asupg.asupgservice.validation.ValidationDoc;

import java.math.BigDecimal;
import java.time.YearMonth;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Request class for updating a device")
public class DeviceUpdateRequest {

    @Schema(description = "Name of the device to be update", example = "Device OOO \"TEST\"")
    private String deviceName;

    @Pattern(
            regexp = "^\\d{9}|\\d{14}$",
            message = ValidationDoc.INN_SIZE_MESSAGE
    )
    @Schema(description = "INN of the company to reassign the device", example = "123456789")
    private String companyInn;

    @Schema(description = "Monthly charge for the device")
    private BigDecimal monthlyRate;

    @Schema(description = "Till when the device is free of charge (last month included). This month + 1 year used if not specified")
    private YearMonth freeUntil;

    @Schema(description = "If the device is created active/inactive")
    private DeviceStatus status = DeviceStatus.ACTIVE;

}
