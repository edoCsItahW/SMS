package com.sms.controller.teacher;

import com.sms.entity.Student;
import com.sms.entity.Teacher;
import com.sms.entity.Course;
import com.sms.service.StudentService;
import com.sms.service.CourseService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class TeacherStudentManagementController {

    @FXML private ComboBox<String> classComboBox;
    @FXML private ComboBox<Course> courseComboBox;
    @FXML private TextField nameField;
    @FXML private TableView<Student> studentTable;

    @Autowired
    private StudentService studentService;

    @Autowired
    private CourseService courseService;

    private Teacher currentTeacher;
    private ObservableList<Student> studentData = FXCollections.observableArrayList();

    public void setCurrentTeacher(Teacher teacher) {
        this.currentTeacher = teacher;
        initializeData();
    }

    @FXML
    public void initialize() {
        initializeComboBoxes();
        initializeTable();
    }

    private void initializeComboBoxes() {
        // 初始化班级选择
        classComboBox.getItems().addAll("全部班级", "计算机1班", "计算机2班", "软件工程1班");
        classComboBox.getSelectionModel().select(0);
    }

    private void initializeTable() {
        studentTable.getColumns().clear();

        TableColumn<Student, String> studentIdCol = new TableColumn<>("学号");
        studentIdCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));

        TableColumn<Student, String> nameCol = new TableColumn<>("姓名");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Student, String> genderCol = new TableColumn<>("性别");
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));

        TableColumn<Student, String> classNameCol = new TableColumn<>("班级");
        classNameCol.setCellValueFactory(new PropertyValueFactory<>("className"));

        TableColumn<Student, String> phoneCol = new TableColumn<>("电话");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<Student, String> emailCol = new TableColumn<>("邮箱");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

        studentTable.getColumns().addAll(studentIdCol, nameCol, genderCol, classNameCol, phoneCol, emailCol);
        studentTable.setItems(studentData);
    }

    private void initializeData() {
        loadCourses();
        loadStudentData();
    }

    private void loadCourses() {
        if (currentTeacher != null) {
            try {
                List<Course> courses = courseService.findByTeacherId(currentTeacher.getId());
                courseComboBox.getItems().clear();
                courseComboBox.getItems().add(null); // 添加空选项表示全部课程
                courseComboBox.getItems().addAll(courses);

                courseComboBox.setCellFactory(_ -> new ListCell<>() {
                    @Override
                    protected void updateItem(Course course, boolean empty) {
                        super.updateItem(course, empty);
                        if (empty || course == null)
                            setText(null);

                        else
                            setText(course.getName());
                    }
                });

                courseComboBox.setButtonCell(new ListCell<>() {
                    @Override
                    protected void updateItem(Course course, boolean empty) {
                        super.updateItem(course, empty);
                        if (empty || course == null)
                            setText(null);

                        else
                            setText(course.getName());

                    }
                });

                courseComboBox.getSelectionModel().select(0);
            } catch (Exception e) {
                System.err.println("加载课程失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleQuery() {
        loadStudentData();
    }

    @FXML
    private void handleReset() {
        classComboBox.getSelectionModel().select(0);
        courseComboBox.getSelectionModel().select(0);
        nameField.clear();
        loadStudentData();
    }

    @FXML
    private void handleExport() {
        // 导出学生信息到Excel
        System.out.println("导出学生信息功能待实现");
    }

    private void loadStudentData() {
        try {
            String selectedClass = classComboBox.getValue();
            Course selectedCourse = courseComboBox.getValue();
            String name = nameField.getText().trim();

            List<Student> students;

            if (selectedCourse != null) {
                // 查询特定课程的学生
                students = studentService.findByCourseId(selectedCourse.getId());
            } else {
                // 查询所有学生
                students = studentService.findAll();
            }

            // 班级过滤
            if (selectedClass != null && !selectedClass.equals("全部班级")) {
                students = students.stream()
                        .filter(s -> selectedClass.equals(s.getClassName()))
                        .collect(Collectors.toList());
            }

            // 姓名过滤
            if (!name.isEmpty()) {
                students = students.stream()
                        .filter(s -> s.getName().contains(name))
                        .collect(Collectors.toList());
            }

            studentData.setAll(students);
            System.out.println("成功加载 " + students.size() + " 名学生");

        } catch (Exception e) {
            System.err.println("加载学生数据失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}