package org.home.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.home.model.Frequency;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class HabitDTO {
    private String title;
    private String description;
    private Frequency frequency;
}
