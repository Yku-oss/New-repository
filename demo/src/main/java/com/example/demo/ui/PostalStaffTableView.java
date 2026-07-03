//邮局工作人员管理界面，提供工作人员信息的增删改查功能
package com.example.demo.ui;

import com.example.demo.config.SpringContextHolder;
import com.example.demo.entity.PostalStaff;
import com.example.demo.service.PostalStaffService;
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

import java.time.LocalDateTime;

public class PostalStaffTableView extends VBox {

    private final PostalStaffService postalStaffService = SpringContextHolder.getBean(PostalStaffService.class);
    private final TableView<PostalStaff> table = new TableView<>();
    private final ObservableList<PostalStaff> data = FXCollections.observableArrayList();

    public PostalStaffTableView() {
        setPadding(new Insets(20));
        setSpacing(15);
        setStyle("-fx-background-color: #f5f6fa;");

        Label title = new Label("👤 邮局工作人员管理");
        title.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 22));

        HBox toolbar = new HBox(10);
        Button addBtn = new Button("➕ 新增工作人员");
        Button refreshBtn = new Button("🔄 刷新");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-size: 13; -fx-padding: 8 16;");
        refreshBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand; -fx-font-size: 13; -fx-padding: 8 16;");
        toolbar.getChildren().addAll(addBtn, refreshBtn);

        TableColumn<PostalStaff, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(50);

        TableColumn<PostalStaff, String> nameCol = new TableColumn<>("姓名");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(100);

        TableColumn<PostalStaff, String> phoneCol = new TableColumn<>("电话");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneCol.setPrefWidth(120);

        TableColumn<PostalStaff, String> emailCol = new TableColumn<>("邮箱");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(180);

        TableColumn<PostalStaff, String> positionCol = new TableColumn<>("职位");
        positionCol.setCellValueFactory(new PropertyValueFactory<>("position"));
        positionCol.setPrefWidth(100);

        TableColumn<PostalStaff, String> departmentCol = new TableColumn<>("部门");
        departmentCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        departmentCol.setPrefWidth(100);

        TableColumn<PostalStaff, String> statusCol = new TableColumn<>("状态");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(80);

        TableColumn<PostalStaff, LocalDateTime> timeCol = new TableColumn<>("创建时间");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("createTime"));
        timeCol.setPrefWidth(150);

        TableColumn<PostalStaff, Void> actionCol = new TableColumn<>("操作");
        actionCol.setPrefWidth(200);
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("编辑");
            private final Button toggleBtn = new Button("离职");
            private final Button delBtn = new Button("删除");
            private final HBox pane = new HBox(4, editBtn, toggleBtn, delBtn);
            {
                pane.setAlignment(Pos.CENTER);
                editBtn.setStyle("-fx-background-color: #d48806; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11; -fx-padding: 3 10; -fx-background-radius: 3;");
                toggleBtn.setStyle("-fx-background-color: #8e44ad; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11; -fx-padding: 3 10; -fx-background-radius: 3;");
                delBtn.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-cursor: hand; -fx-font-size: 11; -fx-padding: 3 10; -fx-background-radius: 3;");
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                PostalStaff s = getTableView().getItems().get(getIndex());
                editBtn.setOnAction(e -> showEditDialog(s));
                toggleBtn.setText("在职".equals(s.getStatus()) ? "离职" : "复职");
                toggleBtn.setOnAction(e -> {
                    String newStatus = "在职".equals(s.getStatus()) ? "离职" : "在职";
                    postalStaffService.updateStatus(s.getId(), newStatus);
                    refresh();
                });
                delBtn.setOnAction(e -> {
                    if (new Alert(Alert.AlertType.CONFIRMATION, "确认删除「" + s.getName() + "」？").showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                        postalStaffService.delete(s.getId());
                        refresh();
                    }
                });
                setGraphic(pane);
            }
        });

        table.getColumns().addAll(idCol, nameCol, phoneCol, emailCol, positionCol, departmentCol, statusCol, timeCol, actionCol);
        table.setItems(data);
        VBox.setVgrow(table, Priority.ALWAYS);

        refreshBtn.setOnAction(e -> refresh());
        addBtn.setOnAction(e -> showEditDialog(null));

        getChildren().addAll(title, toolbar, table);
        refresh();
    }

    private void refresh() { data.setAll(postalStaffService.getAll()); }

    private void showEditDialog(PostalStaff staff) {
        boolean isNew = staff == null;
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(isNew ? "新增工作人员" : "编辑工作人员");

        GridPane form = new GridPane();
        form.setHgap(10); form.setVgap(10); form.setPadding(new Insets(20));

        TextField nameField = new TextField(isNew ? "" : staff.getName());
        TextField phoneField = new TextField(isNew ? "" : staff.getPhone());
        TextField emailField = new TextField(isNew ? "" : staff.getEmail());
        TextField passwordField = new TextField(isNew ? "123456" : staff.getPassword());
        TextField positionField = new TextField(isNew ? "" : staff.getPosition());
        ComboBox<String> departmentCombo = new ComboBox<>(FXCollections.observableArrayList(
                "营业部", "订阅部", "财务部", "数据分析部", "配送部", "客服部"));
        departmentCombo.setValue(isNew ? "营业部" : staff.getDepartment());
        ComboBox<String> statusCombo = new ComboBox<>(FXCollections.observableArrayList("在职", "离职"));
        statusCombo.setValue(isNew ? "在职" : staff.getStatus());

        form.add(new Label("姓名:"), 0, 0); form.add(nameField, 1, 0);
        form.add(new Label("电话:"), 0, 1); form.add(phoneField, 1, 1);
        form.add(new Label("邮箱:"), 0, 2); form.add(emailField, 1, 2);
        form.add(new Label("密码:"), 0, 3); form.add(passwordField, 1, 3);
        form.add(new Label("职位:"), 0, 4); form.add(positionField, 1, 4);
        form.add(new Label("部门:"), 0, 5); form.add(departmentCombo, 1, 5);
        form.add(new Label("状态:"), 0, 6); form.add(statusCombo, 1, 6);

        Button saveBtn = new Button("保存");
        saveBtn.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-cursor: hand;");
        saveBtn.setOnAction(e -> {
            try {
                PostalStaff s = isNew ? new PostalStaff() : staff;
                s.setName(nameField.getText());
                s.setPhone(phoneField.getText());
                s.setEmail(emailField.getText());
                s.setPassword(passwordField.getText());
                s.setPosition(positionField.getText());
                s.setDepartment(departmentCombo.getValue());
                s.setStatus(statusCombo.getValue());
                if (isNew) postalStaffService.add(s);
                else postalStaffService.update(s);
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
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}
