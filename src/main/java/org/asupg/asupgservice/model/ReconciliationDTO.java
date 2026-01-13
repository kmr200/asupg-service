package org.asupg.asupgservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ReconciliationDTO {

    private ReconciliationStatus status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime processedAt;

    private String failureReason;

    private boolean manual;

    private String updatedBy;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime updatedAt;

    public ReconciliationDTO(ReconciliationStatus status) {
        this.status = status;
        this.processedAt = LocalDateTime.now();
        manual = false;
    }

    public ReconciliationDTO(ReconciliationStatus status, String failureReason) {
        this.status = status;
        this.failureReason = failureReason;
        this.processedAt = LocalDateTime.now();
        manual = false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ReconciliationDTO that = (ReconciliationDTO) o;
        return manual == that.manual && status == that.status && Objects.equals(processedAt, that.processedAt) && Objects.equals(failureReason, that.failureReason) && Objects.equals(updatedBy, that.updatedBy) && Objects.equals(updatedAt, that.updatedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, processedAt, failureReason, manual, updatedBy, updatedAt);
    }

}
