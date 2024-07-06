package yarashevich.dzmitry.by.entity;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import yarashevich.dzmitry.by.action.CardBlockScheduler;

@Data
@AllArgsConstructor
public class BankAccount {
    private final String cardNumber;
    private String pin;
    private double balance;
    private int failedAttempts;
    private boolean isBlocked;
    private final AtomicReference<Double> transactionBalance = new AtomicReference<>(balance);

    public void deposit(double amount) {
        if (amount > 0 && amount <= 1000000) {
            try {
                beginTransaction();
                balance += amount;
                transactionBalance.set(balance);
            } catch (Exception e) {
                balance = transactionBalance.get();
                throw new IllegalArgumentException("Invalid deposit amount");
            }
        } else {
            throw new IllegalArgumentException("Invalid deposit amount");
        }
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            try {
                beginTransaction();
                balance -= amount;
                transactionBalance.set(balance);
                return true;
            } catch (Exception e) {
                balance = transactionBalance.get();
                return false;
            }
        } else {
            return false;
        }
    }

    private void beginTransaction() {
        transactionBalance.set(balance);
    }

    public void block() {
        this.isBlocked = true;
        System.out.println("Card is blocked due to 3 incorrect PIN attempts. It will be unblocked in 24 hours.");
        CardBlockScheduler.scheduleUnblock(this, LocalDateTime.now().plusMinutes(2));
    }

    public void unblock() {
        this.isBlocked = false;
        this.failedAttempts = 0;

    }

    public void incrementFailedAttempts() {
        this.failedAttempts++;
        if (this.failedAttempts > 2) {
            block();
        }
    }

    public void resetFailedAttempts() {
        this.failedAttempts = 0;
    }
}