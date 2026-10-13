package farit.dev.service.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthUserServiceImplTest {

    private final AuthUserServiceImpl service = new AuthUserServiceImpl();

    @Test
    void correctCredentials() {
        assertTrue(service.isValidUser("user123", "password123"));
    }

    @Test
    void wrongLogin() {
        assertFalse(service.isValidUser("anotherUser", "password123"));
    }
}
