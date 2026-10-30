package nl.hu.library.security;

import nl.hu.library.domain.Role;
import nl.hu.library.domain.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {
    private final User anna = new User(7, "anna", "hash", Role.LIBRARIAN);

    @Test
    void tokenRoundTripKeepsUserAndRole() {
        AuthUser parsed = JwtUtil.parse(JwtUtil.createToken(anna));
        assertEquals(7, parsed.id());
        assertEquals("anna", parsed.username());
        assertEquals(Role.LIBRARIAN, parsed.role());
    }

    @Test
    void tamperedTokenIsRejected() {
        String token = JwtUtil.createToken(anna);
        String tampered = token.substring(0, token.length() - 12) + "AAAAAAAAAAAA";
        assertThrows(RuntimeException.class, () -> JwtUtil.parse(tampered));
    }

    @Test
    void garbageTokenIsRejected() {
        assertThrows(RuntimeException.class, () -> JwtUtil.parse("dit.is.geen.token"));
    }
}
