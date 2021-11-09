package br.com.rafael.extra.enum_;

//An enum class is such a class that contains only constants as attributes and these constants are
//similar to objects and can have own attributes and be constructed indirectly.
//They are supposed to standardize data!

public enum TypeOfAccount {
    CURRENT_ACCOUNT(1, "Conta Corrente"), SAVINGS_ACCOUNT(2, "Conta Poupanca"),
    SPECIAL_ACCOUNT(3, "Conta Especial/Pessoa Fisica"), BUSINESS_ACCOUNT(4, "Conta Especial/Pessoa Juridica");

    //Properties of any enum TypeOfAccount. See that they are initialized internally.
    private final int identifierNumber;
    private final String identifierString;

    //All enum constructors are private by definition and called internally when one of those constants defined above
    //is used somewhere.
    TypeOfAccount(int identifierNumber, String identifierString) {
        this.identifierNumber = identifierNumber;
        this.identifierString = identifierString;
    }

    public int getIdentifierNumber(){
        return this.identifierNumber;
    }

    public String getIdentifierString(){
        return this.identifierString;
    }
}
