package br.com.rafael.extra.accounts.types.concrete;

import br.com.rafael.extra.accounts.owners._abstract.Customer;
import br.com.rafael.extra.accounts.owners.concrete.PhysicalPerson;
import br.com.rafael.extra.accounts.types._abstract.Account;
import java.math.BigDecimal;

public class CurrentAccount extends Account {

    public CurrentAccount(PhysicalPerson owner, int accountNumber) {
        super(owner, accountNumber);
    }

    public CurrentAccount(PhysicalPerson owner, int accountNumber, String agency) {
        super(owner, accountNumber, agency, BigDecimal.ZERO);
    }

    @Override
    public PhysicalPerson getOwner() {
        return (PhysicalPerson) super.getOwner();
    }

    @Override
    public void setOwner(Customer newOwner) {
        if(!(newOwner instanceof PhysicalPerson)) throw new IllegalArgumentException("Invalid Argument!");
        super.setOwner(newOwner);
    }
}
