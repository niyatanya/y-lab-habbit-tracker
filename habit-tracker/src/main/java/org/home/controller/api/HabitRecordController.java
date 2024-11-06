package org.home.controller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.home.dto.ErrorResponseDTO;
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

/**
 * REST controller for managing habit records.
 */
@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class HabitRecordController {

    private final HabitRecordService recordService;

    /**
     * Retrieves all records for a specified user's habit.
     *
     * @param email      the email of the user whose habit records are to be retrieved
     * @param habitTitle the title of the habit for which records are being retrieved
     * @return a response entity containing the user's habit records
     */
    @PreAuthorize("#email == authentication.principal.username or hasRole('ADMIN')")
    @GetMapping(value = "/{email}/{habitTitle}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllRecords(@PathVariable("email") String email,
                                           @PathVariable("habitTitle") String habitTitle) {
        Map<LocalDate, HabitRecordDTO> records = recordService.getAllRecords(email, habitTitle);
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(records.size()))
                .body(records);
    }

    /**
     * Creates a new record for a specified habit of a user.
     *
     * @param email      the email of the user for whom the record is being created
     * @param habitTitle the title of the habit for which the record is being created
     * @param recordDTO  the record data to be created
     * @return a response entity containing the newly created record or a conflict error message
     */
    @PreAuthorize("#email == authentication.principal.username or hasRole('ADMIN')")
    @PostMapping("/{email}/{habitTitle}")
    public ResponseEntity<?> createRecord(@PathVariable("email") String email,
                                          @PathVariable("habitTitle") String habitTitle,
                                          @Valid @RequestBody HabitRecordDTO recordDTO) {
        HabitRecordDTO newRecordDTO = recordService.createRecord(email, habitTitle, recordDTO);
        if (newRecordDTO == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO("Record with this date already exists"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(newRecordDTO);
    }

    /**
     * Updates an existing record for a specified habit of a user.
     *
     * @param email                the email of the user whose record is to be updated
     * @param habitTitle           the title of the habit for which the record is to be updated
     * @param recordDTOToUpdate    the updated record data
     * @return a response entity containing the updated record or a conflict error message
     */
    @PreAuthorize("#email == authentication.principal.username or hasRole('ADMIN')")
    @PutMapping("/{email}/{habitTitle}")
    public ResponseEntity<?> editRecord(@PathVariable("email") String email,
                                        @PathVariable("habitTitle") String habitTitle,
                                        @Valid @RequestBody HabitRecordDTO recordDTOToUpdate) {
        HabitRecordDTO updatedRecordDTO = recordService.editRecord(email, habitTitle, recordDTOToUpdate);
        if (updatedRecordDTO == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO("Record with this completion status already exists"));
        }
        return ResponseEntity.ok(updatedRecordDTO);
    }

    /**
     * Deletes a specified record for a user's habit.
     *
     * @param email                the email of the user whose record is to be deleted
     * @param habitTitle           the title of the habit for which the record is to be deleted
     * @param recordDTOToDelete    the record data to be deleted
     * @return a response entity indicating the result of the deletion operation
     */
    @PreAuthorize("#email == authentication.principal.username or hasRole('ADMIN')")
    @DeleteMapping("/{email}/{habitTitle}")
    public ResponseEntity<?> deleteRecord(@PathVariable("email") String email,
                                          @PathVariable("habitTitle") String habitTitle,
                                          @Valid @RequestBody HabitRecordDTO recordDTOToDelete) {
        boolean deleteResult = recordService.deleteRecord(email, habitTitle, recordDTOToDelete);
        if (!deleteResult) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDTO("Record not found"));
        }
        return ResponseEntity.noContent().build();
    }
}
