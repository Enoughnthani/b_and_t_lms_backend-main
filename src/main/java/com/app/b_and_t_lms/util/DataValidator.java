package com.app.b_and_t_lms.util;

import java.lang.reflect.Field;
import java.util.List;

import com.app.b_and_t_lms.dto.ApiResponse;

public class DataValidator {

    public static ApiResponse<?> validate(Object obj) {

        if (obj == null) {
            return new ApiResponse<>(false, "Object cannot be null", null);
        }

        Field[] fields = obj.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            try {
                Object value = field.get(obj);
                String fieldName = field.getName();

                fieldName = fieldName.substring(0, 1).toUpperCase()
                        + fieldName.substring(1);

                if (value == null) {
                    return new ApiResponse<>(false, fieldName + " is required", null);
                }
               
                if (value instanceof String &&
                        ((String) value).replace("<p>", "").replace("</p>", "").trim().isEmpty()) {
                    return new ApiResponse<>(false, fieldName + " is required", null);
                }

                if (value instanceof List<?> &&
                        ((List<?>) value).isEmpty()) {
                    return new ApiResponse<>(false, fieldName + " is required", null);
                }

            } catch (IllegalAccessException e) {
                return new ApiResponse<>(false, "Failed to validate data", null);
            }
        }

        return new ApiResponse<>(true, "Valid", null);
    }
}