package database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;

public class DatabaseSetup {
	private static final String CONFIG_FILE_PATH = "src/database/config.properties";

    public static Connection getConnection() throws SQLException {
    	Properties properties = loadConfig();
    	
	    String url = properties.getProperty("db.url");
	    String username = properties.getProperty("db.username");
	    String password = properties.getProperty("db.password");
	
	    try {
            	Connection connection = DriverManager.getConnection(url, username, password);
           	ensureTablesExist(connection); // checks if tables for watchlist exists
            	return connection;
	    } 
	    catch (SQLException e) {
	    	throw new RuntimeException("Error connecting to the database", e);
	    }
	}
    
    private static Properties loadConfig() {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(CONFIG_FILE_PATH)) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading configuration file", e);
        }
        return properties;
    }
	
    private static void ensureTablesExist(Connection connection) {
        // SQL to create the watchlist_names table
        String createWatchlistNamesTableSQL = """
            CREATE TABLE IF NOT EXISTS watchlist_names (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(255) NOT NULL UNIQUE
            );
        """;

        // SQL to create the watchlist_items table
        String createWatchlistItemsTableSQL = """
            CREATE TABLE IF NOT EXISTS watchlist_items (
                id INT AUTO_INCREMENT PRIMARY KEY,
                watchlist_name VARCHAR(255) NOT NULL,
                title VARCHAR(255) NOT NULL,
                rating INT NOT NULL,
                status VARCHAR(50) NOT NULL,
                FOREIGN KEY (watchlist_name) REFERENCES watchlist_names(name) ON DELETE CASCADE
            );
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createWatchlistNamesTableSQL); //creates the tables
            stmt.execute(createWatchlistItemsTableSQL);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create required tables", e);
        }
    }

}