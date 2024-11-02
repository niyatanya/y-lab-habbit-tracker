package org.home.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.home.model.Role;
import org.home.service.AuthService;

import java.io.IOException;

import static org.home.model.Role.ADMIN;
import static org.home.model.Role.USER;

@AllArgsConstructor
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String authHeader = httpRequest.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                var userClaims = AuthService.validateToken(token);
                httpRequest.setAttribute("email", userClaims.getEmail());
                httpRequest.setAttribute("role", userClaims.getRole());

                Role role = userClaims.getRole();
                String path = httpRequest.getRequestURI();

                if (!isAuthorized(role, path)) {
                    httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }

                chain.doFilter(request, response);

            } catch (Exception e) {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.getWriter().write("Invalid token.");
            }

        } else {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.getWriter().write("Authorization header is missing.");
        }
    }

    private boolean isAuthorized(Role role, String path) {
        if (ADMIN.equals(role)) {
            return true;
        } else if (USER.equals(role)) {
            return !path.startsWith("/admin");
        }
        return false;
    }
}
