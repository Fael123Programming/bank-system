package br.com.rafael.extra.accounts.types._abstract;

import br.com.rafael.extra.accounts.owners._abstract.Customer;
import br.com.rafael.extra.accounts.owners.concrete.*;
import br.com.rafael.extra.accounts.types.concrete.*;
import br.com.rafael.extra.enum_.TypeOfAccount;
import br.com.rafael.extra.exceptions.*;
import java.time.LocalDate;
import java.util.*;
import java.math.BigDecimal;

public abstract class Account implements Comparable<Account> {
    private static int numberOfAccounts = 0;
    private int accountNumber;
    private BigDecimal currentBalance, valueEspecialCheck;
    private String agency;
    private Customer owner;

    public Account() { //Empty constructor function
        Account.numberOfAccounts++;
    }

    public Account(Customer owner, int accountNumber) {
        this.owner = owner;
        this.accountNumber = accountNumber;
        this.currentBalance = BigDecimal.ZERO;
        this.valueEspecialCheck = BigDecimal.ZERO;
        Account.numberOfAccounts++;
    }

    public Account(Customer owner, int accountNumber, String agency, BigDecimal valueEspecialCheck) {
        this(owner, accountNumber);//It calls the constructor function above
        this.agency = agency;
        this.currentBalance = BigDecimal.ZERO;
        this.valueEspecialCheck = valueEspecialCheck;
        Account.numberOfAccounts++;
    }

