package com.sms.controller.student;

import com.sms.entity.Student;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Controller;

@Controller
public class StudentEditInfoController {

    @FXML private Label studentIdLabel;
    @FXML private Label nameLabel;
    @FXML private Label genderLabel;
    @FXML private TextField classNameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;

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
            classNameField.setText(currentStudent.getClassName());
            phoneField.setText(currentStudent.getPhone() != null ? currentStudent.getPhone() : "");
            emailField.setText(currentStudent.getEmail() != null ? currentStudent.getEmail() : "");
        }
    }

    public Student getUpdatedStudent() {
        if (currentStudent != null) {
            currentStudent.setClassName(classNameField.getText().trim());
            currentStudent.setPhone(phoneField.getText().trim());
            currentStudent.setEmail(emailField.getText().trim());
        }
        return currentStudent;
    }

    public boolean validateInput() {
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String className = classNameField.getText().trim();

        if (className.isEmpty()) {
            return false;
        }

        // 简单的手机号验证
        if (!phone.isEmpty() && !phone.matches("\\d{11}")) {
            return false;
        }

        // 简单的邮箱验证
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return false;
        }

        return true;
    }
}