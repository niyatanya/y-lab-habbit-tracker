package org.home.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) for a habit record.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class HabitRecordDTO {

    @NotNull
    private LocalDate date;

    @NotNull
    private boolean completed;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public LocalDate getDate() {
        return date;
    }
}
