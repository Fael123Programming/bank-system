package br.com.rafael.extra.exceptions;

public class InsufficientFundsException extends Exception {
    public InsufficientFundsException(){
        super("<<<<< Saldo insuficiente >>>>>");
    }

    public InsufficientFundsException(String value) {
        super("<<<<< Saldo insuficiente para sacar R$ " + value + " >>>>>");
    }
}
