package com.example.quanlytoanhanhom4.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class PasswordUtils {

    private PasswordUtils() {
        // Utility class
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Lỗi khi mã hóa mật khẩu", e);
        }
    }

    public static boolean verifyPassword(String inputPassword, String storedHash) {
        return hashPassword(inputPassword).equals(storedHash);
    }
}


