import javax.swing.*;
import java.sql.*;

/*
 * Main.java
 *
 * This is the ENTRY POINT of the Kiryana Management System.
 * Java always looks for the main() method first when you run the program.
 *
 * WHAT THIS CLASS DOES STEP BY STEP:
 *   Step 1 - Tests the database connection before opening any window.
 *            If the DB is not reachable, shows an error and stops.
 *   Step 2 - If DB is OK, opens the LoginFrame window.
 *
 * WHY TEST DB FIRST?
 *   If we open the GUI without checking the DB, the shopkeeper
 *   will log in, click something, and THEN get a confusing error.
 *   It is better to catch the problem immediately at startup
 *   and show a clear message.
 *
 * HOW TO COMPILE (run this command in your project folder):
 *
 *   Windows:
 *     javac -cp .;mysql-connector-java.jar *.java
 *
 *   Mac/Linux:
 *     javac -cp .:mysql-connector-java.jar *.java
 *
 * HOW TO RUN:
 *
 *   Windows:
 *     java -cp .;mysql-connector-java.jar Main
 *
 *   Mac/Linux:
 *     java -cp .:mysql-connector-java.jar Main
 *
 * DEFAULT LOGIN:
 *   Username : admin
 *   Password : admin123
 */
public class Main {

    public static void main(String[] args) {

        // -----------------------------------------------
        // STEP 1: Test the database connection first
        // -----------------------------------------------
        /*
         * Before we open any window, we check if the database
         * is reachable. DBConnection.getConnection() will:
         *   - Return a Connection object if MySQL is running and
         *     credentials in DBConnection.java are correct.
         *   - Throw a RuntimeException if something is wrong.
         *
         * We wrap it in try-catch to handle the error gracefully
         * and show the shopkeeper a helpful message instead of
         * a confusing Java stack trace.
         */
        System.out.println("Starting Kiryana Management System...");
        System.out.println("Testing database connection...");

        try {
            // Try to get a connection
            Connection con = DBConnection.getConnection();

            // If we reach this line, connection worked
            System.out.println("Database connection successful!");

            // Always close the test connection after checking
            con.close();

        } catch (Exception e) {
            // Connection failed - show error popup and stop the program
            System.out.println("Database connection FAILED: " + e.getMessage());

            JOptionPane.showMessageDialog(
                null,
                "Cannot connect to the database!\n\n"
                + "Please check:\n"
                + "  1. MySQL is running\n"
                + "  2. Username and password in DBConnection.java are correct\n"
                + "  3. The database 'kiryana_db' exists\n"
                + "  4. mysql-connector-java.jar is in the same folder\n\n"
                + "Error: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
            );

            // Stop the program - no point opening the GUI without a DB
            System.exit(0);
        }

        // -----------------------------------------------
        // STEP 2: Database is OK - open the Login window
        // -----------------------------------------------
        /*
         * SwingUtilities.invokeLater() schedules the GUI to open
         * on the Event Dispatch Thread (EDT).
         *
         * All Swing windows must be created on the EDT.
         * This is the correct and safe way to start any Swing app.
         *
         * The Runnable inside is just an object with a run() method.
         * invokeLater() calls run() on the EDT for us.
         */
        System.out.println("Opening application...");

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Create the login window and make it visible
                LoginFrame loginWindow = new LoginFrame();
                loginWindow.setVisible(true);

                System.out.println("Login window opened. Application ready.");
            }
        });
    }
}