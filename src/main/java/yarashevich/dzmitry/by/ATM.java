package yarashevich.dzmitry.by;

import yarashevich.dzmitry.by.repository.BankAccountRepository;
import yarashevich.dzmitry.by.service.BankAccountService;

public class ATM {
    public static void main(String[] args) {
        BankAccountRepository repository = new BankAccountRepository();
        BankAccountService service = new BankAccountService(repository);
        Menu menu = new Menu(service);

        menu.start();
    }
}