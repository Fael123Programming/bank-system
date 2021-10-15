package br.com.rafael.main;

import br.com.rafael.bank.BankSystem;

public class Main {
    public static void main(String[] args) {
        BankSystem bank = new BankSystem(args[0]); //args[0] is the path of the file where the accounts will be recorded.
        bank.start();
    }
}
