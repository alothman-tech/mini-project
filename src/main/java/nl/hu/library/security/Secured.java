package nl.hu.library.security;

import jakarta.ws.rs.NameBinding;
import nl.hu.library.domain.Role;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Endpoint vereist een geldig JWT. Met rollen erbij: alleen die rollen mogen erbij. */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Secured {
    Role[] value() default {};
}
