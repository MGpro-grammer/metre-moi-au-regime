package be.esi.prj.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class that manages database connections to SQLite databases.
 * Provides methods to create persistent or in-memory connections and close them.
 */
public class ConnectionManager {
    private static Connection connection;

    /**
     * Returns a connection to the persistent SQLite database.
     * Creates a new connection if none exists.
     *
     * @return A connection to the persistent database
     * @throws RepositoryException if connection fails
     */
    public static Connection getConnection() {
        if (connection == null) {
            try{
                String url = "jdbc:sqlite:external-data/metre-moi-au-regime.db";
                connection = DriverManager.getConnection(url);
            } catch (SQLException sqlException) {
                throw new RepositoryException("Connection impossible", sqlException);
            }
        }
        return connection;
    }

    /**
     * Returns a connection to an in-memory SQLite database.
     * Creates a new connection if none exists.
     *
     * @return A connection to the in-memory database
     * @throws RepositoryException if connection fails
     */
    public static Connection getMemConnection() {
        if (connection == null) {
            try {
                String url = "jdbc:sqlite::memory:";
                connection = DriverManager.getConnection(url);
            } catch (SQLException sqlException) {
                throw new RepositoryException("Connection impossible", sqlException);
            }
        }
        return connection;
    }

    /**
     * Closes the current database connection if it exists and is open.
     *
     * @throws RepositoryException if closure fails
     */
    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException sqlException) {
            throw new RepositoryException("Closure impossible", sqlException);
        }
    }

}
