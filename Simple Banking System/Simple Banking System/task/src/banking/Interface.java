package banking;

import java.sql.SQLException;

import static banking.Main.*;

public enum Interface {

    MENU {
        @Override
        public Interface action() throws SQLException {
            if (logged) {
                System.out.println("""
                        1. Balance
                        2. Add income
                        3. Withdraw money
                        4. Do transfer
                        5. Close account
                        6. Log out
                        0. Exit""");
                return switch (SCANNER.next()) {
                    case "1" -> BALANCE.action();
                    case "2" -> ADD_INCOME.action();
                    case "3" -> WITHDRAW.action();
                    case "4" -> TRANSFER.action();
                    case "5" -> DELETE.action();
                    case "6" -> LOG_OUT.action();
                    case "0" -> EXIT.action();
                    default  -> ERROR.action();
                };
            } else {
                System.out.println("""
                        1. Create an account
                        2. Log into account
                        0. Exit""");
                return switch (SCANNER.next()) {
                    case "1" -> CREATE.action();
                    case "2" -> LOG_IN.action();
                    case "0" -> EXIT.action();
                    default -> ERROR.action();
                };
            }
        }
    },

    ERROR {
        @Override
        public Interface action() throws SQLException {
            System.out.println("Wrong number of action!");
            return MENU.action();
        }
    },

    CREATE {
        @Override
        public Interface action() throws SQLException {
            MainMechanisms.createNewAccount();
            ID = Database.getLastID();

            System.out.printf("""
                    Your card has been created
                    Your card number:
                    %s
                    Your card PIN:
                    %s
                    
                    """, Database.getNumberOfCardByID(ID),
                    Database.getPinByID(ID));

            return MENU.action();
        }
    },

    DELETE {
        @Override
        public Interface action() throws SQLException {
            logged = false;

            Database.deleteAccount(ID);
            System.out.println("The account has been closed!");

            return MENU.action();
        }
    },

    LOG_IN {
        @Override
        public Interface action() throws SQLException {
            System.out.println("Enter your card number:");
            String numberOfCardToLogIn = SCANNER.next();
            System.out.println("Enter your PIN:");
            String pinOfCardToLogIn = SCANNER.next();

            if (Database.isValidData(numberOfCardToLogIn, pinOfCardToLogIn)) {

                if (Database.getPinByID(ID).equals(pinOfCardToLogIn)) {
                    logged = true;
                    ID = Database.getIdByNumberOfCard(numberOfCardToLogIn);
                    System.out.println("You have successfully logged in!");
                } else {
                    System.out.println("Wrong card number or PIN!");
                }

            } else {
                System.out.println("Wrong card number or PIN!");
            }

            return MENU.action();
        }
    },

    LOG_OUT {
        @Override
        public Interface action() throws SQLException {
            System.out.println("You have successfully logged out!");
            logged = false;
            return MENU.action();
        }
    },

    BALANCE {
        @Override
        public Interface action() throws SQLException {
            System.out.println("Balance: " + Database.getBalanceByID(ID));
            return MENU.action();
        }
    },

    TRANSFER {
        @Override
        public Interface action() throws SQLException {
            System.out.println("""
                    Transfer
                    Enter card number:""");
            String number = SCANNER.next();


            if (! Database.isNumberOfCardExists(number)) {
                System.out.println("Such a card does not exist.");
            } else if (! MainMechanisms.isNumberCreatedByLuhn(number)) {
                    System.out.println("Probably you made a mistake in the card number. Please try again!");
            } else if (Database.getIdByNumberOfCard(number) == ID) {
                System.out.println("You can't transfer money to the same account!");
            } else {
                System.out.println("Enter how much money you want to transfer:");
                int moneyToTransfer = SCANNER.nextInt();

                if (moneyToTransfer > Database.getBalanceByID(ID)) {
                    System.out.println("Not enough money!");
                } else {
                    Database.moneyTransfer(ID, number, moneyToTransfer);
                    System.out.println("Success!");
                }
            }


            return MENU.action();
        }
    },

    ADD_INCOME {
        @Override
        public Interface action() throws SQLException {
            System.out.println("Enter income:");
            int amount = SCANNER.nextInt();

            if (amount >= 0) {
                Database.addIncome(amount, ID);
                System.out.println("Income was added!");
            } else {
                System.out.println("Unfortunately, you cannot add a negative amount of money.");
            }

            return MENU.action();
        }
    },

    WITHDRAW {
        @Override
        public Interface action() throws SQLException {
            System.out.println("Enter amount of withdraw:");
            int amount = SCANNER.nextInt();

            if (amount >= Database.getBalanceByID(ID)) {
                System.out.println("You don't have enough money in your account!");
            } else if (amount < 0) {
                System.out.println("Unfortunately, you cannot get a negative amount of money.");
            } else {
                Database.addIncome(-amount, ID);
                System.out.println("Successful withdrawal!");
            }

            return MENU.action();
        }
    },

    EXIT {
        @Override
        public Interface action() throws SQLException {
            System.out.println("Bye!");
            Database.connection.close();
            return EXIT;
        }
    };

    public abstract Interface action() throws SQLException;

}
