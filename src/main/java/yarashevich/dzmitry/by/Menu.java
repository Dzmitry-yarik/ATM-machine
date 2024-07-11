package yarashevich.dzmitry.by;

import java.math.BigDecimal;
import java.util.Scanner;
import yarashevich.dzmitry.by.entity.BankAccount;
import yarashevich.dzmitry.by.service.BankAccountService;

public class Menu {
    private final BankAccountService service;

    public Menu(BankAccountService service) {
        this.service = service;
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to ATM");

        while (true) {
            System.out.println("Enter \"0\" to exit or enter card number (XXXX-XXXX-XXXX-XXXX):");
            String cardNumber = scanner.nextLine();
            if ("0".equals(cardNumber)) {
                System.out.println("Thank you for using the ATM. Goodbye!");
                service.saveAccounts();
                System.exit(0);
            }

            BankAccount account = service.findAccountByCardNumber(cardNumber);
            if (account != null) {
                if (account.isBlocked()) {
                    System.out.println("Card is currently blocked.");
                    continue;
                }

                System.out.println("Card accepted!");
                boolean authenticated = false;

                while (account.getFailedAttempts() < Constants.MAX_FAILED_ATTEMPTS) {
                    int attemptsLeft = Constants.MAX_FAILED_ATTEMPTS - account.getFailedAttempts();
                    System.out.println("You have " + attemptsLeft + " attempts left before the card is blocked.");
                    System.out.println("Enter \"0\" to exit or enter PIN:");
                    String pin = scanner.nextLine().trim();
                    if ("0".equals(pin)) {
                        System.out.println("Thank you for using the ATM. Goodbye!");
                        service.saveAccounts();
                        System.exit(0);
                    }

                    if (service.authenticatePin(account, pin)) {
                        authenticated = true;
                        System.out.println("Authentication successful!");
                        break;
                    } else {
                        System.out.println("Authentication failed!");
                    }
                }

                if (!authenticated) {
                    continue;
                }

                while (true) {
                    System.out.print("""
                            Choose an option:
                            1. Check Balance
                            2. Withdraw
                            3. Deposit
                            4. Exit
                            """);
                    int choice;
                    try {
                        choice = Integer.parseInt(scanner.nextLine());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid choice. Please enter a number between 1 and 4.");
                        continue;
                    }

                    switch (choice) {
                        case 1 -> System.out.println("Current balance: " + account.getBalance());
                        case 2 -> {
                            System.out.print("Enter amount to withdraw: ");
                            try {
                                BigDecimal amount = new BigDecimal(scanner.nextLine().trim());
                                if (service.withdraw(account, amount)) {
                                    System.out.println("Withdrawal successful! New balance: " + account.getBalance());
                                } else {
                                    System.out.println("Insufficient funds or invalid amount.");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid amount format.");
                            }
                        }
                        case 3 -> {
                            System.out.print("Enter amount to deposit: ");
                            try {
                                BigDecimal amount = new BigDecimal(scanner.nextLine().trim());
                                if (service.deposit(account, amount)) {
                                    System.out.println("Deposit successful! New balance: " + account.getBalance());
                                } else {
                                    System.out.println("Invalid amount.");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid amount format.");
                            }
                        }
                        case 4 -> {
                            System.out.println("Thank you for using the ATM. Goodbye!");
                            service.saveAccounts();
                            System.exit(0);
                        }
                        default -> System.out.println("Invalid choice. Please enter a number between 1 and 4.");
                    }
                }
            } else {
                System.out.println("Card number not recognized.");
            }
        }
    }
}