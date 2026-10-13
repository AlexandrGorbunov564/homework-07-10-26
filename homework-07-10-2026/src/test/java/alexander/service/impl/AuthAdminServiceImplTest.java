package alexander.service.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthAdminServiceImplTest {

    private final AuthAdminServiceImpl service = new AuthAdminServiceImpl();

    @Test
    void correctCredentials() {
        assertTrue(service.isValidAdmin("admin123", "ytrewq"));
    }

    @Test
    void wrongPassword() {
        assertFalse(service.isValidAdmin("admin123", "wrong"));
    }

    @Test
    void nullLogin() {
        assertFalse(service.isValidAdmin(null, "ytrewq"));
    }
}
