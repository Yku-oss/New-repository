package com.example.demo.ui;

import com.example.demo.config.SpringContextHolder;
import com.example.demo.entity.Customer;
import com.example.demo.entity.PostalStaff;
import com.example.demo.service.CustomerService;
import com.example.demo.service.PostalStaffService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class LoginView extends VBox {

    private final CustomerService customerService = SpringContextHolder.getBean(CustomerService.class);
    private final PostalStaffService postalStaffService = SpringContextHolder.getBean(PostalStaffService.class);
    private final Stage primaryStage;

    // 登录模式: true=工作人员登录, false=客户登录
    private boolean isStaffLogin = false;

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

        // 登录模式切换
        HBox modeSwitch = new HBox(0);
        modeSwitch.setAlignment(Pos.CENTER);
        modeSwitch.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 8; -fx-padding: 3;");

        Label loginTitle = new Label("用户登录");
        loginTitle.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 20));
        loginTitle.setStyle("-fx-text-fill: #1a1a2e;");

        // 邮箱输入框（先声明，后面的 lambda 需要引用）
        TextField emailField = new TextField();
        emailField.setPromptText("邮箱");
        emailField.setPrefHeight(42);

        // 密码输入框
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("密码 (默认: 123456)");
        passwordField.setPrefHeight(42);

        Label messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 13; -fx-font-weight: bold;");

        Button customerModeBtn = new Button("👤 客户登录");
        Button staffModeBtn = new Button("👥 工作人员登录");

        for (Button btn : new Button[]{customerModeBtn, staffModeBtn}) {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #7f8c8d; " +
                        "-fx-font-size: 13; -fx-cursor: hand; -fx-padding: 8 18; " +
                        "-fx-background-radius: 6; -fx-font-weight: bold;");
        }
        // 默认选中客户登录
        customerModeBtn.setStyle("-fx-background-color: white; -fx-text-fill: #2c3e50; " +
                                "-fx-font-size: 13; -fx-cursor: hand; -fx-padding: 8 18; " +
                                "-fx-background-radius: 6; -fx-font-weight: bold; " +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);");

        customerModeBtn.setOnAction(e -> {
            isStaffLogin = false;
            customerModeBtn.setStyle("-fx-background-color: white; -fx-text-fill: #2c3e50; " +
                                    "-fx-font-size: 13; -fx-cursor: hand; -fx-padding: 8 18; " +
                                    "-fx-background-radius: 6; -fx-font-weight: bold; " +
                                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);");
            staffModeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #7f8c8d; " +
                                "-fx-font-size: 13; -fx-cursor: hand; -fx-padding: 8 18; " +
                                "-fx-background-radius: 6; -fx-font-weight: bold;");
            emailField.setText("zhangsan@mail.com");
            passwordField.setText("123456");
            messageLabel.setText("");
        });
        staffModeBtn.setOnAction(e -> {
            isStaffLogin = true;
            staffModeBtn.setStyle("-fx-background-color: white; -fx-text-fill: #2c3e50; " +
                                "-fx-font-size: 13; -fx-cursor: hand; -fx-padding: 8 18; " +
                                "-fx-background-radius: 6; -fx-font-weight: bold; " +
                                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);");
            customerModeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #7f8c8d; " +
                                    "-fx-font-size: 13; -fx-cursor: hand; -fx-padding: 8 18; " +
                                    "-fx-background-radius: 6; -fx-font-weight: bold;");
            emailField.setText("wang@post.com");
            passwordField.setText("123456");
            messageLabel.setText("");
        });

        modeSwitch.getChildren().addAll(customerModeBtn, staffModeBtn);

        Button loginBtn = new Button("登  录");
        loginBtn.setPrefHeight(44);
        loginBtn.setPrefWidth(200);
        loginBtn.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #667eea, #764ba2); " +
                          "-fx-text-fill: white; -fx-font-size: 16; -fx-font-weight: bold; " +
                          "-fx-background-radius: 22; -fx-cursor: hand; " +
                          "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.4), 10, 0, 0, 4);");

        // 默认填充客户账号
        emailField.setText("zhangsan@mail.com");
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

            if (isStaffLogin) {
                // 工作人员登录
                PostalStaff staff = postalStaffService.login(email, password);
                if (staff != null) {
                    primaryStage.setScene(new MainScene(primaryStage, staff.getName(), "staff-" + staff.getPosition()).getScene());
                    primaryStage.setResizable(true);
                    primaryStage.setMaximized(true);
                } else {
                    messageLabel.setText("邮箱或密码错误，或账号已离职");
                }
            } else {
                // 客户登录
                Customer customer = customerService.login(email, password);
                if (customer != null) {
                    primaryStage.setScene(new MainScene(primaryStage, customer.getName(), customer.getRole()).getScene());
                    primaryStage.setResizable(true);
                    primaryStage.setMaximized(true);
                } else {
                    messageLabel.setText("邮箱或密码错误，请重试");
                }
            }
        });

        card.getChildren().addAll(modeSwitch, loginTitle, emailField, passwordField, messageLabel, loginBtn);

        // 底部提示
        Label hintLabel = new Label("客户: zhangsan@mail.com | 员工: wang@post.com");
        hintLabel.setStyle("-fx-text-fill: #ccc; -fx-font-size: 12;");

        getChildren().addAll(titleLabel, card, hintLabel);
    }
}
