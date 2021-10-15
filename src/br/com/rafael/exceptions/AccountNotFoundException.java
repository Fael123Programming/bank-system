package br.com.rafael.exceptions;

public class AccountNotFoundException extends Exception {

    public AccountNotFoundException(){
        super("<<<<< Conta nao encontrada >>>>>");
    }
}
