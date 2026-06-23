package com.example.demo.ui;

import com.example.demo.config.SpringContextHolder;
import com.example.demo.entity.*;
import com.example.demo.service.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import java.util.function.Consumer;

/**
 * 首页概览 - 显示各项统计数据
 */
public class DashboardView extends VBox {

    private final NewspaperService newspaperService = SpringContextHolder.getBean(NewspaperService.class);
    private final CustomerService customerService = SpringContextHolder.getBean(CustomerService.class);
    private final SubscriptionService subscriptionService = SpringContextHolder.getBean(SubscriptionService.class);
    private final SupplierService supplierService = SpringContextHolder.getBean(SupplierService.class);
    private Consumer<String> onNavigate;

    public DashboardView() {
        this(null);
    }

    public DashboardView(Consumer<String> onNavigate) {
        this.onNavigate = onNavigate;
        setPadding(new Insets(30));
        setSpacing(25);
        setStyle("-fx-background-color: #f0f2f5;");

        // 顶部标题行
        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);
        headerRow.setSpacing(15);

        Label title = new Label("🏠 首页概览");
        title.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #1a1a2e;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label dateLabel = new Label(java.time.LocalDate.now().toString());
        dateLabel.setFont(Font.font("Microsoft YaHei", 13));
        dateLabel.setStyle("-fx-text-fill: #888; -fx-padding: 6 15; -fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 4, 0, 0, 1);");

        headerRow.getChildren().addAll(title, spacer, dateLabel);

        // 统计卡片
        HBox statsRow = new HBox(20);
        statsRow.setAlignment(Pos.CENTER_LEFT);

        statsRow.getChildren().addAll(
            createStatCard("📰 报纸总数", String.valueOf(newspaperService.getAll().size()), 
                "linear-gradient(from 0% 0% to 100% 100%, #5b9bd5, #3a7cc3)", "newspaper"),
            createStatCard("👥 客户总数", String.valueOf(customerService.getAll().size()), 
                "linear-gradient(from 0% 0% to 100% 100%, #6cbf73, #4fa857)", "customer"),
            createStatCard("📋 订阅订单", String.valueOf(subscriptionService.getAll().size()), 
                "linear-gradient(from 0% 0% to 100% 100%, #e8a838, #d4891e)", "subscription"),
            createStatCard("🏢 供应商", String.valueOf(supplierService.getAll().size()), 
                "linear-gradient(from 0% 0% to 100% 100%, #af7ac5, #8e5ea6)", "supplier")
        );

        // 下方区域：系统说明 + 快捷操作
        HBox bottomRow = new HBox(20);
        bottomRow.setAlignment(Pos.TOP_LEFT);
        VBox.setVgrow(bottomRow, Priority.ALWAYS);

