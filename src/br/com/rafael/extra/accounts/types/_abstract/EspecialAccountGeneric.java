package br.com.rafael.extra.accounts.types._abstract;

import br.com.rafael.extra.accounts.owners._abstract.Customer;
import java.math.BigDecimal;

public abstract class EspecialAccountGeneric extends Account {

    public EspecialAccountGeneric(Customer owner, int accountNumber) {
        //The owner will be an instance of PhysicalPerson or LegalPerson (which are customers by definition).
        super(owner, accountNumber);
    }

    public EspecialAccountGeneric(Customer owner, int accountNumber, String agency, BigDecimal valueEspecialCheck) {
        super(owner, accountNumber, agency, valueEspecialCheck);
    }
}
