package com.sms.controller;

import com.sms.entity.Student;
import com.sms.entity.Teacher;
import com.sms.service.StudentService;
import com.sms.service.TeacherService;
import com.sms.utils.PasswordEncoderUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import com.sms.JavaFxApplication;

import java.io.IOException;

@Controller
public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button actionButton;
    @FXML private Label switchLabel;
    @FXML private Label titleLabel;
    @FXML private Label messageLabel;
    @FXML private RadioButton studentRadio;
    @FXML private RadioButton teacherRadio;

    private ToggleGroup roleToggleGroup;

    private final StudentService studentService;
    private final TeacherService teacherService;
    private final PasswordEncoderUtil passwordEncoder;

    @Autowired
    public LoginController(StudentService studentService, TeacherService teacherService,
                          PasswordEncoderUtil passwordEncoder) {
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.passwordEncoder = passwordEncoder;
    }

    private boolean isLoginMode = true;

    @FXML
    public void initialize() {
        // 创建ToggleGroup并设置给RadioButton
        roleToggleGroup = new ToggleGroup();
        studentRadio.setToggleGroup(roleToggleGroup);
        teacherRadio.setToggleGroup(roleToggleGroup);
        studentRadio.setSelected(true); // 默认选择学生

        updateUI();

        // 设置点击事件
        switchLabel.setOnMouseClicked(event -> switchMode());

        // 设置鼠标样式
        switchLabel.setStyle("-fx-cursor: hand; -fx-text-fill: blue;");

        // 监听身份选择变化
        roleToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateUIForRole();
            }
        });
    }

    private void switchMode() {
        isLoginMode = !isLoginMode;
        updateUI();
    }

    private void updateUI() {
        if (isLoginMode) {
            titleLabel.setText("登录");
            actionButton.setText("登录");
            switchLabel.setText("没有账号？立即注册");
            confirmPasswordField.setVisible(false);
        } else {
            titleLabel.setText("注册");
            actionButton.setText("注册");
            switchLabel.setText("已有账号？立即登录");
            confirmPasswordField.setVisible(true);
        }

        updateUIForRole();
        messageLabel.setText("");

        // 清空输入框
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
    }

    private void updateUIForRole() {
        if (!isLoginMode) {
            boolean isStudent = studentRadio.isSelected();
            if (isStudent) {
                titleLabel.setText("注册 - 学生");
            } else {
                titleLabel.setText("注册 - 教师");
            }
        } else {
            boolean isStudent = studentRadio.isSelected();
            if (isStudent) {
                titleLabel.setText("登录 - 学生");
            } else {
                titleLabel.setText("登录 - 教师");
            }
        }
    }

    @FXML
    private void handleLogin() {
        if (isLoginMode) {
            performLogin();
        } else {
            performRegister();
        }
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showMessage("请输入用户名和密码");
            return;
        }

        try {
            boolean isStudent = studentRadio.isSelected();

            if (isStudent) {
                // 学生登录
                var studentOpt = studentService.findByName(username);
                if (studentOpt.isPresent()) {
                    Student student = studentOpt.get();
                    if (passwordEncoder.matches(password, student.getPassword())) {
                        loginSuccess("student", student.getName(), student);
                        return;
                    } else {
                        showMessage("密码错误");
                        return;
                    }
                } else {
                    showMessage("学生用户不存在");
                    return;
                }
            } else {
                // 教师登录
                var teacherOpt = teacherService.findByName(username);
                if (teacherOpt.isPresent()) {
                    Teacher teacher = teacherOpt.get();
                    if (passwordEncoder.matches(password, teacher.getPassword())) {
                        loginSuccess("teacher", teacher.getName(), teacher);
                        return;
                    } else {
                        showMessage("密码错误");
                        return;
                    }
                } else {
                    showMessage("教师用户不存在");
                    return;
                }
            }

        } catch (Exception e) {
            showAlert("错误", "登录过程中发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void performRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showMessage("请填写所有字段");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showMessage("两次输入的密码不一致");
            return;
        }

        if (password.length() < 6) {
            showMessage("密码长度至少6位");
            return;
        }

        try {
            boolean isStudent = studentRadio.isSelected();

            // 检查用户是否已存在
            if (studentService.existsByName(username) || teacherService.existsByName(username)) {
                showMessage("用户名已存在");
                return;
            }

            if (isStudent) {
                // 注册为学生
                Student student = new Student();
                student.setName(username);
                // 使用密码加密
                student.setPassword(passwordEncoder.encode(password));
                student.setStudentId(generateStudentId());
                student.setClassName("默认班级");
                student.setGender(com.sms.enums.Gender.MALE);

                studentService.save(student);
                showMessage("学生注册成功，请登录");
            } else {
                // 注册为教师
                Teacher teacher = new Teacher();
                teacher.setName(username);
                // 使用密码加密
                teacher.setPassword(passwordEncoder.encode(password));
                teacher.setTeacherId(generateTeacherId());
                teacher.setDepartment("默认院系");
                teacher.setOfficeLocation("默认办公室");

                teacherService.save(teacher);
                showMessage("教师注册成功，请登录");
            }

            switchMode();

        } catch (Exception e) {
            showAlert("错误", "注册失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loginSuccess(String userType, String username, Object user) {
        try {
            FXMLLoader loader = new FXMLLoader();
            Parent root;
            Stage currentStage = (Stage) usernameField.getScene().getWindow();

            if ("student".equals(userType)) {
                loader.setLocation(getClass().getResource("/fxml/student.fxml"));
                loader.setControllerFactory(JavaFxApplication.getSpringContext()::getBean);
                root = loader.load();
                StudentController controller = loader.getController();
                controller.setCurrentStudent((Student) user);
                currentStage.setTitle("学生管理系统 - 学生界面");
            } else {
                loader.setLocation(getClass().getResource("/fxml/teacher.fxml"));
                loader.setControllerFactory(JavaFxApplication.getSpringContext()::getBean);
                root = loader.load();
                TeacherController controller = loader.getController();
                controller.setCurrentTeacher((Teacher) user);
                currentStage.setTitle("学生管理系统 - 教师界面");
            }

            currentStage.setScene(new Scene(root, 1200, 800));

        } catch (IOException e) {
            showAlert("错误", "加载界面失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String generateStudentId() {
        return "S" + (System.currentTimeMillis() % 100000);
    }

    private String generateTeacherId() {
        return "T" + (System.currentTimeMillis() % 100000);
    }

    private void showMessage(String message) {
        messageLabel.setText(message);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}