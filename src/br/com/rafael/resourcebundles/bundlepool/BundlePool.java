package br.com.rafael.resourcebundles.bundlepool;

import br.com.rafael.extra.exceptions.BaseLocaleNullException;

import java.util.Locale;
import java.util.ResourceBundle;

public class BundlePool {
    private static BundlePool instance;
    private static Locale baseLocale;
    private ResourceBundle menuBundle, exceptionBundle, accountCreationBundle, accountInfoBundle;

    private BundlePool() {}
    public static BundlePool getInstance() { //Singleton facility!
        if (baseLocale == null)
            throw new BaseLocaleNullException();
        if (instance == null) {
            instance = new BundlePool();
            instance.menuBundle = ResourceBundle.getBundle("br.com.rafael.resourcebundles.menus", baseLocale);
            instance.exceptionBundle = ResourceBundle.getBundle("br.com.rafael.resourcebundles.exceptions", baseLocale);
            instance.accountCreationBundle = ResourceBundle.getBundle("br.com.rafael.resourcebundles.accountCreation", baseLocale);
            instance.accountInfoBundle = ResourceBundle.getBundle("br.com.rafael.resourcebundles.accountInformation", baseLocale);
        }
        return instance;
    }

    public static void setBaseLocale(Locale locale) {
        if (locale == null)
            throw new IllegalArgumentException("locale cannot be null");
        baseLocale = locale;
    }

    public static Locale getBaseLocale() {
        return baseLocale;
    }

    public ResourceBundle getMenuBundle() {
        return this.menuBundle;
    }

    public ResourceBundle getExceptionBundle() {
        return this.exceptionBundle;
    }

    public ResourceBundle getAccountCreationBundle() {
        return this.accountCreationBundle;
    }

    public ResourceBundle getAccountInfoBundle() {
        return this.accountInfoBundle;
    }
}
