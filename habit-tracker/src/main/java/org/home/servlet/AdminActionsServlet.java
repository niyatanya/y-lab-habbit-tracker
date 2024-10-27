package org.home.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.home.model.User;
import org.home.service.UserService;
import org.home.service.AuthService;

import java.io.IOException;
import java.util.Map;

import static org.home.model.Role.ADMIN;

@WebServlet("/secured/admin/users/*")
public class AdminActionsServlet extends HttpServlet {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    public AdminActionsServlet(UserService userService) {
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        var userSession = (AuthService.UserSession) req.getAttribute("userSession");
        if (!ADMIN.equals(userSession.getRole())) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            objectMapper.writeValue(resp.getWriter(), Map.of("error", "Access denied. Admins only."));
            return;
        }

        String email = req.getPathInfo().split("/")[2];
        User user = userService.findUserByEmail(email);
        if (user != null && user.getRole().equals(ADMIN)) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            objectMapper.writeValue(resp.getWriter(), Map.of("error", "Cannot block an admin user."));
            return;
        }

        try {
            String action = req.getPathInfo().split("/")[1];
            boolean success;
            if (user != null && "block".equalsIgnoreCase(action)) {
                success = userService.blockUser(user);
            } else if (user != null && "unblock".equalsIgnoreCase(action)) {
                success = userService.unblockUser(user);
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                objectMapper.writeValue(resp.getWriter(), Map.of("error",
                        "Invalid action. Use 'block' or 'unblock'."));
                return;
            }

            if (success) {
                resp.setStatus(HttpServletResponse.SC_OK);
                objectMapper.writeValue(resp.getWriter(), Map.of("message",
                        "User " + action + "ed successfully."));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                objectMapper.writeValue(resp.getWriter(), Map.of("error",
                        "User not found or already " + action + "ed."));
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), Map.of("error",
                    "Error processing request: " + e.getMessage()));
        }
    }
}
