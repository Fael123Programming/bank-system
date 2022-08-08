package br.com.rafael.resourcebundles.bundlepool;

import br.com.rafael.extra.exceptions.BaseLocaleNullException;

import java.util.Locale;
import java.util.ResourceBundle;

public final class BundlePool {
    private static ResourceBundle menuBundle, exceptionBundle, accountCreationBundle, accountInfoBundle,
            accountMovementBundle;

    private static Locale baseLocale = Locale.getDefault();

    private BundlePool() {}

    public static ResourceBundle getMenuBundle() {
        if (menuBundle == null) {
            menuBundle = ResourceBundle.getBundle("br.com.rafael.resourcebundles.menus", baseLocale);
        }
        return menuBundle;
    }

    public static ResourceBundle getExceptionBundle() {
        if (exceptionBundle == null) {
            exceptionBundle = ResourceBundle.getBundle("br.com.rafael.resourcebundles.exceptions", baseLocale);
        }
        return exceptionBundle;
    }

    public static ResourceBundle getAccountCreationBundle() {
        if (accountCreationBundle == null) {
            accountCreationBundle = ResourceBundle.getBundle("br.com.rafael.resourcebundles.accountCreation", baseLocale);
        }
        return accountCreationBundle;
    }

    public static ResourceBundle getAccountInfoBundle() {
        if (accountInfoBundle == null) {
            accountInfoBundle = ResourceBundle.getBundle("br.com.rafael.resourcebundles.accountInformation", baseLocale);
        }
        return accountInfoBundle;
    }

    public static ResourceBundle getAccountMovementBundle() {
        if (accountMovementBundle == null) {
            accountMovementBundle = ResourceBundle.getBundle("br.com.rafael.resourcebundles.accountMovement", baseLocale);
        }
        return accountMovementBundle;
    }

    public static Locale getBaseLocale() {
        return baseLocale;
    }

    public static void setBaseLocale(Locale locale) {
        if (locale == null)
            throw new IllegalArgumentException("locale cannot be null");
        baseLocale = locale;
    }
}
