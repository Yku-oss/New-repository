//订单审批视图 - 邮局工作人员专用的订单审批界面，包含订单详情、订阅时间、订阅地点、订单状态
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class OrderApprovalView extends VBox {

    private final SubscriptionService subscriptionService = SpringContextHolder.getBean(SubscriptionService.class);
    private final CustomerService customerService = SpringContextHolder.getBean(CustomerService.class);
    private final NewspaperService newspaperService = SpringContextHolder.getBean(NewspaperService.class);
    private final TableView<Subscription> table = new TableView<>();
    private final ObservableList<Subscription> data = FXCollections.observableArrayList();

    // 筛选按钮
    private Button allBtn, pendingBtn, approvedBtn, rejectedBtn;
    private Button activeFilterBtn;
    private String currentFilter = "all";

    public OrderApprovalView() {
        setPadding(new Insets(20));
        setSpacing(15);
        setStyle("-fx-background-color: #f5f6fa;");

        Label title = new Label("✅ 订单审批");
        title.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 22));

        // 顶部筛选栏
        HBox filterBar = new HBox(8);
        filterBar.setAlignment(Pos.CENTER_LEFT);

        allBtn = createFilterBtn("📋 全部订单", "all");
        pendingBtn = createFilterBtn("⏳ 待审批", "pending");
        approvedBtn = createFilterBtn("✅ 已通过", "approved");
        rejectedBtn = createFilterBtn("❌ 已驳回", "rejected");

        filterBar.getChildren().addAll(allBtn, pendingBtn, approvedBtn, rejectedBtn);
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

        TableColumn<Subscription, BigDecimal> priceCol = new TableColumn<>("总价(元)");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        priceCol.setPrefWidth(80);

        // === 包含关系：订单详情、订阅时间、订阅地点、订单状态 ===
        TableColumn<Subscription, LocalDate> startCol = new TableColumn<>("订阅时间(起)");
        startCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        startCol.setPrefWidth(110);

        TableColumn<Subscription, LocalDate> endCol = new TableColumn<>("订阅时间(止)");
        endCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        endCol.setPrefWidth(110);

        TableColumn<Subscription, String> addressCol = new TableColumn<>("订阅地点(配送地址)");
        addressCol.setCellValueFactory(new PropertyValueFactory<>("deliveryAddress"));
        addressCol.setPrefWidth(180);

        TableColumn<Subscription, String> payMethodCol = new TableColumn<>("支付方式");
        payMethodCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        payMethodCol.setPrefWidth(80);

        TableColumn<Subscription, String> payStatusCol = new TableColumn<>("支付状态");
        payStatusCol.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        payStatusCol.setPrefWidth(80);

        TableColumn<Subscription, String> approvalCol = new TableColumn<>("订单状态(审批)");
        approvalCol.setCellValueFactory(new PropertyValueFactory<>("approvalStatus"));
        approvalCol.setPrefWidth(110);

        TableColumn<Subscription, String> statusCol = new TableColumn<>("订单状态");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(80);

        TableColumn<Subscription, LocalDateTime> orderTimeCol = new TableColumn<>("下单时间");
        orderTimeCol.setCellValueFactory(new PropertyValueFactory<>("orderTime"));
        orderTimeCol.setPrefWidth(150);

        // 操作列 - 审批/驳回/查看详情
        TableColumn<Subscription, Void> actionCol = new TableColumn<>("审批操作");
        actionCol.setPrefWidth(280);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button detailBtn = new Button("📄 详情");
            private final Button approveBtn = new Button("✅ 通过");
            private final Button rejectBtn = new Button("❌ 驳回");
            private final HBox pane = new HBox(4, detailBtn, approveBtn, rejectBtn);
            {
                pane.setAlignment(Pos.CENTER);
                detailBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11; -fx-padding: 3 8; -fx-background-radius: 3;");
                approveBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11; -fx-padding: 3 8; -fx-background-radius: 3;");
                rejectBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11; -fx-padding: 3 8; -fx-background-radius: 3;");
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Subscription s = getTableView().getItems().get(getIndex());
                boolean isPending = "待审批".equals(s.getApprovalStatus());
                approveBtn.setDisable(!isPending);
                rejectBtn.setDisable(!isPending);

                detailBtn.setOnAction(e -> showOrderDetail(s));
                approveBtn.setOnAction(e -> {
                    subscriptionService.approve(s.getId());
                    refresh();
                });
                rejectBtn.setOnAction(e -> {
                    subscriptionService.reject(s.getId());
                    refresh();
                });
                setGraphic(pane);
            }
        });

        table.getColumns().addAll(idCol, customerCol, newspaperCol, qtyCol, priceCol,
                startCol, endCol, addressCol, payMethodCol, payStatusCol,
                approvalCol, statusCol, orderTimeCol, actionCol);
        table.setItems(data);
        table.setRowFactory(tv -> {
            TableRow<Subscription> row = new TableRow<>();
            row.setPrefHeight(36);
            // 根据审批状态设置行颜色
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem == null) {
                    row.setStyle("");
                } else if ("已通过".equals(newItem.getApprovalStatus())) {
                    row.setStyle("-fx-background-color: #f0fff0;");
                } else if ("已驳回".equals(newItem.getApprovalStatus())) {
                    row.setStyle("-fx-background-color: #fff0f0;");
                } else {
                    row.setStyle("-fx-background-color: #fffff0;");
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
        activeFilterBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 15; " +
                                "-fx-cursor: hand; -fx-font-size: 12; -fx-padding: 6 16; " +
                                "-fx-effect: dropshadow(gaussian, rgba(52,152,219,0.3), 6, 0, 0, 2);");
    }

    private void refresh() {
        java.util.List<Subscription> all = subscriptionService.getAll();
        switch (currentFilter) {
            case "pending" -> data.setAll(all.stream()
                    .filter(s -> "待审批".equals(s.getApprovalStatus()))
                    .collect(Collectors.toList()));
            case "approved" -> data.setAll(all.stream()
                    .filter(s -> "已通过".equals(s.getApprovalStatus()))
                    .collect(Collectors.toList()));
            case "rejected" -> data.setAll(all.stream()
                    .filter(s -> "已驳回".equals(s.getApprovalStatus()))
                    .collect(Collectors.toList()));
            default -> data.setAll(all);
        }
    }

    private void showOrderDetail(Subscription s) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("订单详情 - #" + s.getId());
        dialog.setHeaderText("📄 订单 #" + s.getId() + " 详细信息");

        Customer customer = customerService.getById(s.getCustomerId());
        Newspaper newspaper = newspaperService.getById(s.getNewspaperId());

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(8);
        grid.setPadding(new Insets(20));

        // 订单详情
        addDetailRow(grid, 0, "订单ID", String.valueOf(s.getId()));
        addDetailRow(grid, 1, "客户姓名", customer != null ? customer.getName() : "");
        addDetailRow(grid, 2, "客户电话", customer != null ? customer.getPhone() : "");
        addDetailRow(grid, 3, "客户地址", customer != null ? customer.getAddress() : "");
        addDetailRow(grid, 4, "报纸名称", newspaper != null ? newspaper.getName() : "");
        addDetailRow(grid, 5, "报纸价格", newspaper != null ? newspaper.getPrice() + " 元" : "");

        // 订阅时间
        addDetailRow(grid, 6, "订阅开始日期", s.getStartDate() != null ? s.getStartDate().toString() : "");
        addDetailRow(grid, 7, "订阅结束日期", s.getEndDate() != null ? s.getEndDate().toString() : "");

        // 订阅地点
        addDetailRow(grid, 8, "配送地址", s.getDeliveryAddress());

        // 订单状态
        addDetailRow(grid, 9, "订阅数量", String.valueOf(s.getQuantity()));
        addDetailRow(grid, 10, "总价", s.getTotalPrice() != null ? s.getTotalPrice() + " 元" : "");
        addDetailRow(grid, 11, "支付方式", s.getPaymentMethod());
        addDetailRow(grid, 12, "支付状态", s.getPaymentStatus());
        addDetailRow(grid, 13, "审批状态", s.getApprovalStatus());
        addDetailRow(grid, 14, "订单状态", s.getStatus());
        addDetailRow(grid, 15, "下单时间", s.getOrderTime() != null ? s.getOrderTime().toString() : "");

        ButtonType closeBtn = new ButtonType("关闭", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(closeBtn);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefWidth(500);
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        dialog.showAndWait();
    }

    private void addDetailRow(GridPane grid, int row, String label, String value) {
        Label lbl = new Label(label + ":");
        lbl.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 13));
        lbl.setStyle("-fx-text-fill: #555;");
        Label val = new Label(value != null ? value : "");
        val.setFont(Font.font("Microsoft YaHei", 13));
        val.setStyle("-fx-text-fill: #2c3e50;");
        if (value != null && (value.contains("待审批") || value.contains("待处理"))) {
            val.setTextFill(Color.web("#e67e22"));
            val.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 13));
        } else if (value != null && (value.contains("已通过") || value.contains("已支付"))) {
            val.setTextFill(Color.web("#27ae60"));
            val.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 13));
        } else if (value != null && (value.contains("已驳回") || value.contains("已取消"))) {
            val.setTextFill(Color.web("#e74c3c"));
            val.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 13));
        }
        grid.add(lbl, 0, row);
        grid.add(val, 1, row);
    }
}
