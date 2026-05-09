package com.app.b_and_t_lms.filters;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.app.b_and_t_lms.models.User;
import com.app.b_and_t_lms.repositories.UserRepository;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ActiveUserFilter implements Filter {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {

            User user = userRepository.findByEmail(auth.getName()).orElse(null);

            if (user == null || !user.isAccountNonLocked()) {
                SecurityContextHolder.clearContext();
                HttpServletResponse httpResponse = (HttpServletResponse) response;
                httpResponse.setStatus(401);
                httpResponse.setContentType("application/json");
                httpResponse.getWriter()
                        .write("{\"success\":false,\"message\":\"Your account is locked. Please contact support.\"}");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}