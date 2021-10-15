package br.com.rafael.accounts.types.concrete;

import br.com.rafael.accounts.owners._abstract.Customer;
import br.com.rafael.accounts.owners.concrete.PhysicalPerson;

public class SavingsAccount extends CurrentAccount {
    private static double yieldPercentage = 0.005; //0.5 %

    public SavingsAccount(PhysicalPerson owner, int accountNumber) {
        super(owner, accountNumber);
    }

    public SavingsAccount(PhysicalPerson owner, int accountNumber, String agency) {
        super(owner, accountNumber, agency);
    }

    public boolean yieldMoney() {
        if (super.getCurrentBalance() == 0) return false;
        super.deposit(super.getCurrentBalance() * SavingsAccount.yieldPercentage);
        return true;
    }

    public static double getYieldPercentage() {
        return SavingsAccount.yieldPercentage * 100;
    }

    public static void setYieldPercentage(double newPercentage) {
        SavingsAccount.yieldPercentage = newPercentage / 100;
    }

    @Override
    public void setOwner(Customer newOwner) throws IllegalArgumentException {
        if (!(newOwner instanceof PhysicalPerson)) throw new IllegalArgumentException("Illegal Argument!");
        super.setOwner(newOwner);
    }
}

