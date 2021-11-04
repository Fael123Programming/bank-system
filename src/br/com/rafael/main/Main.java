package br.com.rafael.main;

import br.com.rafael.extra.bank.BankSystem;
import br.com.rafael.extra.exceptions.UnparseableStringAccount;

public class Main {
    public static void main(String[] args) throws UnparseableStringAccount {
        BankSystem bank = new BankSystem(args[0]);
        //args[0] is the path of the file where the accounts will be recorded.
        //It should be src/br/com/rafael/extra/persistence/accounts.txt
        bank.start();
    }
}
