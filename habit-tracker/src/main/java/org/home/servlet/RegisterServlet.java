package org.home.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.home.annotations.LoggableUserAction;
import org.home.dto.UserCreateDTO;
import org.home.dto.UserDTO;
import org.home.service.UserService;
import org.home.validation.UserCreateDTOValidator;

import java.io.IOException;
import java.util.Map;

@NoArgsConstructor
@LoggableUserAction
public class RegisterServlet extends HttpServlet {

    private UserService userService;
    private ObjectMapper objectMapper;

    public RegisterServlet(UserService userService) {
        this.userService = userService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            UserCreateDTO userCreateDTO = objectMapper.readValue(req.getReader(), UserCreateDTO.class);
            UserCreateDTOValidator.validate(userCreateDTO);
            UserDTO newUserDTO = userService.register(userCreateDTO);

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            if (newUserDTO == null) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                objectMapper.writeValue(resp.getWriter(), Map.of("error", "Email is already registered"));
            } else {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                objectMapper.writeValue(resp.getWriter(), newUserDTO);
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), Map.of("error", "Invalid input: " + e.getMessage()));
        }
    }
}
