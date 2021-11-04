package br.com.rafael.extra.manager;

import br.com.rafael.extra.accounts.types._abstract.Account;

import javax.swing.JOptionPane;
import java.util.Map;

public class Manager {
    private static volatile Manager instance;

    private Manager(){}

    public static Manager getInstance(){
        //DCL: Double-Check Lock among threads using this singleton.
        Manager local = instance;
        if (local != null) {
            return local;
        }
        synchronized(Manager.class) {
            if (instance == null) {
                instance = new Manager();
            }
            return instance;
        }
    }

    public int menu(String options) {
        return inputDialogForIntegerNumber(options);
    }

    public void exit() {
        showMessage("<<<<< Sessao finalizada >>>>>");
        System.exit(0);
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(null, msg);
    }

    public int inputDialogForIntegerNumber(String msg) {
        return Integer.parseInt(inputDialog(msg));
    }

    public double inputDialogForFloatNumber(String msg) {
        return Double.parseDouble(inputDialog(msg));
    }

    public String inputDialog(String msg) {
        return JOptionPane.showInputDialog(msg);
    }

    private int randomNumber(int bound1, int bound2) {
        return (int) (bound1 + (Math.random() * (bound2 - bound1 + 1)));
    }

    public int getNewAccountNumber(Map<Integer, Account> mapOfAccounts) {
        int number = randomNumber(1000, 100000);
        while (mapOfAccounts.containsKey(number)) number = randomNumber(1000, 100000);
        return number;
    }
}
