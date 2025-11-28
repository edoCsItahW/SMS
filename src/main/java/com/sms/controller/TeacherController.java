package com.sms.controller;

import com.sms.JavaFxApplication;
import com.sms.entity.Teacher;
import com.sms.service.TeacherService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class TeacherController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private VBox contentArea;

    private final TeacherService teacherService;
    private Teacher currentTeacher;

    @Autowired
    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    public void setCurrentTeacher(Teacher teacher) {
        this.currentTeacher = teacher;
        updateWelcomeMessage();
        showStudentManagement(); // 默认显示学生信息管理
    }

    @FXML
    public void initialize() {
        // 初始化内容
    }

    private void updateWelcomeMessage() {
        if (currentTeacher != null) {
            welcomeLabel.setText("欢迎您，" + currentTeacher.getName());
        }
    }

    @FXML
    private void showCourseManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/teacher/teacher-course-management.fxml"));
            loader.setControllerFactory(JavaFxApplication.getSpringContext()::getBean);
            VBox courseManagementView = loader.load();

            com.sms.controller.teacher.TeacherCourseManagementController controller = loader.getController();
            controller.setCurrentTeacher(currentTeacher);

            contentArea.getChildren().setAll(courseManagementView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("错误", "加载课程管理页面失败: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            loader.setControllerFactory(JavaFxApplication.getSpringContext()::getBean);
            Parent root = loader.load();

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle("学生管理系统 - 登录");
        } catch (IOException e) {
            showAlert("错误", "退出登录失败: " + e.getMessage());
        }
    }

    // 菜单点击处理方法
    @FXML
    private void showStudentManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/teacher/teacher-student-management.fxml"));
            loader.setControllerFactory(JavaFxApplication.getSpringContext()::getBean);
            VBox studentManagementView = loader.load();

            com.sms.controller.teacher.TeacherStudentManagementController controller = loader.getController();
            controller.setCurrentTeacher(currentTeacher);

            contentArea.getChildren().setAll(studentManagementView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("错误", "加载学生信息管理页面失败: " + e.getMessage());
        }
    }

    @FXML
    private void showAttendanceManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/teacher/teacher-attendance-management.fxml"));
            loader.setControllerFactory(JavaFxApplication.getSpringContext()::getBean);
            VBox attendanceManagementView = loader.load();

            com.sms.controller.teacher.TeacherAttendanceManagementController controller = loader.getController();
            controller.setCurrentTeacher(currentTeacher);

            contentArea.getChildren().setAll(attendanceManagementView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("错误", "加载考勤管理页面失败: " + e.getMessage());
        }
    }

    @FXML
    private void showVacationManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/teacher/teacher-vacation-management.fxml"));
            loader.setControllerFactory(JavaFxApplication.getSpringContext()::getBean);
            VBox vacationManagementView = loader.load();

            com.sms.controller.teacher.TeacherVacationManagementController controller = loader.getController();
            controller.setCurrentTeacher(currentTeacher);

            contentArea.getChildren().setAll(vacationManagementView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("错误", "加载请假信息管理页面失败: " + e.getMessage());
        }
    }

    @FXML
    private void showGradeManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/teacher/teacher-grade-management.fxml"));
            loader.setControllerFactory(JavaFxApplication.getSpringContext()::getBean);
            VBox gradeManagementView = loader.load();

            com.sms.controller.teacher.TeacherGradeManagementController controller = loader.getController();
            controller.setCurrentTeacher(currentTeacher);

            contentArea.getChildren().setAll(gradeManagementView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("错误", "加载成绩信息管理页面失败: " + e.getMessage());
        }
    }

    @FXML
    private void showSystemManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/teacher/teacher-system-management.fxml"));
            loader.setControllerFactory(JavaFxApplication.getSpringContext()::getBean);
            VBox systemManagementView = loader.load();

            com.sms.controller.teacher.TeacherSystemManagementController controller = loader.getController();
            controller.setCurrentTeacher(currentTeacher);

            contentArea.getChildren().setAll(systemManagementView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("错误", "加载系统管理页面失败: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}