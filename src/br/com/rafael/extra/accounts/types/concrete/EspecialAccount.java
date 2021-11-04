package br.com.rafael.extra.accounts.types.concrete;

import br.com.rafael.extra.accounts.owners._abstract.Customer;
import br.com.rafael.extra.accounts.owners.concrete.PhysicalPerson;
import br.com.rafael.extra.accounts.types._abstract.EspecialAccountGeneric;
import java.math.BigDecimal;

public class EspecialAccount extends EspecialAccountGeneric {

    public EspecialAccount(PhysicalPerson owner, int accountNumber) {
        super(owner, accountNumber);
    }

    public EspecialAccount(PhysicalPerson owner, int accountNumber, String agency, BigDecimal valueEspecialCheck) {
        super(owner, accountNumber, agency, valueEspecialCheck);
    }

    @Override
    public PhysicalPerson getOwner() {
        return (PhysicalPerson) super.getOwner();
    }

    @Override
    public void setOwner(Customer newOwner) throws IllegalArgumentException {
        if (!(newOwner instanceof PhysicalPerson)) throw new IllegalArgumentException("Invalid argument!");
        super.setOwner(newOwner); //As setOwner belongs initially to the super class, it would be better to reference it already!
    }
}
