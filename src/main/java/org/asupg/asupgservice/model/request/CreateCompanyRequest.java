package org.asupg.asupgservice.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.asupg.asupgservice.validation.ValidationDoc;

import java.time.LocalDate;
import java.time.YearMonth;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateCompanyRequest {

    @NotBlank(message = ValidationDoc.INN_BLANK_MESSAGE)
    @Size(min = 9, max = 9, message = ValidationDoc.INN_SIZE_MESSAGE)
    private String inn;

    @NotBlank(message = ValidationDoc.NAME_BLANK_MESSAGE)
    private String name;

    private Long monthlyRate;

    @Future(message = ValidationDoc.BILLING_START_DATE_FUTURE)
    private YearMonth billingStartMonth;

    @Email(message = ValidationDoc.EMAIL_VALID_MESSAGE)
    private String email;

    private String phone;

}
