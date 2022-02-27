package br.com.rafael.extra.exceptions;

public class BaseLocaleNullException extends RuntimeException {
    public BaseLocaleNullException() {
        super("call setBaseLocale(locale) first, passing it a non-null locale");
    }
}
