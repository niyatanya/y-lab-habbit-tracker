package org.home.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.home.annotations.LoggableUserAction;
import org.home.dto.LoginInputDTO;
import org.home.dto.LoginOutputDTO;
import org.home.model.User;
import org.home.service.AuthService;
import org.home.service.UserService;
import org.home.validation.LoginInputDTOValidator;

import java.io.IOException;
import java.util.Map;

@NoArgsConstructor
@LoggableUserAction
public class LoginServlet extends HttpServlet {

    private AuthService authService;
    private UserService userService;
    private ObjectMapper objectMapper;

    public LoginServlet(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        LoginInputDTO inputDTO = objectMapper.readValue(req.getReader(), LoginInputDTO.class);
        LoginInputDTOValidator.validate(inputDTO);

        User user = userService.findUserByEmail(inputDTO.getEmail());
        if (user != null) {
            if (userService.validatePassword(user, inputDTO.getPassword())
                    && !user.isBlocked()) {
                String token = authService.loginUser(user);
                objectMapper.writeValue(resp.getWriter(), new LoginOutputDTO(token));
                resp.setStatus(HttpServletResponse.SC_OK);
                return;
            }
        }
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        objectMapper.writeValue(resp.getWriter(), Map.of("error", "Invalid email or password"));
    }
}
