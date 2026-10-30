package nl.hu.library.persistence;

import nl.hu.library.domain.Role;
import nl.hu.library.domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {

    public User findByUsername(String username) {
        String sql = "SELECT u.id, u.username, u.password_hash, r.name AS role "
                + "FROM users u JOIN roles r ON r.id = u.role_id WHERE u.username = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new User(rs.getInt("id"), rs.getString("username"),
                        rs.getString("password_hash"), Role.valueOf(rs.getString("role")));
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }
}
