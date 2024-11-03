package org.home.controller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.home.dto.HabitDTO;
import org.home.service.HabitService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/habits")
@RequiredArgsConstructor
public class HabitController {

    private final HabitService habitService;

    @PreAuthorize("#email == authentication.principal.username or hasRole('ADMIN')")
    @GetMapping(value = "/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllHabits(@PathVariable("email") String email) {
        Map<String, HabitDTO> habits = habitService.getAllHabits(email);
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(habits.size()))
                .body(habits);
    }

    @PreAuthorize("#email == authentication.principal.username or hasRole('ADMIN')")
    @PostMapping(value = "/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createHabit(@PathVariable("email") String email, @Valid @RequestBody HabitDTO habitDTO) {
        HabitDTO newHabitDTO = habitService.createHabit(email, habitDTO);
        if (newHabitDTO == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Habit with this title already exists"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(newHabitDTO);
    }

    @PreAuthorize("#email == authentication.principal.username or hasRole('ADMIN')")
    @PutMapping(value = "/{email}/{title}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> editHabit(@PathVariable("email") String email, @PathVariable("title") String title,
                                       @Valid @RequestBody HabitDTO habitDTOToUpdate) {
        HabitDTO updatedHabitDTO = habitService.editHabit(email, title, habitDTOToUpdate);
        if (updatedHabitDTO == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Habit with this title already exists"));
        }
        return ResponseEntity.ok(updatedHabitDTO);
    }

    @PreAuthorize("#email == authentication.principal.username or hasRole('ADMIN')")
    @DeleteMapping(value = "/{email}/{title}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteHabit(@PathVariable("email") String email, @PathVariable("title") String title) {
        boolean deleteResult = habitService.deleteHabit(email, title);
        if (!deleteResult) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Habit not found"));
        }
        return ResponseEntity.noContent().build();
    }
}
