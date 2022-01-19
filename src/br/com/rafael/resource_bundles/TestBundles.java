package br.com.rafael.resource_bundles;

import java.util.ResourceBundle;
import java.util.Locale;
import java.time.LocalDate;

public class TestBundles {
    public static void main(String[] args) {
        ResourceBundle frBundle = ResourceBundle.getBundle("br.com.rafael.resource_bundles.menus",
                new Locale("fr", "FR"));
        System.out.println(frBundle.getString("mainMenu"));
    }
}
