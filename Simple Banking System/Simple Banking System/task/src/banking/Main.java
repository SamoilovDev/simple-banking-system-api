package banking;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {

    public static boolean logged;

    public static int ID;

    public static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) throws SQLException {
        String urlOfDatabase = "jdbc:sqlite:".concat(args[1]); // args[0] = "-filename", args[1] = "***.db"
        Database.createDatabaseSQLite(urlOfDatabase);
        Interface.MENU.action(); // start interface menu
    }

}
