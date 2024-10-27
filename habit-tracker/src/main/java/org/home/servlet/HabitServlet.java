package org.home.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.home.annotations.LoggableUserAction;
import org.home.dto.HabitDTO;
import org.home.service.AuthService;
import org.home.service.HabitService;
import org.home.validation.HabitDTOValidator;

import java.io.IOException;
import java.util.Map;

import static org.home.model.Role.ADMIN;

@LoggableUserAction
@WebServlet("/secured/habits/*")
public class HabitServlet extends HttpServlet {

    private final HabitService habitService;
    private final ObjectMapper objectMapper;

    public HabitServlet(HabitService habitService) {
        this.habitService = habitService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
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
            Map<String, HabitDTO> habits = habitService.getAllHabits(email);
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), habits);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), Map.of("error", "Unable to retrieve habits"));
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
            HabitDTO habitDTO = objectMapper.readValue(req.getReader(), HabitDTO.class);
            HabitDTOValidator.validate(habitDTO);
            HabitDTO newHabitDTO = habitService.createHabit(email, habitDTO);

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            if (newHabitDTO == null) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                objectMapper.writeValue(resp.getWriter(), Map.of("error",
                        "Habit with this title already exists"));
            } else {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                objectMapper.writeValue(resp.getWriter(), newHabitDTO);
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
            String oldTitle = req.getPathInfo().split("/")[2];
            HabitDTO habitDTOToUpdate = objectMapper.readValue(req.getReader(), HabitDTO.class);
            HabitDTOValidator.validate(habitDTOToUpdate);
            HabitDTO updatedHabitDTO = habitService.editHabit(email, oldTitle, habitDTOToUpdate);

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            if (updatedHabitDTO == null) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                objectMapper.writeValue(resp.getWriter(), Map.of("error",
                        "Habit with this title already exists"));
            } else {
                resp.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(resp.getWriter(), updatedHabitDTO);
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
            String title = req.getPathInfo().split("/")[2];

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            boolean deleteResult = habitService.deleteHabit(email, title);
            if (!deleteResult) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getWriter(), Map.of("error", "Habit not found"));
            } else {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(resp.getWriter(), Map.of("error", "Habit not found: " + e.getMessage()));
        }
    }
}