    public void withdraw(BigDecimal quantity) throws InsufficientFundsException, IllegalArgumentException {
        if(quantity.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("<<<<< Quantia invalida >>>>>");
        if(quantity.compareTo(this.valueEspecialCheck.add(this.currentBalance)) > 0) throw new InsufficientFundsException(quantity.toString());
        this.currentBalance = this.currentBalance.subtract(quantity);
    }

    public void transfer(Account targetAccount, BigDecimal quantity) throws InsufficientFundsException,IllegalArgumentException {
        this.withdraw(quantity);
        targetAccount.deposit(quantity);
    }

    public void deposit(BigDecimal quantity) throws IllegalArgumentException {
        if (quantity.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("<<<<< Quantia invalida >>>>>");
        this.currentBalance = this.currentBalance.add(quantity);
    }

    public static int getNumberOfAccounts() {
        return Account.numberOfAccounts;
    }

    public int getAccountNumber() {
        return this.accountNumber;
    }

    public void setAccountNumber(int newNumber) {
        this.accountNumber = newNumber;
    }

    public Customer getOwner() {
        return this.owner;
    }

    public void setOwner(Customer newOwner) {
        this.owner = newOwner;
    }

    public BigDecimal getCurrentBalance() {
        return this.currentBalance;
    }

    public String getAgency() {
        return this.agency;
    }

    public void setAgency(String newAgency) {
        this.agency = newAgency;
    }

    public BigDecimal getValueEspecialCheck() {
        return this.valueEspecialCheck;
    }

    public String getType() {
        if (CurrentAccount.class.equals(this.getClass())) {
            return TypeOfAccount.CURRENT_ACCOUNT.getIdentifierString();
        } else if (SavingsAccount.class.equals(this.getClass())) {
            return TypeOfAccount.SAVINGS_ACCOUNT.getIdentifierString();
        } else if (SpecialAccount.class.equals(this.getClass())) {
            return TypeOfAccount.SPECIAL_ACCOUNT.getIdentifierString();
        } else if (BusinessAccount.class.equals(this.getClass())) {
            return TypeOfAccount.BUSINESS_ACCOUNT.getIdentifierString();
        }
        return null;
    }

    public static Account parseString(String accountStr) throws NonConvertibleStringAccount {
        if (accountStr == null || accountStr.isBlank()) throw new NonConvertibleStringAccount();
        Account parsedAccount;
        String[] keysAndValues = new String[6]; //All attributes of an account are six.
        StringBuilder builder = new StringBuilder();
        boolean nestedObject = false;
        byte counter = 0;
        accountStr = accountStr.substring(1, accountStr.length() - 1); //Getting the first couple of {} out of the string.
        String agency, accountType, ownerName, ownerDateOfBirth, ownerIdentification;
        int accountNumber;
        BigDecimal currentBalance, valueEspecialCheck;
        String[] ownerKeysAndValues;
        for (char ch : accountStr.toCharArray()) {
            builder.append(ch);
            if (ch == '{') nestedObject = true;
            else if (ch == '}') nestedObject = false;
            if (nestedObject) continue;
            if (ch == ',') {
                builder.deleteCharAt(builder.length() - 1); //The remaining comma!
                keysAndValues[counter++] = builder.toString();
                builder = new StringBuilder();
            }
        }
        try {
            keysAndValues[counter] = builder.toString();
            agency = keysAndValues[5].split(":")[1].trim();
            accountNumber = Integer.parseInt(keysAndValues[2].split(":")[1].trim());
            accountType = keysAndValues[1].split(":")[1].trim();
            currentBalance = new BigDecimal(keysAndValues[3].split(":")[1].replace("R$", "").trim());
            valueEspecialCheck = new BigDecimal(keysAndValues[4].split(":")[1].replace("R$","").trim());
            ownerKeysAndValues = keysAndValues[0].substring(keysAndValues[0].indexOf('{') + 1, keysAndValues[0].indexOf('}')).split(",");
            ownerName = ownerKeysAndValues[0].split(":")[1];
            ownerDateOfBirth = ownerKeysAndValues[1].split(":")[1].trim();
            ownerIdentification = ownerKeysAndValues[2].split(":")[1].trim();
        } catch (ArrayIndexOutOfBoundsException | NullPointerException | NumberFormatException exc) {
            throw new NonConvertibleStringAccount();
        }
        Customer accountOwner;
        if (accountType.equals(TypeOfAccount.CURRENT_ACCOUNT.getIdentifierString()) || accountType.equals(TypeOfAccount.SPECIAL_ACCOUNT.getIdentifierString()) ||
                accountType.equals(TypeOfAccount.SAVINGS_ACCOUNT.getIdentifierString())) {
            accountOwner = new PhysicalPerson(ownerName, LocalDate.parse(ownerDateOfBirth), ownerIdentification);
        } else if (accountType.equals(TypeOfAccount.BUSINESS_ACCOUNT.getIdentifierString())) {
            accountOwner = new LegalPerson(ownerName, LocalDate.parse(ownerDateOfBirth), ownerIdentification);
        } else throw new NonConvertibleStringAccount();

        if (accountType.equals(TypeOfAccount.CURRENT_ACCOUNT.getIdentifierString())) {
            parsedAccount = new CurrentAccount((PhysicalPerson) accountOwner,accountNumber, agency);
        } else if(accountType.equals(TypeOfAccount.SAVINGS_ACCOUNT.getIdentifierString())) {
            parsedAccount = new SavingsAccount((PhysicalPerson) accountOwner, accountNumber, agency);
        } else if (accountType.equals(TypeOfAccount.SPECIAL_ACCOUNT.getIdentifierString())) {
            parsedAccount = new SpecialAccount((PhysicalPerson) accountOwner, accountNumber, agency, valueEspecialCheck);
        } else if (accountType.equals(TypeOfAccount.BUSINESS_ACCOUNT.getIdentifierString())) {
            parsedAccount = new BusinessAccount((LegalPerson) accountOwner, accountNumber, agency, valueEspecialCheck);
        } else throw new NonConvertibleStringAccount();
        parsedAccount.deposit(currentBalance);
        return parsedAccount;
    }

    public String getStandardized(){
        return "--------------------------------------------------------------------------\n" +
                "Responsavel: " + this.getOwner().getName() + "\n" +
                "Numero da conta: " + this.getAccountNumber() + "\n" +
                "Tipo da conta: " + this.getType() + "\n" +
                "Saldo atual: R$ " + this.getCurrentBalance() + "\n" +
                "Agencia: " + this.getAgency() + "\n" +
                "--------------------------------------------------------------------------";
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.owner.toString(), this.getType(), this.accountNumber, this.currentBalance.toString(),
                this.valueEspecialCheck.toString(), this.agency);
        //This method utilizes its arguments to generate a hash code to an object of this class.
    }

    @Override
    public boolean equals(Object toCompare) {
        if(!(toCompare instanceof Account casted)) return false;
        return (this.owner.equals(casted.owner) && this.getType().equals(casted.getType()) && this.accountNumber ==
                casted.accountNumber && this.currentBalance.equals(casted.currentBalance) && this.valueEspecialCheck.
                equals(casted.valueEspecialCheck) && this.agency.equals(casted.agency));
    }

    @Override
    public String toString() {
        return String.format("{responsavel: %s, tipo: %s, numero: %d, saldo atual: R$ %s, valor cheque especial: R$ %s, " +
                "agencia: %s}", this.owner, this.getType(), this.accountNumber, this.currentBalance.toString(),
                this.valueEspecialCheck.toString(), this.agency);
    }

    @Override
    public int compareTo(Account toCompare) {
        return Integer.compare(this.hashCode(),toCompare.hashCode());
    }
}
