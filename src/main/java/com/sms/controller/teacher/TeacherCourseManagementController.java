package com.sms.controller.teacher;

import com.sms.entity.Course;
import com.sms.entity.Teacher;
import com.sms.entity.Student;
import com.sms.service.CourseService;
import com.sms.service.StudentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public class TeacherCourseManagementController {

    @FXML
    private TableView<Course> courseTable;
    @FXML
    private TableView<Student> studentTable;
    @FXML
    private TextField courseNameField;
    @FXML
    private TextField courseCodeField;
    @FXML
    private TextField creditField;
    @FXML
    private TextField capacityField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private ComboBox<String> semesterComboBox;
    @FXML
    private TextField classTimeField;
    @FXML
    private TextField classLocationField;

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentService studentService;

    private Teacher currentTeacher;
    private ObservableList<Course> courseData = FXCollections.observableArrayList();
    private ObservableList<Student> studentData = FXCollections.observableArrayList();

    public void setCurrentTeacher(Teacher teacher) {
        this.currentTeacher = teacher;
        initializeData();
    }

    @FXML
    public void initialize() {
        initializeComboBoxes();
        initializeTables();
    }

    private void initializeComboBoxes() {
        semesterComboBox.getItems().addAll(
                "2023-2024学年第一学期",
                "2023-2024学年第二学期",
                "2024-2025学年第一学期"
        );
        semesterComboBox.getSelectionModel().select(0);
    }

    private void initializeTables() {
        // 初始化课程表格
        initializeCourseTable();
        // 初始化学生表格
        initializeStudentTable();

        // 添加课程选择监听器
        courseTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadCourseStudents(newSelection);
            } else {
                studentData.clear();
            }
        });
    }

    private void initializeCourseTable() {
        courseTable.getColumns().clear();

        TableColumn<Course, String> codeCol = new TableColumn<>("课程代码");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));

        TableColumn<Course, String> nameCol = new TableColumn<>("课程名称");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Course, Integer> creditCol = new TableColumn<>("学分");
        creditCol.setCellValueFactory(new PropertyValueFactory<>("credit"));

        TableColumn<Course, Integer> capacityCol = new TableColumn<>("容量");
        capacityCol.setCellValueFactory(new PropertyValueFactory<>("capacity"));

        TableColumn<Course, Integer> selectedCol = new TableColumn<>("已选人数");
        selectedCol.setCellValueFactory(new PropertyValueFactory<>("selectedCount"));

        TableColumn<Course, String> semesterCol = new TableColumn<>("学期");
        semesterCol.setCellValueFactory(new PropertyValueFactory<>("semester"));

        TableColumn<Course, Void> actionCol = new TableColumn<>("操作");
        actionCol.setCellFactory(param -> new TableCell<Course, Void>() {
            private final Button editButton = new Button("编辑");
            private final Button deleteButton = new Button("删除");
            private final Button manageStudentsButton = new Button("管理学生");

            {
                editButton.setOnAction(event -> {
                    Course course = getTableView().getItems().get(getIndex());
                    handleEditCourse(course);
                });

                deleteButton.setOnAction(event -> {
                    Course course = getTableView().getItems().get(getIndex());
                    handleDeleteCourse(course);
                });

                manageStudentsButton.setOnAction(event -> {
                    Course course = getTableView().getItems().get(getIndex());
                    handleManageStudents(course);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new javafx.scene.layout.HBox(5, editButton, deleteButton, manageStudentsButton));
                }
            }
        });

        courseTable.getColumns().addAll(codeCol, nameCol, creditCol, capacityCol, selectedCol, semesterCol, actionCol);
        courseTable.setItems(courseData);
    }

    private void initializeStudentTable() {
        studentTable.getColumns().clear();

        TableColumn<Student, String> studentIdCol = new TableColumn<>("学号");
        studentIdCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));

        TableColumn<Student, String> nameCol = new TableColumn<>("姓名");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Student, String> classNameCol = new TableColumn<>("班级");
        classNameCol.setCellValueFactory(new PropertyValueFactory<>("className"));

        TableColumn<Student, String> genderCol = new TableColumn<>("性别");
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));

        TableColumn<Student, Void> actionCol = new TableColumn<>("操作");
        actionCol.setCellFactory(param -> new TableCell<Student, Void>() {
            private final Button removeButton = new Button("移除");

            {
                removeButton.setOnAction(event -> {
                    Student student = getTableView().getItems().get(getIndex());
                    Course course = courseTable.getSelectionModel().getSelectedItem();
                    if (course != null) {
                        handleRemoveStudentFromCourse(course, student);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(removeButton);
                }
            }
        });

        studentTable.getColumns().addAll(studentIdCol, nameCol, classNameCol, genderCol, actionCol);
        studentTable.setItems(studentData);
    }

    private void initializeData() {
        loadCourses();
    }

    @FXML
    private void handleCreateCourse() {
        if (validateCourseInput()) {
            createCourse();
        }
    }

    @FXML
    private void handleUpdateCourse() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null && validateCourseInput()) {
            updateCourse(selectedCourse);
        } else {
            showAlert("错误", "请选择要更新的课程");
        }
    }

    @FXML
    private void handleAddStudentToCourse() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse != null) {
            showAddStudentDialog(selectedCourse);
        } else {
            showAlert("错误", "请先选择课程");
        }
    }

    private boolean validateCourseInput() {
        String name = courseNameField.getText().trim();
        String code = courseCodeField.getText().trim();
        String creditText = creditField.getText().trim();
        String capacityText = capacityField.getText().trim();

        if (name.isEmpty() || code.isEmpty() || creditText.isEmpty() || capacityText.isEmpty()) {
            showAlert("错误", "请填写所有必填字段");
            return false;
        }

        try {
            int credit = Integer.parseInt(creditText);
            int capacity = Integer.parseInt(capacityText);

            if (credit <= 0 || capacity <= 0) {
                showAlert("错误", "学分和容量必须大于0");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("错误", "学分和容量必须为数字");
            return false;
        }

        return true;
    }

    private void createCourse() {
        try {
            Course course = new Course();
            course.setName(courseNameField.getText().trim());
            course.setCode(courseCodeField.getText().trim());
            course.setCredit(Integer.parseInt(creditField.getText().trim()));
            course.setCapacity(Integer.parseInt(capacityField.getText().trim()));
            course.setDescription(descriptionField.getText().trim());
            course.setSemester(semesterComboBox.getValue());
            course.setClassTime(classTimeField.getText().trim());
            course.setClassLocation(classLocationField.getText().trim());
            course.setTeacher(currentTeacher);

            courseService.save(course);
            showAlert("成功", "课程创建成功");
            clearForm();
            loadCourses();
        } catch (Exception e) {
            showAlert("错误", "创建课程失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateCourse(Course course) {
        try {
            course.setName(courseNameField.getText().trim());
            course.setCode(courseCodeField.getText().trim());
            course.setCredit(Integer.parseInt(creditField.getText().trim()));
            course.setCapacity(Integer.parseInt(capacityField.getText().trim()));
            course.setDescription(descriptionField.getText().trim());
            course.setSemester(semesterComboBox.getValue());
            course.setClassTime(classTimeField.getText().trim());
            course.setClassLocation(classLocationField.getText().trim());

            courseService.save(course);
            showAlert("成功", "课程更新成功");
            clearForm();
            loadCourses();
        } catch (Exception e) {
            showAlert("错误", "更新课程失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleEditCourse(Course course) {
        courseNameField.setText(course.getName());
        courseCodeField.setText(course.getCode());
        creditField.setText(String.valueOf(course.getCredit()));
        capacityField.setText(String.valueOf(course.getCapacity()));
        descriptionField.setText(course.getDescription());
        semesterComboBox.setValue(course.getSemester());
        classTimeField.setText(course.getClassTime());
        classLocationField.setText(course.getClassLocation());
    }

    private void handleDeleteCourse(Course course) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认删除");
        alert.setHeaderText("确定要删除课程 '" + course.getName() + "' 吗？");
        alert.setContentText("此操作不可撤销！");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                courseService.deleteById(course.getId());
                showAlert("成功", "课程删除成功");
                loadCourses();
            } catch (Exception e) {
                showAlert("错误", "删除课程失败: " + e.getMessage());
            }
        }
    }

    private void handleManageStudents(Course course) {
        // 确保课程被选中
        courseTable.getSelectionModel().select(course);
        loadCourseStudents(course);
    }

    private void loadCourseStudents(Course course) {
        try {
            // 使用包含学生信息的查询
            Optional<Course> courseWithStudents = courseService.findByIdWithStudents(course.getId());
            if (courseWithStudents.isPresent()) {
                Course loadedCourse = courseWithStudents.get();
                // 直接使用从数据库加载的学生集合
                studentData.setAll(loadedCourse.getStudents());
                System.out.println("加载了 " + loadedCourse.getStudents().size() + " 名学生");

                // 调试：打印学生信息
                for (Student student : loadedCourse.getStudents()) {
                    System.out.println("学生: " + student.getName() + " (ID: " + student.getId() + ")");
                }

                // 验证数据库中的关联
                verifyStudentCourseAssociation(course.getId());
            } else {
                System.err.println("未找到课程信息");
                studentData.clear();
            }
        } catch (Exception e) {
            System.err.println("加载课程学生失败: " + e.getMessage());
            e.printStackTrace();
            studentData.clear();
        }
    }

    // 添加验证方法
    private void verifyStudentCourseAssociation(Long courseId) {
        try {
            // 直接查询数据库中的关联关系
            List<Student> studentsInDb = studentService.findByCourseId(courseId);
            System.out.println("数据库验证 - 课程 " + courseId + " 的学生数: " + studentsInDb.size());

            for (Student student : studentsInDb) {
                System.out.println("数据库中的学生: " + student.getName() + " (ID: " + student.getId() + ")");
            }
        } catch (Exception e) {
            System.err.println("验证关联关系失败: " + e.getMessage());
        }
    }

    private void showAddStudentDialog(Course course) {
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle("添加学生到课程");
        dialog.setHeaderText("选择要添加到课程的学生");

        // 创建学生选择表格
        TableView<Student> studentSelectionTable = new TableView<>();
        ObservableList<Student> allStudents = FXCollections.observableArrayList(studentService.findAll());

        // 过滤掉已经在该课程中的学生
        List<Student> currentStudents = studentService.findByCourseId(course.getId());
        allStudents.removeAll(currentStudents);

        TableColumn<Student, String> studentIdCol = new TableColumn<>("学号");
        studentIdCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));

        TableColumn<Student, String> nameCol = new TableColumn<>("姓名");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Student, String> classNameCol = new TableColumn<>("班级");
        classNameCol.setCellValueFactory(new PropertyValueFactory<>("className"));

        studentSelectionTable.getColumns().addAll(studentIdCol, nameCol, classNameCol);
        studentSelectionTable.setItems(allStudents);
        studentSelectionTable.setPrefHeight(300);

        dialog.getDialogPane().setContent(studentSelectionTable);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return studentSelectionTable.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(student -> {
            handleAddStudentToCourse(course, student);
        });
    }

    private void handleAddStudentToCourse(Course course, Student student) {
        try {
            System.out.println("开始添加学生 " + student.getName() + " 到课程 " + course.getName());

            Course updatedCourse = courseService.addStudentToCourse(course.getId(), student.getId());

            // 验证关联是否成功
            if (updatedCourse != null) {
                // 重新加载课程的学生数据
                loadCourseStudents(updatedCourse);
                showAlert("成功", "学生 " + student.getName() + " 添加成功");

                // 调试信息
                System.out.println("添加后课程学生数: " + updatedCourse.getStudents().size());
            } else {
                showAlert("错误", "添加学生失败");
            }
        } catch (Exception e) {
            showAlert("错误", "添加学生失败: " + e.getMessage());
            System.err.println("添加学生失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleRemoveStudentFromCourse(Course course, Student student) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("确认移除");
        alert.setHeaderText("确定要将学生 '" + student.getName() + "' 从课程中移除吗？");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                courseService.removeStudentFromCourse(course.getId(), student.getId());
                showAlert("成功", "学生移除成功");
                loadCourseStudents(course);
            } catch (Exception e) {
                showAlert("错误", "移除学生失败: " + e.getMessage());
            }
        }
    }

    private void loadCourses() {
        if (currentTeacher != null) {
            try {
                List<Course> courses = courseService.findByTeacherId(currentTeacher.getId());
                courseData.setAll(courses);
                System.out.println("成功加载 " + courses.size() + " 门课程");
            } catch (Exception e) {
                System.err.println("加载课程失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void clearForm() {
        courseNameField.clear();
        courseCodeField.clear();
        creditField.clear();
        capacityField.clear();
        descriptionField.clear();
        classTimeField.clear();
        classLocationField.clear();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}