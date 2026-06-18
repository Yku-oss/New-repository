package com.example.demo.ui;

import com.example.demo.config.SpringContextHolder;
import com.example.demo.entity.Customer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * 主场景 - 左侧导航 + 右侧内容区
 */
public class MainScene {

    private final Scene scene;
    private final StackPane contentArea = new StackPane();
    private final Customer currentUser;

    // 各个视图
    private final DashboardView dashboardView;
    private final NewspaperTableView newspaperView;
    private final CustomerTableView customerView;
    private final CategoryTableView categoryView;
    private final SupplierTableView supplierView;
    private final SubscriptionTableView subscriptionView;
    private final PaymentTableView paymentView;
    private final InventoryLogTableView inventoryLogView;
    private final AnalysisView analysisView;

    // 导航按钮
    private Button activeNavBtn = null;

    public MainScene(Stage stage, Customer currentUser) {
        this.currentUser = currentUser;

        // 初始化所有视图
        this.dashboardView = new DashboardView();
        this.newspaperView = new NewspaperTableView();
        this.customerView = new CustomerTableView();
        this.categoryView = new CategoryTableView();
        this.supplierView = new SupplierTableView();
        this.subscriptionView = new SubscriptionTableView();
        this.paymentView = new PaymentTableView();
        this.inventoryLogView = new InventoryLogTableView();
        this.analysisView = new AnalysisView();

        // 左侧导航栏
        VBox sidebar = createSidebar();

        // 右侧内容区
        contentArea.setPadding(new Insets(0));
        contentArea.getChildren().add(dashboardView);

        // 整体布局
        BorderPane root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(contentArea);

        // 顶部标题栏
        HBox topBar = createTopBar(stage);
        root.setTop(topBar);

        this.scene = new Scene(root, 1200, 800);
    }

    private HBox createTopBar(Stage stage) {
        HBox bar = new HBox();
        bar.setAlignment(Pos.CENTER_RIGHT);
        bar.setPadding(new Insets(10, 20, 10, 20));
        bar.setStyle("-fx-background-color: #2c3e50;");

        Label title = new Label("📰 邮局订报管理系统");
        title.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 16));
        title.setTextFill(Color.WHITE);
        title.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(title, Priority.ALWAYS);

        Label userLabel = new Label("👤 " + currentUser.getName() + " (" + currentUser.getRole() + ")");
        userLabel.setTextFill(Color.WHITE);
        userLabel.setFont(Font.font("Microsoft YaHei", 13));
        HBox.setMargin(userLabel, new Insets(0, 15, 0, 0));

        Button logoutBtn = new Button("退出");
        logoutBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 4; -fx-cursor: hand;");
        logoutBtn.setOnAction(e -> {
            LoginView loginView = new LoginView(stage);
            Scene loginScene = new Scene(loginView, 420, 520);
            loginScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            stage.setScene(loginScene);
            stage.setResizable(false);
            stage.setMaximized(false);
            stage.sizeToScene();
        });

        bar.getChildren().addAll(title, userLabel, logoutBtn);
        return bar;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(2);
        sidebar.setPrefWidth(200);
        sidebar.setMinWidth(200);
        sidebar.setPadding(new Insets(10, 0, 10, 0));
        sidebar.setStyle("-fx-background-color: #34495e;");

        Label navTitle = new Label("功能导航");
        navTitle.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 14));
        navTitle.setTextFill(Color.web("#bdc3c7"));
        navTitle.setPadding(new Insets(10, 15, 15, 15));

        // 导航按钮定义
        Button[] navButtons = createNavButtons();
        sidebar.getChildren().add(navTitle);
        sidebar.getChildren().addAll(navButtons);

        // 弹性空间
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().add(spacer);

        return sidebar;
    }

    private Button[] createNavButtons() {
        String[][] navItems = {
            {"🏠  首页概览", "dashboard"},
            {"📰  报纸管理", "newspaper"},
            {"👥  客户管理", "customer"},
            {"📂  分类管理", "category"},
            {"🏢  供应商管理", "supplier"},
            {"📋  订阅管理", "subscription"},
            {"💳  付款记录", "payment"},
            {"📦  库存日志", "inventory"},
            {"📊  数据分析", "analysis"},
        };

        Button[] buttons = new Button[navItems.length];

        for (int i = 0; i < navItems.length; i++) {
            Button btn = new Button(navItems[i][0]);
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setAlignment(Pos.CENTER_LEFT);
            btn.setPadding(new Insets(12, 15, 12, 15));
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ecf0f1; " +
                        "-fx-font-size: 13; -fx-cursor: hand; -fx-border: none;");
            btn.setFont(Font.font("Microsoft YaHei", 13));

            String viewId = navItems[i][1];
            btn.setOnAction(e -> switchView(btn, viewId));

            // 悬停效果
            btn.setOnMouseEntered(e -> {
                if (btn != activeNavBtn) {
                    btn.setStyle("-fx-background-color: #3d566e; -fx-text-fill: white; " +
                                "-fx-font-size: 13; -fx-cursor: hand; -fx-border: none;");
                }
            });
            btn.setOnMouseExited(e -> {
                if (btn != activeNavBtn) {
                    btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ecf0f1; " +
                                "-fx-font-size: 13; -fx-cursor: hand; -fx-border: none;");
                }
            });

            buttons[i] = btn;
        }

        // 默认选中第一个
        setActive(buttons[0]);

        return buttons;
    }

    private void setActive(Button btn) {
        if (activeNavBtn != null) {
            activeNavBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ecf0f1; " +
                                "-fx-font-size: 13; -fx-cursor: hand; -fx-border: none;");
        }
        activeNavBtn = btn;
        activeNavBtn.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; " +
                            "-fx-font-size: 13; -fx-cursor: hand; -fx-border: none; -fx-border-width: 0 0 0 3; -fx-border-color: #3498db;");
    }

    private void switchView(Button btn, String viewId) {
        setActive(btn);
        contentArea.getChildren().clear();

        switch (viewId) {
            case "dashboard" -> contentArea.getChildren().add(dashboardView);
            case "newspaper" -> contentArea.getChildren().add(newspaperView);
            case "customer" -> contentArea.getChildren().add(customerView);
            case "category" -> contentArea.getChildren().add(categoryView);
            case "supplier" -> contentArea.getChildren().add(supplierView);
            case "subscription" -> contentArea.getChildren().add(subscriptionView);
            case "payment" -> contentArea.getChildren().add(paymentView);
            case "inventory" -> contentArea.getChildren().add(inventoryLogView);
            case "analysis" -> contentArea.getChildren().add(analysisView);
        }
    }

    public Scene getScene() {
        return scene;
    }
}
