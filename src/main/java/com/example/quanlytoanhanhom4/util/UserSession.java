package com.example.quanlytoanhanhom4.util;

public final class UserSession {
    private static String currentUsername;
    private static String currentRole;
    private static Integer currentUserId;

    private UserSession() {
        // Utility class
    }

    public static void setUser(String username, String role, Integer userId) {
        currentUsername = username;
        currentRole = role;
        currentUserId = userId;
    }

    public static String getCurrentUsername() {
        return currentUsername;
    }

    public static String getCurrentRole() {
        return currentRole;
    }

    public static Integer getCurrentUserId() {
        return currentUserId;
    }

    public static void clear() {
        currentUsername = null;
        currentRole = null;
        currentUserId = null;
    }
}


