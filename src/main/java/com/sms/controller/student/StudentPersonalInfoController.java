package com.sms.controller.student;

import com.sms.JavaFxApplication;
import com.sms.entity.Student;
import com.sms.service.StudentService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Optional;

@Controller
public class StudentPersonalInfoController {

    @FXML private Label studentIdLabel;
    @FXML private Label nameLabel;
    @FXML private Label genderLabel;
    @FXML private Label classNameLabel;
    @FXML private Label phoneLabel;
    @FXML private Label emailLabel;

    @Autowired
    private StudentService studentService;

    private Student currentStudent;

    public void setCurrentStudent(Student student) {
        this.currentStudent = student;
        updateDisplay();
    }

    private void updateDisplay() {
        if (currentStudent != null) {
            studentIdLabel.setText(currentStudent.getStudentId());
            nameLabel.setText(currentStudent.getName());
            genderLabel.setText(currentStudent.getGender().toString());
            classNameLabel.setText(currentStudent.getClassName());
            phoneLabel.setText(currentStudent.getPhone() != null ? currentStudent.getPhone() : "未设置");
            emailLabel.setText(currentStudent.getEmail() != null ? currentStudent.getEmail() : "未设置");
        }
    }

    @FXML
    private void handleEditInfo() {
        try {
            // 创建编辑对话框
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("修改个人信息");
            dialog.setHeaderText("请修改您的个人信息");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/student/student-edit-info.fxml"));
            loader.setControllerFactory(JavaFxApplication.getSpringContext()::getBean);
            dialog.getDialogPane().setContent(loader.load());

            StudentEditInfoController editController = loader.getController();
            editController.setCurrentStudent(currentStudent);

            // 添加按钮
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            // 显示对话框并等待用户响应
            Optional<ButtonType> result = dialog.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (editController.validateInput()) {
                    Student updatedStudent = editController.getUpdatedStudent();
                    studentService.save(updatedStudent);
                    currentStudent = updatedStudent; // 更新当前学生对象
                    updateDisplay();
                    showAlert("成功", "个人信息修改成功");
                } else {
                    showAlert("错误", "输入信息不合法，请检查班级、电话和邮箱格式");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("错误", "加载编辑对话框失败: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("错误", "保存失败: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}