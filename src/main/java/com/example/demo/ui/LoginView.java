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
        setSpacing(15);
        setPadding(new Insets(40));
        setStyle("-fx-background: linear-gradient(from 0% 0% to 0% 100%, #667eea 0%, #764ba2 100%);");

        // 标题
        Label titleLabel = new Label("📰 邮局订报管理系统");
        titleLabel.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 26));
        titleLabel.setStyle("-fx-text-fill: white;");
        VBox.setMargin(titleLabel, new Insets(0, 0, 30, 0));

        // 登录卡片
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 10);");
        card.setMaxWidth(340);
        card.setPrefWidth(340);

        Label loginTitle = new Label("用户登录");
        loginTitle.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 18));
        loginTitle.setStyle("-fx-text-fill: #333;");

        TextField emailField = new TextField();
        emailField.setPromptText("邮箱 (如: admin@post.com)");
        emailField.setStyle("-fx-padding: 10; -fx-font-size: 14; -fx-background-radius: 6; -fx-border-color: #ddd; -fx-border-radius: 6;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("密码 (默认: 123456)");
        passwordField.setStyle("-fx-padding: 10; -fx-font-size: 14; -fx-background-radius: 6; -fx-border-color: #ddd; -fx-border-radius: 6;");

        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12;");

        Button loginBtn = new Button("登  录");
        loginBtn.setPrefHeight(40);
        loginBtn.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-font-size: 15; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
        loginBtn.setOnMouseEntered(e -> loginBtn.setStyle("-fx-background-color: #5a6fd6; -fx-text-fill: white; -fx-font-size: 15; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;"));
        loginBtn.setOnMouseExited(e -> loginBtn.setStyle("-fx-background-color: #667eea; -fx-text-fill: white; -fx-font-size: 15; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;"));

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
