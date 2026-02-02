package org.asupg.asupgservice.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response class for requesting total debt")
public class TotalDebt {

    @Schema(description = "Total debt", example = "-1111.11")
    BigDecimal totalDebt;

}
