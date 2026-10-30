package nl.hu.library.persistence;

import nl.hu.library.domain.Loan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanRepository {

    private static final String SELECT =
            "SELECT l.id, l.user_id, u.username, l.item_id, i.title, l.borrowed_at, l.returned_at "
          + "FROM loans l JOIN users u ON u.id = l.user_id JOIN library_items i ON i.id = l.item_id ";

    public int insert(int userId, int itemId) {
        String sql = "INSERT INTO loans (user_id, item_id) VALUES (?, ?) RETURNING id";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public Loan findById(int id) {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT + "WHERE l.id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public List<Loan> findAll() {
        return query(SELECT + "ORDER BY l.id DESC", null);
    }

    public List<Loan> findByUser(int userId) {
        return query(SELECT + "WHERE l.user_id = ? ORDER BY l.id DESC", userId);
    }

    /** Geeft het aantal gewijzigde rijen terug (0 = al teruggebracht of niet gevonden). */
    public int markReturned(int id) {
        String sql = "UPDATE loans SET returned_at = CURRENT_DATE WHERE id = ? AND returned_at IS NULL";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    private List<Loan> query(String sql, Integer userId) {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (userId != null) ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Loan> loans = new ArrayList<>();
                while (rs.next()) loans.add(map(rs));
                return loans;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    private Loan map(ResultSet rs) throws SQLException {
        return new Loan(rs.getInt("id"), rs.getInt("user_id"), rs.getString("username"),
                rs.getInt("item_id"), rs.getString("title"),
                rs.getObject("borrowed_at", LocalDate.class),
                rs.getObject("returned_at", LocalDate.class));
    }
}
