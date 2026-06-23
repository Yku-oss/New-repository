package com.example.demo.ui;

import com.example.demo.config.SpringContextHolder;
import com.example.demo.entity.Customer;
import com.example.demo.service.CustomerService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginView extends VBox {

    private final CustomerService customerService = SpringContextHolder.getBean(CustomerService.class);
    private final Stage primaryStage;

    public LoginView(Stage primaryStage) {
        this.primaryStage = primaryStage;
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setPadding(new Insets(50));
        setStyle("-fx-background: linear-gradient(from 0% 0% to 100% 100%, #667eea 0%, #764ba2 100%);");

        // 标题
        Label titleLabel = new Label("📰 邮局订报管理系统");
        titleLabel.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 28));
        titleLabel.setStyle("-fx-text-fill: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3);");
        VBox.setMargin(titleLabel, new Insets(0, 0, 25, 0));

        // 登录卡片
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(35, 30, 30, 30));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 16; " +
                      "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 30, 0, 0, 15);");
        card.setMaxWidth(360);
        card.setPrefWidth(360);

        Label loginTitle = new Label("用户登录");
        loginTitle.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 20));
        loginTitle.setStyle("-fx-text-fill: #1a1a2e;");

        // 邮箱输入框
        TextField emailField = new TextField();
        emailField.setPromptText("邮箱 (如: admin@post.com)");
        emailField.setPrefHeight(42);

        // 密码输入框
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("密码 (默认: 123456)");
        passwordField.setPrefHeight(42);

        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 13; -fx-font-weight: bold;");

        Button loginBtn = new Button("登  录");
        loginBtn.setPrefHeight(44);
        loginBtn.setPrefWidth(200);
        loginBtn.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #667eea, #764ba2); " +
                          "-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; " +
                          "-fx-background-radius: 22; -fx-cursor: hand; " +
                          "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.4), 10, 0, 0, 4);");

        // 默认填充管理员账号
        emailField.setText("admin@post.com");
        passwordField.setText("123456");

        // 回车键登录
        passwordField.setOnAction(e -> loginBtn.fire());
        emailField.setOnAction(e -> passwordField.requestFocus());

        loginBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();

            if (email.isEmpty() || password.isEmpty()) {
                messageLabel.setText("请输入邮箱和密码");
                return;
            }

            Customer customer = customerService.login(email, password);
            if (customer != null) {
                primaryStage.setScene(new MainScene(primaryStage, customer).getScene());
                primaryStage.setResizable(true);
                primaryStage.setMaximized(true);
            } else {
                messageLabel.setText("邮箱或密码错误，请重试");
            }
        });

        card.getChildren().addAll(loginTitle, emailField, passwordField, messageLabel, loginBtn);

        // 底部提示
        Label hintLabel = new Label("管理员: admin@post.com / 123456");
        hintLabel.setStyle("-fx-text-fill: #ccc; -fx-font-size: 12;");

        getChildren().addAll(titleLabel, card, hintLabel);
    }
}
