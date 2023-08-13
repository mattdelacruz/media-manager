package com.mediamanager.mediamanager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application{
    private static Scene scene;

    public static void main(String[] args) {
        launch();

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        Menu root = Menu.getInstance();

        scene = new Scene(root, 500, 500);

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }


}