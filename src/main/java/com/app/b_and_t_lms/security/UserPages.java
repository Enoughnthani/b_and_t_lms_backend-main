package com.app.b_and_t_lms.security;

import java.util.List;
import java.util.Map;

public class UserPages {

    public static Map<String, List<String>> allowedPages = Map.of(
            "LEARNER", List.of("/user/learner_dashboard", "/user/chat", "/user/grades"),
            "FACILITATOR", List.of("/user/facilitator_dashboard", "/user/chat", "/user/grades"),
            "ADMIN", List.of("/user/admin_dashboard", "/user/chat", "/user/grades"));

}
