package org.home.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.home.annotations.LoggableUserAction;
import org.home.dto.UserCreateDTO;
import org.home.dto.UserDTO;
import org.home.service.AuthService;
import org.home.service.UserService;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.home.model.Role.ADMIN;

@WebServlet("/secured/users/*")
public class UserServlet extends HttpServlet {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    public UserServlet(UserService userService) {
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        var userSession = (AuthService.UserSession) req.getAttribute("userSession");

        if (!ADMIN.equals(userSession.getRole())) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            objectMapper.writeValue(resp.getWriter(), Map.of("error", "Access denied. Admins only."));
            return;
        }

        try {
            Map<String, UserDTO> userMap = userService.getAllUsers();
            List<UserDTO> users = userMap.values().stream().toList();
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), users);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), Map.of("error", "Unable to retrieve users"));
        }
    }

    @LoggableUserAction
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getPathInfo().split("/")[1];
        var userSession = (AuthService.UserSession) req.getAttribute("userSession");

        if (!userSession.getEmail().equals(email) && !ADMIN.equals(userSession.getRole())) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            objectMapper.writeValue(resp.getWriter(), Map.of("error",
                    "Access denied. Unauthorized update attempt."));
            return;
        }

        try {
            UserCreateDTO userCreateDTO = objectMapper.readValue(req.getReader(), UserCreateDTO.class);
            UserDTO updatedUserDTO = userService.editProfile(email, userCreateDTO);

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            if (updatedUserDTO == null) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                objectMapper.writeValue(resp.getWriter(), Map.of("error", "Email is already registered"));
            } else {
                resp.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(resp.getWriter(), updatedUserDTO);
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), Map.of("error", "Invalid request: " + e.getMessage()));
        }
    }

    @LoggableUserAction
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getPathInfo().split("/")[1];
        var userSession = (AuthService.UserSession) req.getAttribute("userSession");

        if (!userSession.getEmail().equals(email) && !ADMIN.equals(userSession.getRole())) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            objectMapper.writeValue(resp.getWriter(), Map.of("error",
                    "Access denied. Unauthorized delete attempt."));
            return;
        }

        try {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            boolean deleteResult = userService.deleteUser(email);
            if (!deleteResult) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                objectMapper.writeValue(resp.getWriter(), Map.of("error", "Cannot delete an admin user."));
            } else {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(resp.getWriter(), Map.of("error", "User not found: " + e.getMessage()));
        }
    }
}
