package yarashevich.dzmitry.by.repository;

import java.math.BigDecimal;
import yarashevich.dzmitry.by.Constants;
import yarashevich.dzmitry.by.entity.BankAccount;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BankAccountRepository {

    public List<BankAccount> loadAccounts() throws IOException {
        List<BankAccount> accounts = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(Constants.FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 5) {
                    String cardNumber = parts[0];
                    String pin = parts[1];
                    BigDecimal balance = new BigDecimal(parts[2]);
                    int failedAttempts = Integer.parseInt(parts[3]);
                    boolean isBlocked = Boolean.parseBoolean(parts[4]);
                    accounts.add(new BankAccount(cardNumber, pin, balance, failedAttempts, isBlocked));
                }
            }
        }
        return accounts;
    }

    public void saveAccounts(List<BankAccount> accounts) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Constants.FILE_PATH))) {
            for (BankAccount account : accounts) {
                writer.write(account.getCardNumber() + " " + account.getPin() + " " + account.getBalance() + " "
                        + account.getFailedAttempts() + " " + account.isBlocked());
                writer.newLine();
            }
        }
    }
}