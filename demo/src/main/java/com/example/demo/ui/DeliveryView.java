//配送管理视图 - 邮局工作人员管理订单配送状态
package com.example.demo.ui;

import com.example.demo.config.SpringContextHolder;
import com.example.demo.entity.*;
import com.example.demo.service.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.time.LocalDate;
import java.util.stream.Collectors;

public class DeliveryView extends VBox {

    private final SubscriptionService subscriptionService = SpringContextHolder.getBean(SubscriptionService.class);
    private final CustomerService customerService = SpringContextHolder.getBean(CustomerService.class);
    private final NewspaperService newspaperService = SpringContextHolder.getBean(NewspaperService.class);
    private final TableView<Subscription> table = new TableView<>();
    private final ObservableList<Subscription> data = FXCollections.observableArrayList();

    // 筛选按钮
    private Button allBtn, pendingBtn, deliveringBtn, completedBtn;
    private Button activeFilterBtn;
    private String currentFilter = "all";

    public DeliveryView() {
        setPadding(new Insets(20));
        setSpacing(15);
        setStyle("-fx-background-color: #f5f6fa;");

        Label title = new Label("🚚 配送管理");
        title.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 22));

        // 顶部筛选栏
        HBox filterBar = new HBox(8);
        filterBar.setAlignment(Pos.CENTER_LEFT);

        allBtn = createFilterBtn("📋 全部配送", "all");
        pendingBtn = createFilterBtn("⏳ 待配送", "pending");
        deliveringBtn = createFilterBtn("🚚 配送中", "delivering");
        completedBtn = createFilterBtn("✅ 已配送", "completed");

        filterBar.getChildren().addAll(allBtn, pendingBtn, deliveringBtn, completedBtn);
        setActiveFilter(allBtn);

        // 表格列
        TableColumn<Subscription, Integer> idCol = new TableColumn<>("订单ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(60);

        TableColumn<Subscription, String> customerCol = new TableColumn<>("客户姓名");
        customerCol.setCellValueFactory(cd -> {
            Customer c = customerService.getById(cd.getValue().getCustomerId());
            return new SimpleStringProperty(c != null ? c.getName() : "");
        });
        customerCol.setPrefWidth(100);

        TableColumn<Subscription, String> newspaperCol = new TableColumn<>("报纸名称");
        newspaperCol.setCellValueFactory(cd -> {
            Newspaper n = newspaperService.getById(cd.getValue().getNewspaperId());
            return new SimpleStringProperty(n != null ? n.getName() : "");
        });
        newspaperCol.setPrefWidth(150);

        TableColumn<Subscription, Integer> qtyCol = new TableColumn<>("数量");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        qtyCol.setPrefWidth(60);

        TableColumn<Subscription, String> addressCol = new TableColumn<>("配送地址");
        addressCol.setCellValueFactory(new PropertyValueFactory<>("deliveryAddress"));
        addressCol.setPrefWidth(200);

        TableColumn<Subscription, LocalDate> startCol = new TableColumn<>("订阅开始");
        startCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        startCol.setPrefWidth(100);

        TableColumn<Subscription, LocalDate> endCol = new TableColumn<>("订阅结束");
        endCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        endCol.setPrefWidth(100);

        TableColumn<Subscription, String> payStatusCol = new TableColumn<>("支付状态");
        payStatusCol.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        payStatusCol.setPrefWidth(80);

        TableColumn<Subscription, String> approvalCol = new TableColumn<>("审批状态");
        approvalCol.setCellValueFactory(new PropertyValueFactory<>("approvalStatus"));
        approvalCol.setPrefWidth(80);

        TableColumn<Subscription, String> deliveryCol = new TableColumn<>("配送状态");
        deliveryCol.setCellValueFactory(new PropertyValueFactory<>("deliveryStatus"));
        deliveryCol.setPrefWidth(90);

        // 操作列
        TableColumn<Subscription, Void> actionCol = new TableColumn<>("配送操作");
        actionCol.setPrefWidth(260);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button startBtn = new Button("🚚 开始配送");
            private final Button completeBtn = new Button("✅ 完成配送");
            private final HBox pane = new HBox(4, startBtn, completeBtn);
            {
                pane.setAlignment(Pos.CENTER);
                startBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11; -fx-padding: 3 10; -fx-background-radius: 3;");
                completeBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11; -fx-padding: 3 10; -fx-background-radius: 3;");
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Subscription s = getTableView().getItems().get(getIndex());

                // 只有已通过审批的订单才能配送
                boolean canDeliver = "已通过".equals(s.getApprovalStatus());
                boolean isPending = "待配送".equals(s.getDeliveryStatus());
                boolean isDelivering = "配送中".equals(s.getDeliveryStatus());

                startBtn.setDisable(!(canDeliver && isPending));
                completeBtn.setDisable(!(canDeliver && isDelivering));

                startBtn.setOnAction(e -> {
                    subscriptionService.startDelivery(s.getId());
                    refresh();
                });
                completeBtn.setOnAction(e -> {
                    subscriptionService.completeDelivery(s.getId());
                    refresh();
                });
                setGraphic(pane);
            }
        });

        table.getColumns().addAll(idCol, customerCol, newspaperCol, qtyCol, addressCol,
                startCol, endCol, payStatusCol, approvalCol, deliveryCol, actionCol);

        table.setItems(data);
        table.setRowFactory(tv -> {
            TableRow<Subscription> row = new TableRow<>();
            row.setPrefHeight(36);
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem == null) {
                    row.setStyle("");
                } else if ("已配送".equals(newItem.getDeliveryStatus())) {
                    row.setStyle("-fx-background-color: #f0fff0;");
                } else if ("配送中".equals(newItem.getDeliveryStatus())) {
                    row.setStyle("-fx-background-color: #fff8f0;");
                } else if ("待配送".equals(newItem.getDeliveryStatus())) {
                    row.setStyle("-fx-background-color: #f0f8ff;");
                } else {
                    row.setStyle("");
                }
            });
            return row;
        });
        VBox.setVgrow(table, Priority.ALWAYS);

        getChildren().addAll(title, filterBar, table);
        refresh();
    }

    private Button createFilterBtn(String text, String filterId) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: #2c3e50; -fx-background-radius: 15; " +
                    "-fx-cursor: hand; -fx-font-size: 12; -fx-padding: 6 16;");
        btn.setOnAction(e -> {
            currentFilter = filterId;
            setActiveFilter(btn);
            refresh();
        });
        return btn;
    }

    private void setActiveFilter(Button btn) {
        if (activeFilterBtn != null) {
            activeFilterBtn.setStyle("-fx-background-color: #ecf0f1; -fx-text-fill: #2c3e50; -fx-background-radius: 15; " +
                                    "-fx-cursor: hand; -fx-font-size: 12; -fx-padding: 6 16;");
        }
        activeFilterBtn = btn;
        activeFilterBtn.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-background-radius: 15; " +
                                "-fx-cursor: hand; -fx-font-size: 12; -fx-padding: 6 16; " +
                                "-fx-effect: dropshadow(gaussian, rgba(230,126,34,0.3), 6, 0, 0, 2);");
    }

    private void refresh() {
        java.util.List<Subscription> all = subscriptionService.getAll();
        switch (currentFilter) {
            case "pending" -> data.setAll(all.stream()
                    .filter(s -> "待配送".equals(s.getDeliveryStatus()))
                    .collect(Collectors.toList()));
            case "delivering" -> data.setAll(all.stream()
                    .filter(s -> "配送中".equals(s.getDeliveryStatus()))
                    .collect(Collectors.toList()));
            case "completed" -> data.setAll(all.stream()
                    .filter(s -> "已配送".equals(s.getDeliveryStatus()))
                    .collect(Collectors.toList()));
            default -> data.setAll(all);
        }
    }
}
