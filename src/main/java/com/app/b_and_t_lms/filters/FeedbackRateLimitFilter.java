package com.app.b_and_t_lms.filters;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.app.b_and_t_lms.dto.ApiResponse;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper; 

@Component
public class FeedbackRateLimitFilter implements Filter {

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String FILTER_APPLIED = "__feedback_rate_limit_filter_applied";

    private Bucket createBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(3)
                .refillGreedy(3, Duration.ofMinutes(20))
                .build();

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    private Bucket resolveBucket(String key) {
        return buckets.computeIfAbsent(key, k -> createBucket());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        
        // Prevent double filtering
        if (request.getAttribute(FILTER_APPLIED) != null) {
            chain.doFilter(request, response);
            return;
        }
        request.setAttribute(FILTER_APPLIED, true);

        // Skip OPTIONS requests
        if (req.getMethod().equals("OPTIONS")) {
            chain.doFilter(request, response);
            return;
        }

        if (req.getRequestURI().contains("/api/users/public/submit_feedback")) {

            String ip = request.getRemoteAddr();
            Bucket bucket = resolveBucket(ip);

            if (bucket.tryConsume(1)) {
                chain.doFilter(request, response);
            } else {
                ApiResponse<Object> apiResponse = new ApiResponse<>(
                        false,
                        "Too many feedback submissions.",
                        null);

                String jsonResponse = objectMapper.writeValueAsString(apiResponse);

                res.setStatus(429);
                res.setContentType("application/json");
                res.getWriter().write(jsonResponse);
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}