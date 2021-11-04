package br.com.rafael.extra.accounts.types.concrete;

import br.com.rafael.extra.accounts.owners._abstract.Customer;
import br.com.rafael.extra.accounts.owners.concrete.LegalPerson;
import br.com.rafael.extra.accounts.types._abstract.EspecialAccountGeneric;
import java.math.BigDecimal;

public class BusinessAccount extends EspecialAccountGeneric {

    public BusinessAccount(LegalPerson owner, int accountNumber, String agency, BigDecimal valueEspecialCheck) {
        super(owner, accountNumber, agency, valueEspecialCheck);
    }

    @Override
    public LegalPerson getOwner() { return (LegalPerson) super.getOwner(); }

    @Override
    public void setOwner(Customer newOwner) throws IllegalArgumentException {
        if (!(newOwner instanceof LegalPerson)) throw new IllegalArgumentException("Invalid Argument!");
        super.setOwner(newOwner);
    }
}
