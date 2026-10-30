package nl.hu.library.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest {
    @Test
    void correctPasswordVerifies() {
        String stored = PasswordHasher.hash("geheim123");
        assertTrue(PasswordHasher.verify("geheim123", stored));
    }

    @Test
    void wrongPasswordIsRejected() {
        String stored = PasswordHasher.hash("geheim123");
        assertFalse(PasswordHasher.verify("fout", stored));
    }

    @Test
    void samePasswordGivesDifferentHashes() {
        assertNotEquals(PasswordHasher.hash("x"), PasswordHasher.hash("x"));
    }

    @Test
    void garbageHashIsRejectedWithoutException() {
        assertFalse(PasswordHasher.verify("x", "geen-geldige-hash"));
    }
}
