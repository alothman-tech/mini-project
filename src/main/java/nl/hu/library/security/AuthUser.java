package nl.hu.library.security;

import nl.hu.library.domain.Role;

import java.security.Principal;

/** De ingelogde gebruiker, afgeleid uit het JWT. */
public record AuthUser(int id, String username, Role role) implements Principal {
    @Override
    public String getName() { return username; }
}
