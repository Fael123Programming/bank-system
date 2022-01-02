package br.com.rafael.extra.exceptions;

public class NonConvertibleStringAccount extends Exception {

    public NonConvertibleStringAccount(){
        super("Impossible to generate an account object based on this string");
    }
}
