package yarashevich.dzmitry.by;

import java.util.Scanner;
import yarashevich.dzmitry.by.action.FileManager;
import yarashevich.dzmitry.by.util.Util;

public class Menu {
    private final Util util;

    public Menu() {
        util = new Util();
    }

    public void menu() {
        try {
            util.setAccounts(FileManager.loadAccounts());
            Scanner scanner = new Scanner(System.in);
            System.out.println("Welcome to ATM");

            while (true) {
                System.out.println("Enter \"0\" to exit or enter card number (XXXX-XXXX-XXXX-XXXX):");
                String cardNumber = scanner.nextLine();
                if ("0".equals(cardNumber)) {
                    System.out.println("Thank you for using the ATM. Goodbye!");
                    util.exit();
                }
                if (util.authenticateCardNumber(cardNumber)) {
                    System.out.println("The card has been accepted!");
                    boolean authenticated = false;

                    while (util.getCurrentAccount().getFailedAttempts() < 3) {
                        int count = 3 - util.getCurrentAccount().getFailedAttempts();
                        System.out.println("You have " + count + " attempts left before the card is blocked.");
                        System.out.println("Enter \"0\" to exit or enter PIN:");
                        String pin = scanner.nextLine().trim();
                        if ("0".equals(pin)) {
                            System.out.println("Thank you for using the ATM. Goodbye!");
                            util.exit();
                        }

                        if (util.authenticatePin(pin)) {
                            authenticated = true;
                            System.out.println("Authentication successful!");
                            util.getCurrentAccount().resetFailedAttempts();
                            break;
                        } else {
                            System.out.println("Authentication failed!");
                            util.getCurrentAccount().incrementFailedAttempts();
                            FileManager.saveAccounts(util.getAccounts());
                        }
                    }

                    if (authenticated) {
                        while (true) {
                            int choice = 0;
                            do {
                                System.out.print("""
                                        Choose an option:
                                        1. Check Balance
                                        2. Withdraw
                                        3. Deposit
                                        4. Exit
                                        """);
                                try {
                                    choice = Integer.parseInt(scanner.nextLine());
                                    if (choice < 1 || choice > 4) {
                                        System.out.println("Incorrect data entered");
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.println("You didn't enter a number");
                                }
                            } while (choice < 1 || choice > 4);

                            switch (choice) {
                                case 1 ->
                                        System.out.println("Current balance: " + util.getCurrentAccount().getBalance());
                                case 2 -> {
                                    System.out.print("Enter amount to withdraw: ");
                                    double amount;
                                    try {
                                        amount = Double.parseDouble(scanner.nextLine().trim());
                                        if (util.getCurrentAccount().withdraw(amount)) {
                                            System.out.println("Withdrawal successful! New balance: " + util.getCurrentAccount().getBalance());
                                            FileManager.saveAccounts(util.getAccounts());
                                        } else {
                                            System.out.println("Insufficient funds or invalid amount.");
                                        }
                                    } catch (NumberFormatException e) {
                                        System.out.println("You didn't enter a number");
                                    }
                                }
                                case 3 -> {
                                    System.out.print("Enter amount to deposit: ");
                                    double amount;
                                    try {
                                        amount = Double.parseDouble(scanner.nextLine());
                                        util.getCurrentAccount().deposit(amount);
                                        System.out.println("Deposit successful! New balance: " + util.getCurrentAccount().getBalance());
                                        FileManager.saveAccounts(util.getAccounts());
                                    } catch (NumberFormatException e) {
                                        System.out.println("You didn't enter a number");
                                    } catch (IllegalArgumentException e) {
                                        System.out.println(e.getMessage());
                                    }
                                }
                                case 4 -> {
                                    System.out.println("Thank you for using the ATM. Goodbye!");
                                    util.exit();
                                }
                            }
                        }
                    }
                } else {
                    System.out.println("Card number not recognized.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}