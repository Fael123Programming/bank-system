package br.com.rafael.extra.exceptions;

public class UnparseableStringAccount extends Exception {

    public UnparseableStringAccount(){
        super("Impossible to generate an account object based on this string");
    }
}
