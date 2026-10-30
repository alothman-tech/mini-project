package nl.hu.library.api;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import nl.hu.library.api.Dtos.LoginRequest;
import nl.hu.library.api.Dtos.LoginResponse;
import nl.hu.library.domain.User;
import nl.hu.library.persistence.UserRepository;
import nl.hu.library.security.JwtUtil;
import nl.hu.library.security.PasswordHasher;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {
    private final UserRepository users = new UserRepository();

    @POST
    @Path("/login")
    public LoginResponse login(LoginRequest request) {
        if (request == null || request.username() == null || request.password() == null) {
            throw new ApiException(400, "Gebruikersnaam en wachtwoord zijn verplicht");
        }
        User user = users.findByUsername(request.username());
        if (user == null || !PasswordHasher.verify(request.password(), user.getPasswordHash())) {
            throw new ApiException(401, "Ongeldige gebruikersnaam of wachtwoord");
        }
        return new LoginResponse(JwtUtil.createToken(user), user.getUsername(), user.getRole().name());
    }
}
