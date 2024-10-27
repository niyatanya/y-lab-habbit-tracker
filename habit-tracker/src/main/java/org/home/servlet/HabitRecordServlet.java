package org.home.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.home.annotations.LoggableUserAction;
import org.home.dto.HabitRecordDTO;
import org.home.service.AuthService;
import org.home.service.HabitRecordService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.home.validation.HabitRecordDTOValidator;

import static org.home.model.Role.ADMIN;

@LoggableUserAction
@WebServlet("/secured/records/*")
public class HabitRecordServlet extends HttpServlet {

    private final HabitRecordService recordService;
    private final ObjectMapper objectMapper;

    public HabitRecordServlet(HabitRecordService recordService) {
        this.recordService = recordService;
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String email = req.getPathInfo().split("/")[1];
        var userSession = (AuthService.UserSession) req.getAttribute("userSession");

        if (!userSession.getEmail().equals(email) && !ADMIN.equals(userSession.getRole())) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            objectMapper.writeValue(resp.getWriter(), Map.of("error",
                    "Access denied. Unauthorized delete attempt."));
            return;
        }

        try {
            String habitTitle = req.getPathInfo().split("/")[2];
            Map<LocalDate, HabitRecordDTO> records = recordService.getAllRecords(email, habitTitle);
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), records);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), Map.of("error", "Unable to retrieve records"));
        }
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getPathInfo().split("/")[1];
        var userSession = (AuthService.UserSession) req.getAttribute("userSession");

        if (!userSession.getEmail().equals(email) && !ADMIN.equals(userSession.getRole())) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            objectMapper.writeValue(resp.getWriter(), Map.of("error",
                    "Access denied. Unauthorized delete attempt."));
            return;
        }

        try {
            HabitRecordDTO recordDTO = objectMapper.readValue(req.getReader(), HabitRecordDTO.class);
            HabitRecordDTOValidator.validate(recordDTO);
            String habitTitle = req.getPathInfo().split("/")[2];
            HabitRecordDTO newRecordDTO = recordService.createRecord(email, habitTitle, recordDTO);

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            if (newRecordDTO == null) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                objectMapper.writeValue(resp.getWriter(), Map.of("error",
                        "Record with this date already exists"));
            } else {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                objectMapper.writeValue(resp.getWriter(), newRecordDTO);
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), Map.of("error", "Invalid input: " + e.getMessage()));
        }
    }

    @Override
    public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getPathInfo().split("/")[1];
        var userSession = (AuthService.UserSession) req.getAttribute("userSession");

        if (!userSession.getEmail().equals(email) && !ADMIN.equals(userSession.getRole())) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            objectMapper.writeValue(resp.getWriter(), Map.of("error",
                    "Access denied. Unauthorized delete attempt."));
            return;
        }

        try {
            String habitTitle = req.getPathInfo().split("/")[2];
            HabitRecordDTO recordDTOToUpdate = objectMapper.readValue(req.getReader(), HabitRecordDTO.class);
            HabitRecordDTOValidator.validate(recordDTOToUpdate);
            HabitRecordDTO updatedRecordDTO = recordService.editRecord(email, habitTitle, recordDTOToUpdate);

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            if (updatedRecordDTO == null) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                objectMapper.writeValue(resp.getWriter(), Map.of("error",
                        "Record with this completion status already exists"));
            } else {
                resp.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(resp.getWriter(), updatedRecordDTO);
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), Map.of("error", "Invalid request: " + e.getMessage()));
        }
    }

    @Override
    public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getPathInfo().split("/")[1];
        var userSession = (AuthService.UserSession) req.getAttribute("userSession");

        if (!userSession.getEmail().equals(email) && !ADMIN.equals(userSession.getRole())) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            objectMapper.writeValue(resp.getWriter(), Map.of("error",
                    "Access denied. Unauthorized delete attempt."));
            return;
        }

        try {
            String habitTitle = req.getPathInfo().split("/")[2];
            HabitRecordDTO recordDTOToDelete = objectMapper.readValue(req.getReader(), HabitRecordDTO.class);
            HabitRecordDTOValidator.validate(recordDTOToDelete);

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            boolean deleteResult = recordService.deleteRecord(email, habitTitle, recordDTOToDelete);
            if (!deleteResult) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getWriter(), Map.of("error", "Record not found"));
            } else {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(resp.getWriter(), Map.of("error", "Record not found: " + e.getMessage()));
        }
    }
}
