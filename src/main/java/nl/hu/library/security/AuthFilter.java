package nl.hu.library.security;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;

import java.security.Principal;
import java.util.Arrays;
import java.util.Map;

/** Draait vóór elk endpoint met @Secured: controleert het JWT en de rol. */
@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext ctx) {
        String header = ctx.getHeaderString(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            abort(ctx, Response.Status.UNAUTHORIZED, "Niet ingelogd");
            return;
        }

        AuthUser user;
        try {
            user = JwtUtil.parse(header.substring("Bearer ".length()));
        } catch (RuntimeException e) {
            abort(ctx, Response.Status.UNAUTHORIZED, "Ongeldig of verlopen token");
            return;
        }

        Secured secured = resourceInfo.getResourceMethod().getAnnotation(Secured.class);
        if (secured == null) secured = resourceInfo.getResourceClass().getAnnotation(Secured.class);
        if (secured != null && secured.value().length > 0
                && !Arrays.asList(secured.value()).contains(user.role())) {
            abort(ctx, Response.Status.FORBIDDEN, "Je rol heeft hier geen toegang toe");
            return;
        }

        final boolean secure = ctx.getSecurityContext().isSecure();
        ctx.setSecurityContext(new SecurityContext() {
            @Override public Principal getUserPrincipal() { return user; }
            @Override public boolean isUserInRole(String role) { return user.role().name().equals(role); }
            @Override public boolean isSecure() { return secure; }
            @Override public String getAuthenticationScheme() { return "Bearer"; }
        });
    }

    private void abort(ContainerRequestContext ctx, Response.Status status, String message) {
        ctx.abortWith(Response.status(status)
                .entity(Map.of("error", message))
                .type(MediaType.APPLICATION_JSON)
                .build());
    }
}
