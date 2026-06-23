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
    private Button[] sidebarButtons;

    public MainScene(Stage stage, Customer currentUser) {
        this.currentUser = currentUser;

        // 初始化所有视图
        this.dashboardView = new DashboardView(viewId -> {
            // 查找侧边栏中对应的导航按钮并切换
            for (Button btn : sidebarButtons) {
                String text = btn.getText();
                String id = text.contains("首页") ? "dashboard" :
                            text.contains("报纸") ? "newspaper" :
                            text.contains("客户") ? "customer" :
                            text.contains("分类") ? "category" :
                            text.contains("供应商") ? "supplier" :
                            text.contains("订阅") ? "subscription" :
                            text.contains("付款") ? "payment" :
                            text.contains("库存") ? "inventory" :
                            text.contains("分析") ? "analysis" : "";
                if (id.equals(viewId)) {
                    switchView(btn, viewId);
                    return;
                }
            }
        });
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
        this.scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
    }

    private HBox createTopBar(Stage stage) {
        HBox bar = new HBox();
        bar.setAlignment(Pos.CENTER_RIGHT);
        bar.setPadding(new Insets(12, 25, 12, 25));
        bar.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 0%, #2c3e50, #3d566e); " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 8, 0, 0, 3);");

        Label title = new Label("📰 邮局订报管理系统");
        title.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 17));
        title.setTextFill(Color.WHITE);
        title.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(title, Priority.ALWAYS);

        // 用户头像样式
        Label avatarLabel = new Label("👤");
        avatarLabel.setStyle("-fx-font-size: 18; -fx-padding: 0 5 0 0;");

        Label userLabel = new Label(currentUser.getName() + " (" + currentUser.getRole() + ")");
        userLabel.setTextFill(Color.web("#ecf0f1"));
        userLabel.setFont(Font.font("Microsoft YaHei", 13));

        HBox userInfo = new HBox(5, avatarLabel, userLabel);
        userInfo.setAlignment(Pos.CENTER_RIGHT);
        userInfo.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 15; -fx-padding: 5 15;");
        HBox.setMargin(userInfo, new Insets(0, 15, 0, 0));

        Button logoutBtn = new Button("退出登录");
        logoutBtn.setStyle("-fx-background-color: rgba(231,76,60,0.8); -fx-text-fill: white; -fx-background-radius: 15; " +
                          "-fx-cursor: hand; -fx-font-size: 12; -fx-padding: 6 16; " +
                          "-fx-effect: dropshadow(gaussian, rgba(231,76,60,0.3), 6, 0, 0, 2);");
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle(
            "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 15; " +
            "-fx-cursor: hand; -fx-font-size: 12; -fx-padding: 6 16; " +
            "-fx-effect: dropshadow(gaussian, rgba(231,76,60,0.5), 10, 0, 0, 4);"));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle(
            "-fx-background-color: rgba(231,76,60,0.8); -fx-text-fill: white; -fx-background-radius: 15; " +
            "-fx-cursor: hand; -fx-font-size: 12; -fx-padding: 6 16; " +
            "-fx-effect: dropshadow(gaussian, rgba(231,76,60,0.3), 6, 0, 0, 2);"));
        logoutBtn.setOnAction(e -> {
            LoginView loginView = new LoginView(stage);
            Scene loginScene = new Scene(loginView, 420, 520);
            loginScene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            stage.setScene(loginScene);
            stage.setResizable(false);
            stage.setMaximized(false);
            stage.sizeToScene();
        });

        bar.getChildren().addAll(title, userInfo, logoutBtn);
        return bar;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(2);
        sidebar.setPrefWidth(220);
        sidebar.setMinWidth(220);
        sidebar.setPadding(new Insets(15, 0, 15, 0));
        sidebar.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 100%, #2c3e50, #34495e); " +
                         "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 5, 0, 0, 0);");

        // 侧边栏顶部 Logo 区域
        VBox logoArea = new VBox(5);
        logoArea.setAlignment(Pos.CENTER);
        logoArea.setPadding(new Insets(0, 15, 20, 15));
        logoArea.setStyle("-fx-border-color: rgba(255,255,255,0.1); -fx-border-width: 0 0 1 0;");

        Label logoIcon = new Label("📰");
        logoIcon.setStyle("-fx-font-size: 32;");

        Label navTitle = new Label("功能导航");
        navTitle.setFont(Font.font("Microsoft YaHei", FontWeight.BOLD, 13));
        navTitle.setTextFill(Color.web("#8e99a4"));
        navTitle.setPadding(new Insets(15, 15, 10, 15));

        logoArea.getChildren().addAll(logoIcon);
        sidebar.getChildren().addAll(logoArea, navTitle);

        // 导航按钮定义
        Button[] navButtons = createNavButtons();
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
            btn.setPadding(new Insets(12, 18, 12, 18));
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #bdc3c7; " +
                        "-fx-font-size: 13; -fx-cursor: hand; -fx-border: none; " +
                        "-fx-background-radius: 8; -fx-margin: 0 8;");
            btn.setFont(Font.font("Microsoft YaHei", 13));

            String viewId = navItems[i][1];
            btn.setOnAction(e -> switchView(btn, viewId));

            // 悬停效果 - 更平滑
            btn.setOnMouseEntered(e -> {
                if (btn != activeNavBtn) {
                    btn.setStyle("-fx-background-color: rgba(255,255,255,0.08); -fx-text-fill: white; " +
                                "-fx-font-size: 13; -fx-cursor: hand; -fx-border: none; " +
                                "-fx-background-radius: 8;");
                }
            });
            btn.setOnMouseExited(e -> {
                if (btn != activeNavBtn) {
                    btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #bdc3c7; " +
                                "-fx-font-size: 13; -fx-cursor: hand; -fx-border: none; " +
                                "-fx-background-radius: 8;");
                }
            });

            buttons[i] = btn;
        }

        // 默认选中第一个
        setActive(buttons[0]);

        this.sidebarButtons = buttons;
        return buttons;
    }

    private void setActive(Button btn) {
        if (activeNavBtn != null) {
            activeNavBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #bdc3c7; " +
                                "-fx-font-size: 13; -fx-cursor: hand; -fx-border: none; " +
                                "-fx-background-radius: 8;");
        }
        activeNavBtn = btn;
        activeNavBtn.setStyle("-fx-background-color: rgba(52,152,219,0.2); -fx-text-fill: white; " +
                            "-fx-font-size: 13; -fx-cursor: hand; -fx-border: none; " +
                            "-fx-background-radius: 8; " +
                            "-fx-border-width: 0 0 0 3; -fx-border-color: #3498db; " +
                            "-fx-effect: dropshadow(gaussian, rgba(52,152,219,0.1), 4, 0, 0, 0);");
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
