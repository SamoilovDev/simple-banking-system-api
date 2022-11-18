package banking;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Arrays;
import java.util.Random;

public class MainMechanisms {

    protected static Random random = new Random();

    public static void createNewAccount() throws SQLException {
        StringBuilder newNumberOfCard = new StringBuilder();

        do {
            newNumberOfCard.append("400000");

            for (int i = 1; i < 10; i++) newNumberOfCard.append(random.nextInt(10));

            String[] firstFifteen = String.valueOf(newNumberOfCard).split("");
            newNumberOfCard.append(lastNumberToLuhnAlgorithm(firstFifteen));

        } while (Database.isNumberOfCardExists(newNumberOfCard.toString()));

        Database.addNewAccount(newNumberOfCard.toString(), createNewCardPin());

    }

    protected static String createNewCardPin() {
        StringBuilder newPin = new StringBuilder();
        for (int i = 1; i <= 4; i++) {
            newPin.append(random.nextInt(10));
        }

        return newPin.toString();
    }

    protected static int lastNumberToLuhnAlgorithm(String[] firstFifteen) {
        int[] numbers = new int[firstFifteen.length];

        for (int i = 1; i <= firstFifteen.length; i++) {
            numbers[i - 1] = Integer.parseInt(firstFifteen[i - 1]);
            if (i % 2 != 0) numbers[i - 1] *= 2;
            if (numbers[i - 1] > 9) numbers[i - 1] -= 9;
        }

        if (Arrays.stream(numbers).sum() % 10 != 0) {
            return  10 - (Arrays.stream(numbers).sum() % 10);
        } else return 0;
    }

    protected static boolean isNumberCreatedByLuhn(String number) {
        String[] stringNumbersOfCard = number.split("");
        int lastNum = Integer.parseInt(stringNumbersOfCard[stringNumbersOfCard.length - 1]);
        int sumByLuhn = 0;

        for (int i = 1; i < stringNumbersOfCard.length; i++) {
            int num = Integer.parseInt(stringNumbersOfCard[i - 1]);
            if (i % 2 != 0) num *= 2;
            if (num > 9) num -= 9;
            sumByLuhn += num;
        }

        return  (sumByLuhn + lastNum) % 10 == 0;
    }

    protected static void moneyTransfer(int idOfAccountFROM, String numberOfCardTO, int moneyToTransfer) throws SQLException {
        String statement = "UPDATE card " +
                "SET balance = balance + ? " +
                "WHERE id = ?";
        Savepoint savepoint = Database.connection.setSavepoint();
        try {
            Database.connection.setAutoCommit(false);

            PreparedStatement transferStatement = Database.connection.prepareStatement(statement);

            transferStatement.setInt(1, -moneyToTransfer);
            transferStatement.setInt(2, idOfAccountFROM);
            transferStatement.executeUpdate();

            transferStatement.setInt(1, moneyToTransfer);
            transferStatement.setInt(2, Database.getIdByNumberOfCard(numberOfCardTO));
            transferStatement.executeUpdate();

            Database.connection.commit();
            Database.connection.setAutoCommit(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
            Database.connection.rollback(savepoint);
        }
    }
}

