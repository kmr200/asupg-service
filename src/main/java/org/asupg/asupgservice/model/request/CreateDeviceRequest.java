package org.asupg.asupgservice.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.asupg.asupgservice.model.DeviceStatus;
import org.asupg.asupgservice.validation.ValidationDoc;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "Request class for creating a device")
public class CreateDeviceRequest {

    @Schema(description = "ID of the device to be created. Random UUID will be generated if not supplied", example = "f5f7ca08-540c-4f8b-90b7-f8fd86a8582b")
    private String deviceId = UUID.randomUUID().toString();

    @NotBlank(message = ValidationDoc.DEVICE_NAME_BLANK_MESSAGE)
    @Schema(description = "Name of the device to be created", example = "Device OOO \"TEST\"")
    private String deviceName;

    @NotBlank(message = ValidationDoc.INN_BLANK_MESSAGE)
    @Size(min = 9, max = 9, message = ValidationDoc.INN_SIZE_MESSAGE)
    @Schema(description = "INN of the company to assign the device", example = "123456789", requiredMode = Schema.RequiredMode.REQUIRED)
    private String companyInn;

    @Schema(description = "Monthly charge for the device", defaultValue = "100000")
    private BigDecimal monthlyRate;

    @Schema(description = "Till when the device is free of charge (last month included). This month + 1 year used if not specified")
    private YearMonth freeUntil;

    @Schema(description = "If the device is created active/inactive", defaultValue = "ACTIVE")
    private DeviceStatus status = DeviceStatus.ACTIVE;

}
