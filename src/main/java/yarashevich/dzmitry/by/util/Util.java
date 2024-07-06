package yarashevich.dzmitry.by.util;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import yarashevich.dzmitry.by.entity.BankAccount;
import yarashevich.dzmitry.by.action.FileManager;

@Getter
@Setter
public class Util {

    private List<BankAccount> accounts;
    private BankAccount currentAccount;

    public boolean authenticateCardNumber(String cardNumber) {
        for (BankAccount account : accounts) {
            if (account.getCardNumber().equals(cardNumber)) {
                currentAccount = account;
                if (account.isBlocked()) {
                    System.out.println("Card is currently blocked.");
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    @SneakyThrows
    public void exit() {
        FileManager.saveAccounts(accounts);
        System.exit(0);
    }

    public boolean authenticatePin(String pin) {
        if (currentAccount.getPin().equals(pin)) {
            currentAccount.resetFailedAttempts();
            return true;
        }
        return false;
    }
}
