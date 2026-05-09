package com.app.b_and_t_lms.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.app.b_and_t_lms.filters.ActiveUserFilter;
import com.app.b_and_t_lms.filters.FeedbackRateLimitFilter;
import com.app.b_and_t_lms.models.Role.RoleName;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

<<<<<<< HEAD
    private final FeedbackRateLimitFilter feedbackRateLimitFilter;
    private final ActiveUserFilter activeUserFilter;

    public SecurityConfig(FeedbackRateLimitFilter feedbackRateLimitFilter, ActiveUserFilter activeUserFilter) {
        this.feedbackRateLimitFilter = feedbackRateLimitFilter;
        this.activeUserFilter = activeUserFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/users/**",
                                "/api/auth/**",
                                "/uploads/**",
                                "/a/**")
                        .permitAll()
                        .requestMatchers("/api/users/me").authenticated()
                        .requestMatchers("/api/admin/**").hasRole(RoleName.ADMIN.name())
                        .requestMatchers("/api/content/upload").hasRole(RoleName.FACILITATOR.name())
                        .anyRequest().authenticated())
                .rememberMe(remember -> remember
                        .key("EFFeUvtpiBQyfdUZisSExMgFLawH6e/Rf5YfdGh6lKF5Wy/KthZYdGC2keskGFUwGZI31k0TcAqhF6IFl2zUGg==")
                        .rememberMeParameter("remember-me")
                        .tokenValiditySeconds(7 * 24 * 60 * 60))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .addFilterAfter(feedbackRateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(activeUserFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(
                List.of("http://localhost:5173", "http://10.144.34.186:5173"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        config.setExposedHeaders(List.of("X-RateLimit-Remaining"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    RememberMeServices rememberMeServices(UserDetailsService userDetailsService) {
        TokenBasedRememberMeServices service = new TokenBasedRememberMeServices(
                "EFFeUvtpiBQyfdUZisSExMgFLawH6e/Rf5YfdGh6lKF5Wy/KthZYdGC2keskGFUwGZI31k0TcAqhF6IFl2zUGg==",
                userDetailsService);
        service.setTokenValiditySeconds(7 * 24 * 60 * 60);
        service.setAlwaysRemember(false);
        return service;
    }

=======
        private final FeedbackRateLimitFilter feedbackRateLimitFilter;
        private final ActiveUserFilter activeUserFilter;

        public SecurityConfig(FeedbackRateLimitFilter feedbackRateLimitFilter,
                        ActiveUserFilter activeUserFilter) {
                this.feedbackRateLimitFilter = feedbackRateLimitFilter;
                this.activeUserFilter = activeUserFilter;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                                .headers(headers -> headers
                                                .frameOptions(frame -> frame.sameOrigin())) // Keep for other endpoints

                                // Add this line to disable frame options for uploads
                                .headers(headers -> headers
                                                .frameOptions(frameOptions -> frameOptions.disable())
                                                .addHeaderWriter((request, response) -> {
                                                        String path = request.getRequestURI();
                                                        if (path.startsWith("/uploads/")) {
                                                                // Allow iframe for uploads content
                                                                response.setHeader("X-Frame-Options", "ALLOWALL");
                                                        } else {
                                                                response.setHeader("X-Frame-Options", "SAMEORIGIN");
                                                        }
                                                }))

                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/api/users/**",
                                                                "/api/auth/**",
                                                                "/uploads/**",
                                                                "/a/**")
                                                .permitAll()
                                                .requestMatchers("/api/users/me").authenticated()
                                                .requestMatchers("/api/admin/**").hasRole(RoleName.ADMIN.name())
                                                .requestMatchers("/api/content/upload")
                                                .hasRole(RoleName.FACILITATOR.name())
                                                .anyRequest().authenticated())

                                .rememberMe(remember -> remember
                                                .key("EFFeUvtpiBQyfdUZisSExMgFLawH6e/Rf5YfdGh6lKF5Wy/KthZYdGC2keskGFUwGZI31k0TcAqhF6IFl2zUGg==")
                                                .rememberMeParameter("remember-me")
                                                .tokenValiditySeconds(7 * 24 * 60 * 60))

                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

                                .addFilterAfter(feedbackRateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                                .addFilterBefore(activeUserFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(
                                List.of("http://localhost:5173", "http://10.144.34.186:5173"));
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                config.setAllowedHeaders(List.of("*"));
                config.setAllowCredentials(true);
                config.setExposedHeaders(List.of("X-RateLimit-Remaining"));

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", config);
                return source;
        }

        @Bean
        AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
                return config.getAuthenticationManager();
        }

        @Bean
        RememberMeServices rememberMeServices(UserDetailsService userDetailsService) {
                TokenBasedRememberMeServices service = new TokenBasedRememberMeServices(
                                "EFFeUvtpiBQyfdUZisSExMgFLawH6e/Rf5YfdGh6lKF5Wy/KthZYdGC2keskGFUwGZI31k0TcAqhF6IFl2zUGg==",
                                userDetailsService);
                service.setTokenValiditySeconds(7 * 24 * 60 * 60);
                service.setAlwaysRemember(false);
                return service;
        }
>>>>>>> c13b675b96e1287ac668e4a860527469263bca48
}