        // 系统说明面板
        VBox quickInfo = new VBox(12);
        quickInfo.setPadding(new Insets(25));
        quickInfo.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 2);");
        quickInfo.setPrefWidth(600);
        VBox.setVgrow(quickInfo, Priority.ALWAYS);

        HBox quickTitleRow = new HBox(10);
        quickTitleRow.setAlignment(Pos.CENTER_LEFT);
        Label quickTitle = new Label("ℹ️ 系统说明");
        quickTitle.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 18));
        quickTitle.setStyle("-fx-text-fill: #1a1a2e;");

        Label quickBadge = new Label("v1.0");
        quickBadge.setStyle("-fx-background-color: #e8eaed; -fx-text-fill: #666; -fx-font-size: 11; " +
                           "-fx-padding: 2 10; -fx-background-radius: 10;");
        quickTitleRow.getChildren().addAll(quickTitle, quickBadge);

        String[] tips = {
            "📰 点击左侧导航栏「报纸管理」管理报纸信息",
            "📋 点击「订阅管理」查看和处理订阅订单",
            "✅ 订单需要「审批通过」后才会扣减库存",
            "💾 数据存储在本地 data/ 目录，无需安装数据库",
            "👤 默认管理员账号: admin@post.com / 123456",
            "👤 客户账号: zhangsan@mail.com / 123456",
        };
        for (String tip : tips) {
            Label tipLabel = new Label(tip);
            tipLabel.setFont(Font.font("Microsoft YaHei", 14));
            tipLabel.setStyle("-fx-text-fill: #555; -fx-padding: 6 0;");
            quickInfo.getChildren().add(tipLabel);
        }

        // 右侧快捷操作面板
        VBox quickActions = new VBox(12);
        quickActions.setPadding(new Insets(25));
        quickActions.setStyle("-fx-background-color: white; -fx-background-radius: 12; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 2);");
        quickActions.setPrefWidth(300);
        VBox.setVgrow(quickActions, Priority.ALWAYS);

        Label actionTitle = new Label("⚡ 快捷操作");
        actionTitle.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 18));
        actionTitle.setStyle("-fx-text-fill: #1a1a2e;");

        // 快捷操作按钮 - 点击后跳转到对应管理页面
        String[][] actions = {
            {"📰 新增报纸", "#2980b9", "newspaper"},
            {"👥 新增客户", "#27ae60", "customer"},
            {"📋 新建订阅", "#d48806", "subscription"},
            {"📊 查看分析", "#8e44ad", "analysis"},
        };
        for (String[] action : actions) {
            Button actionBtn = new Button(action[0]);
            actionBtn.setMaxWidth(Double.MAX_VALUE);
            actionBtn.setPrefHeight(40);
            actionBtn.setStyle(String.format(
                "-fx-background-color: %s; -fx-text-fill: white; -fx-font-size: 13; " +
                "-fx-background-radius: 6; -fx-cursor: hand; -fx-padding: 8 16;", action[1]));
            String targetView = action[2];
            actionBtn.setOnAction(e -> {
                if (onNavigate != null) onNavigate.accept(targetView);
            });
            quickActions.getChildren().add(actionBtn);
        }

        bottomRow.getChildren().addAll(quickInfo, quickActions);

        getChildren().addAll(headerRow, statsRow, bottomRow);
    }

    private VBox createStatCard(String title, String value, String gradient, String targetView) {
        VBox card = new VBox(8);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(25, 30, 25, 30));
        card.setPrefWidth(210);
        card.setPrefHeight(140);
        card.setStyle(String.format(
            "-fx-background-color: %s; -fx-background-radius: 14; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 15, 0, 0, 5); " +
            "-fx-cursor: hand;",
            gradient
        ));
        // 悬停放大效果
        card.setOnMouseEntered(e -> 
            card.setStyle(String.format(
                "-fx-background-color: %s; -fx-background-radius: 14; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 8); " +
                "-fx-scale-x: 1.03; -fx-scale-y: 1.03; " +
                "-fx-cursor: hand;", gradient)));
        card.setOnMouseExited(e ->
            card.setStyle(String.format(
                "-fx-background-color: %s; -fx-background-radius: 14; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 15, 0, 0, 5); " +
                "-fx-scale-x: 1; -fx-scale-y: 1; " +
                "-fx-cursor: hand;", gradient)));
        // 点击跳转到对应管理页面
        card.setOnMouseClicked(e -> {
            if (onNavigate != null) onNavigate.accept(targetView);
        });

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 36));
        valueLabel.setTextFill(Color.WHITE);
        valueLabel.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 4, 0, 0, 2);");

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Microsoft YaHei", 14));
        titleLabel.setTextFill(Color.web("rgba(255,255,255,0.9)"));

        // 小装饰条
        Label bar = new Label();
        bar.setPrefHeight(3);
        bar.setPrefWidth(40);
        bar.setStyle("-fx-background-color: rgba(255,255,255,0.4); -fx-background-radius: 2;");

        card.getChildren().addAll(valueLabel, titleLabel, bar);
        return card;
    }
}
