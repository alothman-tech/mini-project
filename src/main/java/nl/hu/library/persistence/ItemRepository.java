package nl.hu.library.persistence;

import nl.hu.library.domain.Book;
import nl.hu.library.domain.LibraryItem;
import nl.hu.library.domain.Magazine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ItemRepository {

    // Een item is beschikbaar als er geen openstaande uitlening (returned_at IS NULL) voor is.
    private static final String SELECT =
            "SELECT i.id, i.item_type, i.title, i.year, i.author, i.issue_number, (l.id IS NULL) AS available "
          + "FROM library_items i LEFT JOIN loans l ON l.item_id = i.id AND l.returned_at IS NULL ";

    public List<LibraryItem> findAll() {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT + "ORDER BY i.id");
             ResultSet rs = ps.executeQuery()) {
            List<LibraryItem> items = new ArrayList<>();
            while (rs.next()) items.add(map(rs));
            return items;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public LibraryItem findById(int id) {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT + "WHERE i.id = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    /** Voegt toe en geeft het nieuwe id terug. */
    public int insert(LibraryItem item) {
        String sql = "INSERT INTO library_items (item_type, title, year, author, issue_number) "
                + "VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, item instanceof Book ? "BOOK" : "MAGAZINE");
            bindFields(ps, item, 2);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    /** Geeft het aantal gewijzigde rijen terug (0 = niet gevonden). */
    public int update(LibraryItem item) {
        String sql = "UPDATE library_items SET title = ?, year = ?, author = ?, issue_number = ? WHERE id = ?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bindFields(ps, item, 1);
            ps.setInt(5, item.getId());
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public int delete(int id) {
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM library_items WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // Zet title, year, author, issue_number op de parameters start .. start+3
    private void bindFields(PreparedStatement ps, LibraryItem item, int start) throws SQLException {
        ps.setString(start, item.getTitle());
        ps.setInt(start + 1, item.getYear());
        if (item instanceof Book book) {
            ps.setString(start + 2, book.getAuthor());
            ps.setNull(start + 3, Types.INTEGER);
        } else if (item instanceof Magazine magazine) {
            ps.setNull(start + 2, Types.VARCHAR);
            ps.setInt(start + 3, magazine.getIssueNumber());
        }
    }

    private LibraryItem map(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String title = rs.getString("title");
        int year = rs.getInt("year");
        LibraryItem item = "BOOK".equals(rs.getString("item_type"))
                ? new Book(id, title, year, rs.getString("author"))
                : new Magazine(id, title, year, rs.getInt("issue_number"));
        if (!rs.getBoolean("available")) item.borrow(); // zet de domeinstatus gelijk aan de database
        return item;
    }
}
