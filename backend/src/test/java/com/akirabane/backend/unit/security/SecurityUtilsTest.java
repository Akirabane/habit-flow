package com.akirabane.backend.unit.security;

import com.akirabane.backend.model.UserModel;
import com.akirabane.backend.model.UserRoleModel;
import com.akirabane.backend.security.SecurityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SecurityUtilsTest {

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getCurrentUserOrThrow_should_throw_when_no_authentication() {
        // GIVEN: pas d'authentification dans le SecurityContext

        // WHEN / THEN
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                SecurityUtils::getCurrentUserOrThrow
        );

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void getCurrentUserOrThrow_should_return_user_when_authenticated() {
        // GIVEN
        UserModel user = new UserModel();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setRole(UserRoleModel.USER);

        var auth = new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of() // pas besoin de rôles ici
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // WHEN
        UserModel current = SecurityUtils.getCurrentUserOrThrow();

        // THEN
        assertThat(current.getId()).isEqualTo(1L);
        assertThat(current.getEmail()).isEqualTo("user@example.com");
    }

    @Test
    void assertCurrentUserOrAdmin_should_pass_for_same_user() {
        // GIVEN
        UserModel user = new UserModel();
        user.setId(1L);
        user.setRole(UserRoleModel.USER);

        var auth = new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // WHEN / THEN (ne doit PAS lever d'exception)
        SecurityUtils.assertCurrentUserOrAdmin(1L);
    }

    @Test
    void assertCurrentUserOrAdmin_should_pass_for_admin_on_different_user() {
        // GIVEN
        UserModel admin = new UserModel();
        admin.setId(99L);
        admin.setRole(UserRoleModel.ADMIN);

        var auth = new UsernamePasswordAuthenticationToken(
                admin,
                null,
                List.of()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // WHEN / THEN (ne doit pas lever)
        SecurityUtils.assertCurrentUserOrAdmin(1L);
    }

    @Test
    void assertCurrentUserOrAdmin_should_throw_for_other_non_admin_user() {
        // GIVEN
        UserModel user = new UserModel();
        user.setId(2L);
        user.setRole(UserRoleModel.USER);

        var auth = new UsernamePasswordAuthenticationToken(
                user,
                null,
                List.of()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // WHEN / THEN
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> SecurityUtils.assertCurrentUserOrAdmin(1L)
        );

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
