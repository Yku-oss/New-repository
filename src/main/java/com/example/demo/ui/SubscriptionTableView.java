package com.example.demo.ui;

import com.example.demo.config.SpringContextHolder;
import com.example.demo.entity.*;
import com.example.demo.service.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SubscriptionTableView extends VBox {

    private final SubscriptionService subscriptionService = SpringContextHolder.getBean(SubscriptionService.class);
    private final CustomerService customerService = SpringContextHolder.getBean(CustomerService.class);
    private final NewspaperService newspaperService = SpringContextHolder.getBean(NewspaperService.class);
    private final TableView<Subscription> table = new TableView<>();
    private final ObservableList<Subscription> data = FXCollections.observableArrayList();

    public SubscriptionTableView() {
        setPadding(new Insets(20));
        setSpacing(15);
        setStyle("-fx-background-color: #f5f6fa;");

        Label title = new Label("📋 订阅管理");
        title.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 22));

        HBox toolbar = new HBox(10);
        Button addBtn = new Button("➕ 新增订阅");
        Button refreshBtn = new Button("🔄 刷新");
        addBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
        toolbar.getChildren().addAll(addBtn, refreshBtn);

        TableColumn<Subscription, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<Subscription, String> customerCol = new TableColumn<>("客户");
        customerCol.setCellValueFactory(cd -> {
            Customer c = customerService.getById(cd.getValue().getCustomerId());
            return new SimpleStringProperty(c != null ? c.getName() : "");
        });
        customerCol.setPrefWidth(100);

        TableColumn<Subscription, String> newspaperCol = new TableColumn<>("报纸");
        newspaperCol.setCellValueFactory(cd -> {
            Newspaper n = newspaperService.getById(cd.getValue().getNewspaperId());
            return new SimpleStringProperty(n != null ? n.getName() : "");
        });
        newspaperCol.setPrefWidth(150);

        TableColumn<Subscription, Integer> qtyCol = new TableColumn<>("数量");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        qtyCol.setPrefWidth(60);

        TableColumn<Subscription, BigDecimal> priceCol = new TableColumn<>("总价");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        priceCol.setPrefWidth(80);

        TableColumn<Subscription, LocalDate> startCol = new TableColumn<>("开始日期");
        startCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        startCol.setPrefWidth(100);

        TableColumn<Subscription, LocalDate> endCol = new TableColumn<>("结束日期");
        endCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        endCol.setPrefWidth(100);

        TableColumn<Subscription, String> payStatusCol = new TableColumn<>("支付状态");
        payStatusCol.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        payStatusCol.setPrefWidth(90);

        TableColumn<Subscription, String> approvalCol = new TableColumn<>("审批状态");
        approvalCol.setCellValueFactory(new PropertyValueFactory<>("approvalStatus"));
        approvalCol.setPrefWidth(90);

        TableColumn<Subscription, String> statusCol = new TableColumn<>("状态");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(80);

        TableColumn<Subscription, Void> actionCol = new TableColumn<>("操作");
        actionCol.setPrefWidth(220);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button approveBtn = new Button("审批通过");
            private final Button rejectBtn = new Button("驳回");
            private final Button delBtn = new Button("删除");
            private final HBox pane = new HBox(5, approveBtn, rejectBtn, delBtn);
            { pane.setAlignment(Pos.CENTER);
              approveBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-cursor: hand;");
              rejectBtn.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-cursor: hand;");
              delBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;"); }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                Subscription s = getTableView().getItems().get(getIndex());
                approveBtn.setDisable(!s.getApprovalStatus().equals("待审批"));
                rejectBtn.setDisable(!s.getApprovalStatus().equals("待审批"));
                approveBtn.setOnAction(e -> {
                    subscriptionService.approve(s.getId());
                    refresh();
                });
                rejectBtn.setOnAction(e -> {
                    subscriptionService.reject(s.getId());
                    refresh();
                });
                delBtn.setOnAction(e -> {
                    if (new Alert(Alert.AlertType.CONFIRMATION, "确认删除订阅 #" + s.getId() + "？").showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                        subscriptionService.cancel(s.getId());
                        refresh();
                    }
                });
                setGraphic(pane);
            }
        });

        table.getColumns().addAll(idCol, customerCol, newspaperCol, qtyCol, priceCol, startCol, endCol, payStatusCol, approvalCol, statusCol, actionCol);
        table.setItems(data);
        VBox.setVgrow(table, Priority.ALWAYS);

        refreshBtn.setOnAction(e -> refresh());
        addBtn.setOnAction(e -> showAddDialog());

        getChildren().addAll(title, toolbar, table);
        refresh();
    }

    private void refresh() { data.setAll(subscriptionService.getAll()); }

    private void showAddDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("新增订阅");

        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(10); form.setPadding(new Insets(20));

        ComboBox<Customer> customerCombo = new ComboBox<>(FXCollections.observableArrayList(customerService.getAll()));
        ComboBox<Newspaper> newspaperCombo = new ComboBox<>(FXCollections.observableArrayList(newspaperService.getAll()));
        TextField qtyField = new TextField("1");
        DatePicker startPicker = new DatePicker(LocalDate.now());
        DatePicker endPicker = new DatePicker(LocalDate.now().plusMonths(6));
        TextField addressField = new TextField();
        ComboBox<String> paymentCombo = new ComboBox<>(FXCollections.observableArrayList("微信", "支付宝", "银行卡"));
        paymentCombo.setValue("微信");

        form.add(new Label("客户:"), 0, 0); form.add(customerCombo, 1, 0);
        form.add(new Label("报纸:"), 0, 1); form.add(newspaperCombo, 1, 1);
        form.add(new Label("数量:"), 0, 2); form.add(qtyField, 1, 2);
        form.add(new Label("开始日期:"), 0, 3); form.add(startPicker, 1, 3);
        form.add(new Label("结束日期:"), 0, 4); form.add(endPicker, 1, 4);
        form.add(new Label("配送地址:"), 0, 5); form.add(addressField, 1, 5);
        form.add(new Label("支付方式:"), 0, 6); form.add(paymentCombo, 1, 6);

        // 选择报纸后自动填充地址
        newspaperCombo.setOnAction(e -> {
            Newspaper n = newspaperCombo.getValue();
            if (n != null && addressField.getText().isEmpty()) {
                // 不自动填充
            }
        });

        Button saveBtn = new Button("保存");
        saveBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-cursor: hand;");
        saveBtn.setOnAction(e -> {
            try {
                Subscription s = new Subscription();
                s.setCustomerId(customerCombo.getValue().getId());
                s.setNewspaperId(newspaperCombo.getValue().getId());
                s.setQuantity(Integer.parseInt(qtyField.getText()));
                s.setStartDate(startPicker.getValue());
                s.setEndDate(endPicker.getValue());
                s.setDeliveryAddress(addressField.getText());
                s.setPaymentMethod(paymentCombo.getValue());
                subscriptionService.add(s);
                refresh(); dialog.close();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "输入有误: " + ex.getMessage()).show();
            }
        });

        Button cancelBtn = new Button("取消");
        cancelBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> dialog.close());

        VBox root = new VBox(10, form, new HBox(10, saveBtn, cancelBtn));
        root.setPadding(new Insets(10));
        dialog.setScene(new Scene(root));
        dialog.showAndWait();
    }
}
