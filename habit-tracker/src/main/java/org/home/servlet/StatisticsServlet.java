package org.home.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.home.dto.StatisticsInputDTO;
import org.home.service.AuthService;
import org.home.service.StatisticsService;

import java.io.IOException;

import java.util.Map;

import static org.home.model.Role.ADMIN;

@WebServlet("/secured/statistics")
public class StatisticsServlet extends HttpServlet {

    private final StatisticsService statisticsService;
    private final ObjectMapper objectMapper;

    public StatisticsServlet(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
            StatisticsInputDTO input = objectMapper.readValue(req.getReader(), StatisticsInputDTO.class);
            Map<String, Map<String, String>> result = statisticsService.generateProgressReport(input);
            Map<String, String> innerMap = result.get(String.format(
                    "Progress Report for Habit: %s", input.getHabitTitle()));
            if (innerMap.containsKey("Error: ")) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getWriter(), result);
            } else {
                resp.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(resp.getWriter(), result);
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), Map.of("error",
                    "Unable to retrieve statistics: " + e.getMessage()));
        }
    }
}
