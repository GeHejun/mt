package com.ghj.mt.ui;

import com.ghj.mt.boot.MtApplicationRunner;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;


public class MainLauncher extends Application {

    private static Stage primaryStageObj;

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStageObj = primaryStage;
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("views/LoginView.fxml"));
        primaryStage.setTitle("GeHejunChat");
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.getIcons().add(new Image(getClass().getClassLoader().getResource("images/plug.png").toString()));
        Scene mainScene = new Scene(root,300,275);
        primaryStage.setScene(mainScene);
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e->Platform.exit());
        MtApplicationRunner instance = MtApplicationRunner.getInstance();
        instance.connect("127.0.0.1",10000);
    }


    public static void main(String[] args) {
        launch(args);
    }


    public static Stage getPrimaryStageObj() {
        return primaryStageObj;
    }
}
