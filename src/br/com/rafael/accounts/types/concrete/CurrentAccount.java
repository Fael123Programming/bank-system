package br.com.rafael.accounts.types.concrete;

import br.com.rafael.accounts.owners._abstract.Customer;
import br.com.rafael.accounts.owners.concrete.PhysicalPerson;
import br.com.rafael.accounts.types._abstract.Account;

public class CurrentAccount extends Account {

    public CurrentAccount(PhysicalPerson owner, int accountNumber) {
        super(owner, accountNumber);
    }

    public CurrentAccount(PhysicalPerson owner, int accountNumber, String agency) {
        super(owner, accountNumber, agency, 0.0);//0.0 is the value of the especial check.
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
