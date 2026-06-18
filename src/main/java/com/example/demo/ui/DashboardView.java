package com.example.demo.ui;

import com.example.demo.config.SpringContextHolder;
import com.example.demo.entity.*;
import com.example.demo.service.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * 首页概览 - 显示各项统计数据
 */
public class DashboardView extends VBox {

    private final NewspaperService newspaperService = SpringContextHolder.getBean(NewspaperService.class);
    private final CustomerService customerService = SpringContextHolder.getBean(CustomerService.class);
    private final SubscriptionService subscriptionService = SpringContextHolder.getBean(SubscriptionService.class);
    private final SupplierService supplierService = SpringContextHolder.getBean(SupplierService.class);

    public DashboardView() {
        setPadding(new Insets(30));
        setSpacing(25);
        setStyle("-fx-background-color: #f5f6fa;");

        Label title = new Label("🏠 首页概览");
        title.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 24));

        // 统计卡片
        HBox statsRow = new HBox(20);
        statsRow.setAlignment(Pos.CENTER_LEFT);

        statsRow.getChildren().addAll(
            createStatCard("📰 报纸总数", String.valueOf(newspaperService.getAll().size()), "#3498db"),
            createStatCard("👥 客户总数", String.valueOf(customerService.getAll().size()), "#2ecc71"),
            createStatCard("📋 订阅订单", String.valueOf(subscriptionService.getAll().size()), "#e67e22"),
            createStatCard("🏢 供应商", String.valueOf(supplierService.getAll().size()), "#9b59b6")
        );

        // 快捷操作提示
        VBox quickInfo = new VBox(10);
        quickInfo.setPadding(new Insets(20));
        quickInfo.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 2);");
        quickInfo.setPrefHeight(300);

        Label quickTitle = new Label("ℹ️ 系统说明");
        quickTitle.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 18));

        String[] tips = {
            "• 点击左侧导航栏「报纸管理」管理报纸信息",
            "• 点击「订阅管理」查看和处理订阅订单",
            "• 订单需要「审批通过」后才会扣减库存",
            "• 数据存储在本地 data/ 目录，无需安装数据库",
            "• 默认管理员账号: admin@post.com / 123456",
            "• 客户账号: zhangsan@mail.com / 123456",
        };
        for (String tip : tips) {
            Label tipLabel = new Label(tip);
            tipLabel.setFont(Font.font("Microsoft YaHei", 14));
            tipLabel.setTextFill(Color.web("#555"));
            quickInfo.getChildren().add(tipLabel);
        }

        getChildren().addAll(title, statsRow, quickInfo);
    }

    private VBox createStatCard(String title, String value, String color) {
        VBox card = new VBox(5);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(25, 30, 25, 30));
        card.setPrefWidth(200);
        card.setPrefHeight(130);
        card.setStyle(String.format(
            "-fx-background-color: %s; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 5);",
            color
        ));

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 36));
        valueLabel.setTextFill(Color.WHITE);

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Microsoft YaHei", 14));
        titleLabel.setTextFill(Color.web("rgba(255,255,255,0.85)"));

        card.getChildren().addAll(valueLabel, titleLabel);
        return card;
    }
}
