package nl.hu.library.persistence;

import java.sql.SQLException;

/** Maakt van de checked SQLException een unchecked exception, met behoud van de SQL-state. */
public class DataAccessException extends RuntimeException {
    private final String sqlState;

    public DataAccessException(SQLException cause) {
        super(cause.getMessage(), cause);
        this.sqlState = cause.getSQLState();
    }

    public String getSqlState() { return sqlState; }
}
