package io.github.valerioisufi.utils;

import java.util.regex.Pattern;

public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?\\d{8,15}$");

    private ValidationUtils() {}

    public static String validateRequired(String value, String fieldName, int maxLength) {
        if (value == null || value.trim().isEmpty()) return "Il campo " + fieldName + " è obbligatorio";
        if (value.length() > maxLength) return fieldName + " non può superare i " + maxLength + " caratteri";
        return null;
    }

    public static String validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) return null;
        if (email.length() > 45) return "L'email è troppo lunga";
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) return "Formato email non valido";
        return null;
    }

    public static String validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return null;
        if (!PHONE_PATTERN.matcher(phone.trim()).matches()) return "Formato telefono non valido";
        return null;
    }
}