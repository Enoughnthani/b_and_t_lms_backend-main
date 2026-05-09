package com.app.b_and_t_lms.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RsaIdInfo {

    /**
     * Extract date of birth from SA ID
     */
    public static LocalDate getDateOfBirth(String idNumber) {
        if (idNumber == null || idNumber.length() != 13 || !idNumber.matches("\\d{13}")) {
            throw new IllegalArgumentException("Invalid SA ID number");
        }

        String dobPart = idNumber.substring(0, 6); // YYMMDD
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
        LocalDate dob = LocalDate.parse(dobPart, formatter);

        // Adjust century
        int yy = Integer.parseInt(dobPart.substring(0, 2));
        int currentYear = LocalDate.now().getYear() % 100;

        if (yy <= currentYear) {
            dob = dob.withYear(2000 + yy);
        } else {
            dob = dob.withYear(1900 + yy);
        }

        return dob;
    }

    /**
     * Extract gender from SA ID
     */
    public static String getGender(String idNumber) {
        if (idNumber == null || idNumber.length() != 13 || !idNumber.matches("\\d{13}")) {
            throw new IllegalArgumentException("Invalid SA ID number");
        }

        String seqPart = idNumber.substring(6, 10); // SSSS
        int seq = Integer.parseInt(seqPart);

        return seq >= 5000 ? "Male" : "Female";
    }
}
