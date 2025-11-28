package com.sms.controller.student;

import com.sms.entity.Student;
import com.sms.service.StudentService;
import com.sms.utils.PasswordEncoderUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class StudentSystemManagementController {

    @FXML private Label currentUsernameLabel;
    @FXML private TextField newUsernameField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmNewPasswordField;
    @FXML private Label messageLabel;

    @Autowired
    private StudentService studentService;

    @Autowired
    private PasswordEncoderUtil passwordEncoder;

    private Student currentStudent;

    public void setCurrentStudent(Student student) {
        this.currentStudent = student;
        updateDisplay();
    }

    private void updateDisplay() {
        if (currentStudent != null) {
            currentUsernameLabel.setText(currentStudent.getName());
        }
    }

    @FXML
    private void handleChangeUsername() {
        String newUsername = newUsernameField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (newUsername.isEmpty() || confirmPassword.isEmpty()) {
            showMessage("请填写所有字段");
            return;
        }

        if (newUsername.equals(currentStudent.getName())) {
            showMessage("新用户名与当前用户名相同");
            return;
        }

        // 验证密码
        if (!passwordEncoder.matches(confirmPassword, currentStudent.getPassword())) {
            showMessage("密码错误");
            return;
        }

        // 检查用户名是否已存在
        if (studentService.existsByName(newUsername)) {
            showMessage("用户名已存在，请选择其他用户名");
            return;
        }

        try {
            currentStudent.setName(newUsername);
            studentService.save(currentStudent);
            currentUsernameLabel.setText(newUsername);
            newUsernameField.clear();
            confirmPasswordField.clear();
            showAlert("成功", "用户名修改成功");
            showMessage("");
        } catch (Exception e) {
            showAlert("错误", "修改用户名失败: " + e.getMessage());
        }
    }

    @FXML
    private void handleChangePassword() {
        String currentPassword = currentPasswordField.getText().trim();
        String newPassword = newPasswordField.getText().trim();
        String confirmNewPassword = confirmNewPasswordField.getText().trim();

        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            showMessage("请填写所有字段");
            return;
        }

        // 验证当前密码
        if (!passwordEncoder.matches(currentPassword, currentStudent.getPassword())) {
            showMessage("当前密码错误");
            return;
        }

        // 验证新密码长度
        if (newPassword.length() < 6) {
            showMessage("新密码长度至少6位");
            return;
        }

        // 验证新密码确认
        if (!newPassword.equals(confirmNewPassword)) {
            showMessage("新密码与确认密码不一致");
            return;
        }

        // 验证新密码与旧密码不同
        if (passwordEncoder.matches(newPassword, currentStudent.getPassword())) {
            showMessage("新密码不能与当前密码相同");
            return;
        }

        try {
            // 使用加密的新密码
            currentStudent.setPassword(passwordEncoder.encode(newPassword));
            studentService.save(currentStudent);
            currentPasswordField.clear();
            newPasswordField.clear();
            confirmNewPasswordField.clear();
            showAlert("成功", "密码修改成功");
            showMessage("");
        } catch (Exception e) {
            showAlert("错误", "修改密码失败: " + e.getMessage());
        }
    }

    private void showMessage(String message) {
        messageLabel.setText(message);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}