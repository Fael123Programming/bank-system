package br.com.rafael.resourcebundles.bundlepool;

import java.util.ResourceBundle;
import java.util.Locale;

public class TestBundles {
    public static void main(String[] args) {
        ResourceBundle frBundle = ResourceBundle.getBundle("br.com.rafael.resourcebundles.menus",
                new Locale("fr", "FR"));
        System.out.println(frBundle.getString("mainMenu"));
    }
}
