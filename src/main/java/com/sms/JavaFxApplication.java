package com.sms;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.Getter;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class JavaFxApplication extends Application {

    @Getter
    private static ConfigurableApplicationContext springContext;

    @Override
    public void init() {
        // 在JavaFX初始化时启动Spring Boot
        String[] args = getParameters().getRaw().toArray(new String[0]);
        springContext = new SpringApplicationBuilder()
                .sources(SmsApplication.class)
                .run(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 直接加载登录页面
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        loader.setControllerFactory(springContext::getBean);
        Parent root = loader.load();

        primaryStage.setTitle("学生管理系统 - 登录");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    @Override
    public void stop() {
        // 关闭Spring上下文
        springContext.close();
        Platform.exit();
    }

    public static void main(String[] args) {
        // 启动JavaFX应用
        launch(args);
    }
}