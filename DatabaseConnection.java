import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/* Maha Elabbadi, CEN-3024C-14835, 11/3/23
   The DatabaseConnection class provides a method to establish a connection to a MySQL database.
   It contains a static method 'getConnection' that loads the MySQL JDBC driver,
   establishes a connection using the provided JDBC URL, username, and password,
   and returns the established Connection object to be used by the rest of the program. */
public class DatabaseConnection {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/library_database";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }
    }