package yarashevich.dzmitry.by.service;

import java.io.IOException;
import yarashevich.dzmitry.by.Constants;
import yarashevich.dzmitry.by.entity.BankAccount;
import yarashevich.dzmitry.by.repository.BankAccountRepository;
import yarashevich.dzmitry.by.action.CardBlockScheduler;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class BankAccountService {
    private final BankAccountRepository repository;
    private List<BankAccount> accounts;

    public BankAccountService(BankAccountRepository repository) {
        this.repository = repository;
        loadAccounts();
    }

    public void loadAccounts() {
        try {
            this.accounts = repository.loadAccounts();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveAccounts() {
        try {
            repository.saveAccounts(accounts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BankAccount findAccountByCardNumber(String cardNumber) {
        return accounts.stream()
                .filter(account -> account.getCardNumber().equals(cardNumber))
                .findFirst()
                .orElse(null);
    }

    public boolean authenticatePin(BankAccount account, String pin) {
        if (account.getPin().equals(pin)) {
            account.setFailedAttempts(0);
            return true;
        } else {
            account.setFailedAttempts(account.getFailedAttempts() + 1);
            if (account.getFailedAttempts() >= Constants.MAX_FAILED_ATTEMPTS) {
                blockAccount(account);
            }
            saveAccounts();
            return false;
        }
    }

    public void blockAccount(BankAccount account) {
        account.setBlocked(true);
        CardBlockScheduler.scheduleUnblock(account, LocalDateTime.now().plusDays(Constants.UNBLOCK_DELAY));
    }

    public void unblockAccount(BankAccount account) {
        account.setBlocked(false);
        account.setFailedAttempts(0);
    }

    public boolean deposit(BankAccount account, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0 && amount.compareTo(new BigDecimal("1000000")) <= 0) {
            account.setBalance(account.getBalance().add(amount));
            saveAccounts();
            return true;
        }
        return false;
    }

    public boolean withdraw(BankAccount account, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) > 0 && amount.compareTo(account.getBalance()) <= 0) {
            account.setBalance(account.getBalance().subtract(amount));
            saveAccounts();
            return true;
        }
        return false;
    }
}