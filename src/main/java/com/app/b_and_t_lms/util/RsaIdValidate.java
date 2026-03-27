package com.app.b_and_t_lms.util;

import java.time.LocalDate;

public class RsaIdValidate {

    public static boolean isValid(String id) {
        if (id == null || !id.matches("\\d{13}"))
            return false;

        if (!isValidBirthdate(id.substring(0, 6)))
            return false;

        int citizenship = Character.getNumericValue(id.charAt(10));
        if (citizenship != 0 && citizenship != 1)
            return false;

        return isValidLuhn(id);
    }

    private static boolean isValidBirthdate(String yymmdd) {
        try {
            int year = Integer.parseInt(yymmdd.substring(0, 2));
            int month = Integer.parseInt(yymmdd.substring(2, 4));
            int day = Integer.parseInt(yymmdd.substring(4, 6));

            if (month < 1 || month > 12)
                return false;

            int fullYear = (year <= LocalDate.now().getYear() % 100) ? 2000 + year : 1900 + year;

            LocalDate.of(fullYear, month, day);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isValidLuhn(String id) {
        int sumOdd = 0;
        for (int i = 0; i <= 10; i += 2) {
            sumOdd += Character.getNumericValue(id.charAt(i));
        }

        StringBuilder evenDigitsStr = new StringBuilder();
        for (int i = 1; i <= 11; i += 2) {
            evenDigitsStr.append(id.charAt(i));
        }

        int evenNumber = Integer.parseInt(evenDigitsStr.toString()) * 2;

        int sumEven = 0;
        for (char c : String.valueOf(evenNumber).toCharArray()) {
            sumEven += Character.getNumericValue(c);
        }

        int total = sumOdd + sumEven;

        int checksum = (10 - (total % 10)) % 10;

        int lastDigit = Character.getNumericValue(id.charAt(12));

        return checksum == lastDigit;
    }
}
