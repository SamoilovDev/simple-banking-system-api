package banking;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class MainMechanisms {

    public static final Random RANDOM = new Random();

    public static void createNewAccount() throws SQLException {
        StringBuilder newNumberOfCard = new StringBuilder();

        do {
            newNumberOfCard.append("400000");
            for (int i = 1; i < 10; i++) newNumberOfCard.append(RANDOM.nextInt(10));

            newNumberOfCard.append(
                    lastNumberToLuhnAlgorithm(String.valueOf(newNumberOfCard).split(""))
            );

        } while (Database.isNumberOfCardExists(newNumberOfCard.toString()));

        Database.addNewAccount(newNumberOfCard.toString(), createNewCardPin());

    }

    public static String createNewCardPin() {
        StringBuilder newPin = new StringBuilder();

        for (int i = 1; i <= 4; i++) {
            newPin.append(RANDOM.nextInt(10));
        }

        return newPin.toString();
    }

    public static int lastNumberToLuhnAlgorithm(String[] firstFifteen) { // adds the last number according to Luhn's algorithm
        int sumByLuhn = getSumByLuhnAlgorithm(List.of(firstFifteen));

        return sumByLuhn % 10 != 0
                ? 10 - (sumByLuhn % 10)
                : 0;
    }

    public static boolean isNumberCreatedByLuhn(String number) { // check the card number for the Luhn algorithm
        List<String> stringNumbersOfCard = new ArrayList<>(List.of(number.split("")));
        int lastNum = Integer.parseInt(
                stringNumbersOfCard.remove(stringNumbersOfCard.size() - 1)
        );

        return (getSumByLuhnAlgorithm(stringNumbersOfCard) + lastNum) % 10 == 0;
    }

    private static Integer getSumByLuhnAlgorithm(List<String> stringNums) {
        AtomicInteger i = new AtomicInteger(0);
        AtomicInteger sumByLuhn = new AtomicInteger(0);

        stringNums.stream()
                .map(Integer::parseInt)
                .forEachOrdered(integer -> {
                    integer = i.incrementAndGet() % 2 != 0 ? integer * 2 : integer;
                    integer = integer > 9 ? integer - 9 : integer;
                    sumByLuhn.addAndGet(integer);
                });

        return sumByLuhn.get();
    }

}

