package org.home.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StatisticsInputDTO {
    private String email;
    private String habitTitle;
    private LocalDate startDate;
    private LocalDate endDate;
}
