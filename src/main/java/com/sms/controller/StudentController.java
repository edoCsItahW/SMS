package com.sms.controller;

import com.sms.JavaFxApplication;
import com.sms.entity.Student;
import com.sms.service.StudentService;
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
public class StudentController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private VBox contentArea;

    private final StudentService studentService;
    private Student currentStudent;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    public void setCurrentStudent(Student student) {
        this.currentStudent = student;
        updateWelcomeMessage();
        showPersonalInfo(); // 默认显示个人信息
    }

    @FXML
    public void initialize() {
        // 初始化内容
    }

    private void updateWelcomeMessage() {
        if (currentStudent != null) {
            welcomeLabel.setText("欢迎您，" + currentStudent.getName());
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
    private void showPersonalInfo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student/student-personal-info.fxml"));
            loader.setControllerFactory(JavaFxApplication.getSpringContext()::getBean);
            VBox personalInfoView = loader.load();

            // 获取控制器并设置当前学生
            com.sms.controller.student.StudentPersonalInfoController controller = loader.getController();
            controller.setCurrentStudent(currentStudent);

            contentArea.getChildren().setAll(personalInfoView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("错误", "加载个人信息页面失败: " + e.getMessage());
        }
    }

    @FXML
    private void showCourseSchedule() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student/student-course-schedule.fxml"));
            loader.setControllerFactory(JavaFxApplication.getSpringContext()::getBean);
            VBox courseScheduleView = loader.load();

            com.sms.controller.student.StudentCourseScheduleController controller = loader.getController();
            controller.setCurrentStudent(currentStudent);

            contentArea.getChildren().setAll(courseScheduleView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("错误", "加载课表查询页面失败: " + e.getMessage());
        }
    }

    @FXML
    private void showCourseSelection() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student/student-course-selection.fxml"));
            loader.setControllerFactory(JavaFxApplication.getSpringContext()::getBean);
            VBox courseSelectionView = loader.load();

            com.sms.controller.student.StudentCourseSelectionController controller = loader.getController();
            controller.setCurrentStudent(currentStudent);

            contentArea.getChildren().setAll(courseSelectionView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("错误", "加载选课页面失败: " + e.getMessage());
        }
    }

    @FXML
    private void showGradeQuery() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student/student-grades.fxml"));
            loader.setControllerFactory(JavaFxApplication.getSpringContext()::getBean);
            VBox gradesView = loader.load();

            com.sms.controller.student.StudentGradesController controller = loader.getController();
            controller.setCurrentStudent(currentStudent);

            contentArea.getChildren().setAll(gradesView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("错误", "加载成绩查询页面失败: " + e.getMessage());
        }
    }

    @FXML
    private void showVacation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student/student-vacation.fxml"));
            loader.setControllerFactory(JavaFxApplication.getSpringContext()::getBean);
            VBox vacationView = loader.load();

            com.sms.controller.student.StudentVacationController controller = loader.getController();
            controller.setCurrentStudent(currentStudent);

            contentArea.getChildren().setAll(vacationView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("错误", "加载请假页面失败: " + e.getMessage());
        }
    }

    @FXML
    private void showSystemManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student/student-system-management.fxml"));
            loader.setControllerFactory(JavaFxApplication.getSpringContext()::getBean);
            VBox systemManagementView = loader.load();

            com.sms.controller.student.StudentSystemManagementController controller = loader.getController();
            controller.setCurrentStudent(currentStudent);

            contentArea.getChildren().setAll(systemManagementView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("错误", "加载系统管理页面失败: " + e.getMessage());
        }
    }

    private void clearContent() {
        contentArea.getChildren().clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}