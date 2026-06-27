package com.example.demo.ui;

import com.example.demo.DemoApplication;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;

public class FxApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("邮局订报管理系统");
        try {
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
        } catch (Exception ignored) {}

        LoginView loginView = new LoginView(primaryStage);
        Scene scene = new Scene(loginView, 420, 520);
        try {
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        } catch (Exception ignored) {}
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);

        // 窗口关闭时自动停止 Spring Boot
        primaryStage.setOnCloseRequest(event -> {
            stopSpringBoot();
            Platform.exit();
        });

        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void stop() {
        stopSpringBoot();
    }

    private void stopSpringBoot() {
        ConfigurableApplicationContext ctx = DemoApplication.getContext();
        if (ctx != null && ctx.isActive()) {
            System.out.println("正在关闭 Spring Boot...");
            ctx.close();
        }
    }
}
