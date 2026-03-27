package com.app.b_and_t_lms.services;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import com.app.b_and_t_lms.dto.ApiResponse;
import com.app.b_and_t_lms.dto.LoginRequest;
import com.app.b_and_t_lms.dto.UserData;
import com.app.b_and_t_lms.models.User;
import com.app.b_and_t_lms.repositories.UserRepository;
import com.app.b_and_t_lms.util.DataValidator;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RememberMeServices rememberMeServices;

    @Autowired
    private UserRepository userRepository;

    public ApiResponse<UserData> login(LoginRequest loginRequest, HttpServletRequest request,
            HttpServletResponse response) {

        ApiResponse<?> resp = DataValidator.validate(loginRequest);

        if (!resp.isSuccess()) {
            return new ApiResponse<>(false, resp.getMessage(), null);
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()));

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            new HttpSessionSecurityContextRepository()
                    .saveContext(context, request, response);

            if (loginRequest.getRememberMe()) {
                rememberMeServices.loginSuccess(request, response, authentication);
            }

            User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow();

            user.setPrevLogin(user.getLastLogin());
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            return new ApiResponse<>(true, "Login successful", new UserData(user));

        } catch (DisabledException ex) {
            return new ApiResponse<>(false, "Your account is inactive. Please contact support.", null);
        } catch (LockedException ex) {
            return new ApiResponse<>(false, "Your account is locked. Please contact support.", null);
        } catch (AuthenticationException ex) {
            System.out.println(ex);
            return new ApiResponse<>(false, "Invalid email or password", null);
        }
    }

    public ApiResponse<?> me(Authentication authentication) {
        User user = authentication != null ? (User) authentication.getPrincipal() : null;

        if (user == null) {
            return new ApiResponse<>(false, "Please login", null);
        }

        return new ApiResponse<>(true, "me", new UserData(user));

    }

    public void logout(HttpSession session, HttpServletResponse response) {

        session.invalidate();

        Cookie jsessionidCookie = new Cookie("JSESSIONID", null);
        jsessionidCookie.setPath("/");
        jsessionidCookie.setMaxAge(0);
        response.addCookie(jsessionidCookie);

        Cookie rememberMeCookie = new Cookie("remember-me", null);
        rememberMeCookie.setPath("/");
        rememberMeCookie.setMaxAge(0);
        response.addCookie(rememberMeCookie);
    }

}
