package br.com.rafael.extra.exceptions;

public class AccountNotFoundException extends Exception {

    public AccountNotFoundException(){
        super("<<<<< Conta nao encontrada >>>>>");
    }
}
