package org.home.controller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.home.dto.HabitRecordDTO;
import org.home.service.HabitRecordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class HabitRecordController {

    private final HabitRecordService recordService;

    @PreAuthorize("#email == authentication.principal.username or hasRole('ADMIN')")
    @GetMapping(value = "/{email}/{habitTitle}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllRecords(@PathVariable("email") String email,
                                           @PathVariable("habitTitle") String habitTitle) {
        Map<LocalDate, HabitRecordDTO> records = recordService.getAllRecords(email, habitTitle);
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(records.size()))
                .body(records);
    }

    @PreAuthorize("#email == authentication.principal.username or hasRole('ADMIN')")
    @PostMapping("/{email}/{habitTitle}")
    public ResponseEntity<?> createRecord(@PathVariable("email") String email,
                                          @PathVariable("habitTitle") String habitTitle,
                                          @Valid @RequestBody HabitRecordDTO recordDTO) {
        HabitRecordDTO newRecordDTO = recordService.createRecord(email, habitTitle, recordDTO);
        if (newRecordDTO == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Record with this date already exists"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(newRecordDTO);
    }

    @PreAuthorize("#email == authentication.principal.username or hasRole('ADMIN')")
    @PutMapping("/{email}/{habitTitle}")
    public ResponseEntity<?> editRecord(@PathVariable("email") String email,
                                        @PathVariable("habitTitle") String habitTitle,
                                        @Valid @RequestBody HabitRecordDTO recordDTOToUpdate) {
        HabitRecordDTO updatedRecordDTO = recordService.editRecord(email, habitTitle, recordDTOToUpdate);
        if (updatedRecordDTO == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Record with this completion status already exists"));
        }
        return ResponseEntity.ok(updatedRecordDTO);
    }

    @PreAuthorize("#email == authentication.principal.username or hasRole('ADMIN')")
    @DeleteMapping("/{email}/{habitTitle}")
    public ResponseEntity<?> deleteRecord(@PathVariable("email") String email,
                                          @PathVariable("habitTitle") String habitTitle,
                                          @Valid @RequestBody HabitRecordDTO recordDTOToDelete) {
        boolean deleteResult = recordService.deleteRecord(email, habitTitle, recordDTOToDelete);
        if (!deleteResult) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Record not found"));
        }
        return ResponseEntity.noContent().build();
    }
}
