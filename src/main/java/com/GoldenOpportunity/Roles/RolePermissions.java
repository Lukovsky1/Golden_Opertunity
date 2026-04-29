package com.GoldenOpportunity.Roles;

import com.GoldenOpportunity.Login.enums.Role;
import com.GoldenOpportunity.UIState;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Centralized role-to-action mapping for UI authorization.
 *
 * Current policy:
 * - GUEST: browse rooms, shop, checkout, and profile/login flows.
 * - CLERK: all guest capabilities plus room management and reservation management screens.
 * - ADMIN: user-directory access plus credential/user administration.
 */
public final class RolePermissions {
    private RolePermissions() {
    }

    public static boolean canAccessAdminDirectory(Role role) {
        return role == Role.ADMIN;
    }

    public static boolean canManageUsers(Role role) {
        return role == Role.ADMIN;
    }

    public static boolean canAccessClerkTools(Role role) {
        return role == Role.CLERK;
    }

    public static boolean canManageRooms(Role role) {
        return role == Role.CLERK;
    }

    public static boolean canModifyReservations(Role role) {
        return role == Role.CLERK;
    }

    public static boolean requireRole(Component parent, UIState uiState, String actionDescription, String fallbackCard, CardLayout cardLayout, JPanel mainPanel, Role... allowedRoles) {
        Role currentRole = uiState == null ? null : uiState.getCurrentRole();
        boolean allowed = Arrays.stream(allowedRoles).anyMatch(role -> role == currentRole);
        if (allowed) {
            return true;
        }

        JOptionPane.showMessageDialog(
                parent,
                "Access denied. " + actionDescription + " requires role: " + joinRoles(allowedRoles) + ".",
                "Unauthorized",
                JOptionPane.WARNING_MESSAGE
        );

        if (fallbackCard != null && cardLayout != null && mainPanel != null) {
            cardLayout.show(mainPanel, fallbackCard);
        }
        return false;
    }

    private static String joinRoles(Role[] roles) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < roles.length; i++) {
            if (i > 0) {
                builder.append(i == roles.length - 1 ? " or " : ", ");
            }
            builder.append(roles[i].name());
        }
        return builder.toString();
    }
}
