package com.akirabane.backend.security;

import com.akirabane.backend.model.UserModel;
import com.akirabane.backend.model.UserRoleModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static UserModel getCurrentUserOrThrow() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !(authentication.getPrincipal() instanceof UserModel)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        return (UserModel) authentication.getPrincipal();
    }

    public static boolean isAdmin(UserModel user) {
        return user.getRole() == UserRoleModel.ADMIN;
    }

    public static void assertCurrentUserOrAdmin(Long requestedUserId) {
        UserModel current = getCurrentUserOrThrow();

        if (!isAdmin(current) && !current.getId().equals(requestedUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }
}