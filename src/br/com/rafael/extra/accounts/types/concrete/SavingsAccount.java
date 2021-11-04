package br.com.rafael.extra.accounts.types.concrete;

import br.com.rafael.extra.accounts.owners._abstract.Customer;
import br.com.rafael.extra.accounts.owners.concrete.PhysicalPerson;
import java.math.BigDecimal;

public class SavingsAccount extends CurrentAccount {
    private static BigDecimal yieldPercentage = new BigDecimal("0.005"); //0.5%

    public SavingsAccount(PhysicalPerson owner, int accountNumber) {
        super(owner, accountNumber);
    }

    public SavingsAccount(PhysicalPerson owner, int accountNumber, String agency) {
        super(owner, accountNumber, agency);
    }

    public boolean yieldMoney() {
        if (super.getCurrentBalance().equals(BigDecimal.ZERO)) return false;
        super.deposit(super.getCurrentBalance().multiply(yieldPercentage));
        return true;
    }

    public static BigDecimal getYieldPercentage() {
        return yieldPercentage;
    }

    public static boolean setYieldPercentage(BigDecimal newPercentage) {
        if (newPercentage.compareTo(BigDecimal.ZERO) <= 0 || newPercentage.compareTo(BigDecimal.ONE) > 0) return false;
        //There cannot be set neither a yield percentage less or equal than zero nor greater than 1.
        yieldPercentage = newPercentage;
        return true;
    }

    @Override
    public void setOwner(Customer newOwner) throws IllegalArgumentException {
        if (!(newOwner instanceof PhysicalPerson)) throw new IllegalArgumentException("Illegal Argument!");
        super.setOwner(newOwner);
    }
}

