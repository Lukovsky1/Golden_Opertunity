package com.GoldenOpportunity.dbLogin;

import java.util.Set;

/**
 * Shared email validation rules for authentication flows.
 */
public final class EmailValidator {
    private static final Set<String> SUPPORTED_DOMAINS = Set.of(
            "gmail.com",
            "yahoo.com",
            "outlook.com",
            "hotmail.com",
            "icloud.com",
            "aol.com",
            "protonmail.com",
            "live.com",
            "msn.com",
            "golden.com"
    );

    private EmailValidator() {
    }

    public static boolean isValidEmail(String email) {
        String normalizedEmail = normalize(email);
        int atIndex = normalizedEmail.indexOf('@');

        if (atIndex <= 0 || atIndex != normalizedEmail.lastIndexOf('@') || atIndex == normalizedEmail.length() - 1) {
            return false;
        }

        String localPart = normalizedEmail.substring(0, atIndex);
        String domain = normalizedEmail.substring(atIndex + 1);

        return !localPart.isBlank() && SUPPORTED_DOMAINS.contains(domain);
    }

    public static boolean looksLikeEmail(String value) {
        return normalize(value).contains("@");
    }

    public static String supportedDomainsMessage() {
        return "Please use a supported email domain such as @gmail.com or @yahoo.com.";
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
