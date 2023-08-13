package com.mediamanager.mediamanager;

import javafx.scene.layout.Pane;

public class Menu extends Pane {
    private static Menu menu_instance = null;

    public Menu() {
        this.init();
    }

    public void init() {
        MovieFinder mf = new MovieFinder();
        mf.testMovieScrape();
    }

    public static Menu getInstance() {
        if (menu_instance == null) {
            menu_instance = new Menu();
        }

        return menu_instance;
    }
}