package org.home.controller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.home.dto.StatisticsInputDTO;
import org.home.service.StatisticsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller for handling statistics-related operations.
 */
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * Retrieves statistics for a specific user and habit.
     *
     * @param email the email of the user requesting the statistics
     * @param input the input data for generating the statistics
     * @return a response entity containing the generated statistics or an error message
     */
    @PreAuthorize("#email == authentication.principal.username or hasRole('ADMIN')")
    @GetMapping(value = "/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getStatistics(@PathVariable("email") String email,
                                           @Valid @RequestBody StatisticsInputDTO input) {
        Map<String, Map<String, String>> result = statisticsService.generateProgressReport(input);
        Map<String, String> innerMap = result.get(String.format(
                "Progress Report for Habit: %s", input.getHabitTitle()));
        if (innerMap.containsKey("Error: ")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }
        return ResponseEntity.ok(result);
    }
}
