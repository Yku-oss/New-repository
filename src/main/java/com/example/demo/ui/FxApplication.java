package com.example.demo.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * JavaFX 入口 - 由 DemoApplication 启动
 */
public class FxApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("邮局订报管理系统");
        try {
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
        } catch (Exception ignored) {}

        // 先显示登录界面
        LoginView loginView = new LoginView(primaryStage);
        Scene scene = new Scene(loginView, 420, 520);
        try {
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        } catch (Exception ignored) {}

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
