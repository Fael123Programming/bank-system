package br.com.rafael.resource_bundles;

import java.util.ResourceBundle;
import java.util.Locale;

public class TestBundles {
    public static void main(String[] args) {
        ResourceBundle ptBundle = ResourceBundle.getBundle("br.com.rafael.resourcebundles.messages",
                new Locale("fr", "FR"));
        System.out.println(ptBundle.getString("menuOptions"));
    }
}
