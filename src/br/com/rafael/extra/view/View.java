package br.com.rafael.extra.view;

import br.com.rafael.extra.accounts.types._abstract.Account;

import javax.swing.JOptionPane;
import java.util.Map;

public class View {
    private View(){}

    public static int menu(String options) {
        return View.inputDialogForIntegerNumber(options);
    }

    public static void exit() {
        View.showMessage("<<<<< Sessao finalizada >>>>>");
        System.exit(0);
    }

    public static void showMessage(String msg) {
        JOptionPane.showMessageDialog(null, msg, "R.I.C.H Bank", JOptionPane.INFORMATION_MESSAGE);
    }

    public static int inputDialogForIntegerNumber(String msg) {
        return Integer.parseInt(View.inputDialog(msg));
    }

    public static double inputDialogForFloatNumber(String msg) {
        return Double.parseDouble(View.inputDialog(msg));
    }

    public static String inputDialog(String msg) {
        return JOptionPane.showInputDialog(null, msg, "R.I.C.H Bank", JOptionPane.QUESTION_MESSAGE);
    }

    private static int randomNumber(int bound1, int bound2) {
        return (int) (bound1 + (Math.random() * (bound2 - bound1 + 1)));
    }

    public static int getNewAccountNumber(Map<Integer, Account> mapOfAccounts) {
        int number = randomNumber(1000, 100000);
        while (mapOfAccounts.containsKey(number))
            number = randomNumber(1000, 100000);
        return number;
    }
}
