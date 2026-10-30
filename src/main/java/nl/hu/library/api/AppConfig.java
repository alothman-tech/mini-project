package nl.hu.library.api;

import jakarta.ws.rs.ApplicationPath;
import nl.hu.library.security.AuthFilter;
import org.glassfish.jersey.server.ResourceConfig;

/** Alle REST-endpoints staan onder /api. */
@ApplicationPath("/api")
public class AppConfig extends ResourceConfig {
    public AppConfig() {
        register(AuthResource.class);
        register(ItemResource.class);
        register(LoanResource.class);
        register(AuthFilter.class);
        register(ApiExceptionMapper.class);
    }
}
