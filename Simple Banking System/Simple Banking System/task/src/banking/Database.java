package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.*;

public class Database {

    private static final SQLiteDataSource dataSource = new SQLiteDataSource();

    static Connection connection;

    private static Statement table;

    private static PreparedStatement preparedStatement;

    static void createDatabaseSQLite(String urlOfDatabase) {

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

            try {
                connection.close();
            } catch (SQLException ignored) {}

            ex.printStackTrace();
        }
    }



    public static void moneyTransfer(int idOfAccountFROM, String numberOfCardTO, int moneyToTransfer) throws SQLException {
        String statement = "UPDATE card SET balance = balance + ? WHERE id = ?";
        Savepoint savepoint = Database.connection.setSavepoint();

        try {
            Database.connection.setAutoCommit(false);

            PreparedStatement transferStatement = Database.connection.prepareStatement(statement);

            transferStatement.setInt(1, -moneyToTransfer);
            transferStatement.setInt(2, idOfAccountFROM);
            transferStatement.executeUpdate();

            transferStatement.setInt(1,  moneyToTransfer);
            transferStatement.setInt(2, Database.getIdByNumberOfCard(numberOfCardTO));
            transferStatement.executeUpdate();

            Database.connection.commit();
            Database.connection.setAutoCommit(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
            Database.connection.rollback(savepoint);
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
        String statement = "DELETE FROM card WHERE id = ?";
        preparedStatement = connection.prepareStatement(statement);

        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
    }

    public static int getLastID() throws SQLException { // get last ID from database
        ResultSet result = table.executeQuery("SELECT MAX(id) FROM card;");
        return result.getInt("MAX(id)");
    }

    public static int getBalanceByID(int id) throws SQLException {
        ResultSet result = table.executeQuery("SELECT balance FROM card WHERE id = " + id);
        return result.getInt("balance");
    }

    public static void addIncome(int amount, int id) throws SQLException { // add income money to the balance
        String statement = "UPDATE card SET balance = balance + ? WHERE id = ?";
        preparedStatement = connection.prepareStatement(statement);

        preparedStatement.setInt(1, amount);
        preparedStatement.setInt(2, id);

        preparedStatement.executeUpdate();
    }

    public static boolean isNumberOfCardExists(String numberOfCard) throws SQLException {
        if (! numberOfCard.matches("^[\\d\\s]+$")) return false;

        String statement = "SELECT * FROM card WHERE number = ?";
        preparedStatement = connection.prepareStatement(statement);

        preparedStatement.setString(1, numberOfCard);

        return preparedStatement.executeQuery().next();
    }

    public static boolean isValidData(String numberOfCard, String pin) throws SQLException { // checks for the similarity of the pincode and card number
        if (!numberOfCard.matches("^[\\d\\s]+$") || !pin.matches("^\\d+$")) return false;
        ResultSet result = table.executeQuery("SELECT * FROM card " +
                "WHERE number = '" + numberOfCard + "' AND pin = '" + pin + "'");
        return result.next();
    }

    public static String getNumberOfCardByID(int id) throws SQLException {
        String statement = "SELECT * FROM card WHERE id = ?";

        preparedStatement = connection.prepareStatement(statement);
        preparedStatement.setInt(1, id);

        return preparedStatement.executeQuery().getString("number");
    }

    public static int getIdByNumberOfCard(String numberOfCard) throws SQLException {
        if (! numberOfCard.matches("^[\\d\\s]+$")) return 0;
        ResultSet result = table.executeQuery("SELECT id FROM card WHERE number = " + numberOfCard);
        return  result.getInt("id");
    }

    public static String getPinByID(int id) throws SQLException {
        ResultSet result = table.executeQuery("SELECT pin FROM card" +
                " WHERE id = " + id);
        return  result.getString("pin");
    }

}