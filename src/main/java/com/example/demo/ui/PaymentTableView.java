package com.example.demo.ui;

import com.example.demo.config.SpringContextHolder;
import com.example.demo.entity.Payment;
import com.example.demo.service.PaymentService;
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
import java.time.LocalDateTime;

public class PaymentTableView extends VBox {

    private final PaymentService paymentService = SpringContextHolder.getBean(PaymentService.class);
    private final TableView<Payment> table = new TableView<>();
    private final ObservableList<Payment> data = FXCollections.observableArrayList();

    public PaymentTableView() {
        setPadding(new Insets(20));
        setSpacing(15);
        setStyle("-fx-background-color: #f5f6fa;");

        Label title = new Label("💳 付款记录");
        title.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 22));

        HBox toolbar = new HBox(10);
        Button refreshBtn = new Button("🔄 刷新");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
        Button addBtn = new Button("➕ 新增付款");
        addBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;");
        toolbar.getChildren().addAll(refreshBtn, addBtn);

        TableColumn<Payment, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id")); idCol.setPrefWidth(50);

        TableColumn<Payment, Integer> subIdCol = new TableColumn<>("订单ID");
        subIdCol.setCellValueFactory(new PropertyValueFactory<>("subscriptionId")); subIdCol.setPrefWidth(80);

        TableColumn<Payment, BigDecimal> amountCol = new TableColumn<>("金额");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount")); amountCol.setPrefWidth(100);

        TableColumn<Payment, String> methodCol = new TableColumn<>("支付方式");
        methodCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod")); methodCol.setPrefWidth(100);

        TableColumn<Payment, String> txIdCol = new TableColumn<>("交易流水");
        txIdCol.setCellValueFactory(new PropertyValueFactory<>("transactionId")); txIdCol.setPrefWidth(180);

        TableColumn<Payment, LocalDateTime> timeCol = new TableColumn<>("支付时间");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("paymentTime")); timeCol.setPrefWidth(150);

        TableColumn<Payment, String> statusCol = new TableColumn<>("状态");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status")); statusCol.setPrefWidth(80);

        table.getColumns().addAll(idCol, subIdCol, amountCol, methodCol, txIdCol, timeCol, statusCol);
        table.setItems(data);
        VBox.setVgrow(table, Priority.ALWAYS);

        refreshBtn.setOnAction(e -> refresh());
        addBtn.setOnAction(e -> showAddDialog());

        getChildren().addAll(title, toolbar, table);
        refresh();
    }

    private void refresh() { data.setAll(paymentService.getAll()); }

    private void showAddDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("新增付款记录");

        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(10); form.setPadding(new Insets(20));

        TextField subIdField = new TextField();
        TextField amountField = new TextField();
        ComboBox<String> methodCombo = new ComboBox<>(FXCollections.observableArrayList("微信", "支付宝", "银行卡"));
        methodCombo.setValue("微信");
        TextField txField = new TextField("TXN" + System.currentTimeMillis());

        form.add(new Label("订单ID:"), 0, 0); form.add(subIdField, 1, 0);
        form.add(new Label("金额:"), 0, 1); form.add(amountField, 1, 1);
        form.add(new Label("支付方式:"), 0, 2); form.add(methodCombo, 1, 2);
        form.add(new Label("交易流水:"), 0, 3); form.add(txField, 1, 3);

        Button saveBtn = new Button("保存");
        saveBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-cursor: hand;");
        saveBtn.setOnAction(e -> {
            try {
                Payment p = new Payment();
                p.setSubscriptionId(Integer.parseInt(subIdField.getText()));
                p.setAmount(new BigDecimal(amountField.getText()));
                p.setPaymentMethod(methodCombo.getValue());
                p.setTransactionId(txField.getText());
                p.setStatus("成功");
                paymentService.add(p);
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
