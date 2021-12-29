package br.com.rafael.main;

import br.com.rafael.extra.bank.BankSystem;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        BankSystem bank = new BankSystem(args[0], Locale.getDefault());
//      args[0] is the path of the file where the accounts will be recorded.
//      It should be src/br/com/rafael/extra/persistence/accounts.txt
        bank.start();
    }
}
