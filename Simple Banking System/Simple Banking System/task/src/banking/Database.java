package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.*;

public class Database {

    static SQLiteDataSource dataSource = new SQLiteDataSource();

    static Connection connection;

    static Statement table;

    static PreparedStatement preparedStatement;

    public static void createDatabaseSQLite(String urlOfDatabase) {

        dataSource.setUrl(urlOfDatabase);

        try {
            connection = dataSource.getConnection();
            table = connection.createStatement();
            table.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS card(
                        id INT NOT NULL,
                        number VARCHAR(20),
                        pin VARCHAR(4),
                        balance INT NOT NULL DEFAULT 0
                    );""");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void addNewAccount(String number, String pin) throws SQLException {
        String statement = "INSERT INTO card (id, number, pin)" +
                " VALUES (?, ?, ?)";
        preparedStatement = connection.prepareStatement(statement);

        preparedStatement.setInt(1, getLastID() + 1);
        preparedStatement.setString(2, number);
        preparedStatement.setString(3, pin);

        preparedStatement.executeUpdate();
    }

    public static void deleteAccount(int id) throws SQLException {
        String statement = "DELETE FROM card " +
                "WHERE id = ?";
        preparedStatement = connection.prepareStatement(statement);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
    }

    public static int getLastID() throws SQLException {
        ResultSet result = table.executeQuery("SELECT MAX(id) FROM card;");
        return result.getInt("MAX(id)");
    }

    public static int getBalanceByID(int id) throws SQLException {
        ResultSet result = table.executeQuery("SELECT balance " +
                "FROM card " +
                "WHERE id = " + id);
        return  result.getInt("balance");
    }

    public static void addIncome(int amount, int id) throws SQLException {
        String statement = "UPDATE card " +
                "SET balance = balance + ?" +
                "WHERE id = ?";
        preparedStatement = connection.prepareStatement(statement);

        preparedStatement.setInt(1, amount);
        preparedStatement.setInt(2, id);

        preparedStatement.executeUpdate();
    }

    public static boolean isNumberOfCardExists(String numberOfCard) throws SQLException {
        ResultSet result = table.executeQuery("SELECT * FROM card" +
                " WHERE number = '" + numberOfCard + "';");
        return result.next();
    }

    public static boolean isValidData(String numberOfCard, String pin) throws SQLException {
        ResultSet result = table.executeQuery("SELECT * FROM card " +
                "WHERE number = '" + numberOfCard + "' AND pin = '" + pin + "';");
        return result.next();
    }

    public static String getNumberOfCardByID(int id) throws SQLException {
        ResultSet result = table.executeQuery("SELECT * FROM card" +
                " WHERE id = " + id);
        return  result.getString("number");
    }

    public static int getIdByNumberOfCard(String numberOfCard) throws SQLException {
        ResultSet result = table.executeQuery("SELECT id FROM card" +
                " WHERE number = " + numberOfCard);
        return  result.getInt("id");
    }

    public static String getPinByID(int id) throws SQLException {
        ResultSet result = table.executeQuery("SELECT pin FROM card" +
                " WHERE id = " + id);
        return  result.getString("pin");
    }

}
