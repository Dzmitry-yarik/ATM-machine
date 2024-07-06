package yarashevich.dzmitry.by.action;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import yarashevich.dzmitry.by.entity.BankAccount;

public class FileManager {
    private static final String FILE_PATH = "src/main/resources/data.txt";

    public static List<BankAccount> loadAccounts() throws IOException {
        List<BankAccount> accounts = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ");
            if (parts.length == 5) {
                String cardNumber = parts[0];
                String pin = parts[1];
                double balance = Double.parseDouble(parts[2]);
                int failedAttempts = Integer.parseInt(parts[3]);
                boolean isBlocked = Boolean.parseBoolean(parts[4]);
                accounts.add(new BankAccount(cardNumber, pin, balance, failedAttempts, isBlocked));
            }
        }
        reader.close();
        return accounts;
    }

    public static void saveAccounts(List<BankAccount> accounts) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH));
        for (BankAccount account : accounts) {
            writer.write(account.getCardNumber() + " " + account.getPin() + " " + account.getBalance() + " "
                    + account.getFailedAttempts() + " " + account.isBlocked());
            writer.newLine();
        }
        writer.close();
    }
}