package yarashevich.dzmitry.by.entity;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BankAccount {
    private final String cardNumber;
    private String pin;
    private BigDecimal balance;
    private int failedAttempts;
    private boolean isBlocked;
}