package com.sms.controller.teacher;

import com.sms.entity.Grade;
import com.sms.entity.Teacher;
import com.sms.entity.Course;
import com.sms.entity.Student;
import com.sms.service.GradeService;
import com.sms.service.CourseService;
import com.sms.service.StudentService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class TeacherGradeManagementController {

    @FXML
    private ComboBox<String> semesterComboBox;
    @FXML
    private ComboBox<String> classComboBox;
    @FXML
    private ComboBox<Course> courseComboBox;
    @FXML
    private TableView<Grade> gradeTable;

    @Autowired
    private GradeService gradeService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentService studentService;

    private Teacher currentTeacher;
    private ObservableList<Grade> gradeData = FXCollections.observableArrayList();

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
        semesterComboBox.getItems().addAll("全部学期", "2023-2024第一学期", "2023-2024第二学期");
        semesterComboBox.getSelectionModel().select(0);

        classComboBox.getItems().addAll("全部班级", "计算机1班", "计算机2班", "软件工程1班");
        classComboBox.getSelectionModel().select(0);
    }

    private void initializeTable() {
        gradeTable.getColumns().clear();

        TableColumn<Grade, String> studentIdCol = new TableColumn<>("学号");
        studentIdCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudent().getStudentId()));

        TableColumn<Grade, String> studentNameCol = new TableColumn<>("姓名");
        studentNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudent().getName()));

        TableColumn<Grade, String> classNameCol = new TableColumn<>("班级");
        classNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudent().getClassName()));

        TableColumn<Grade, Double> usualScoreCol = new TableColumn<>("平时成绩");
        usualScoreCol.setCellValueFactory(new PropertyValueFactory<>("usualScore"));
        usualScoreCol.setCellFactory(column -> new TableCell<Grade, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.1f", item));
                }
            }
        });

        TableColumn<Grade, Double> examScoreCol = new TableColumn<>("期末成绩");
        examScoreCol.setCellValueFactory(new PropertyValueFactory<>("examScore"));
        examScoreCol.setCellFactory(column -> new TableCell<Grade, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.1f", item));
                }
            }
        });

        TableColumn<Grade, Double> totalScoreCol = new TableColumn<>("总成绩");
        totalScoreCol.setCellValueFactory(new PropertyValueFactory<>("totalScore"));
        totalScoreCol.setCellFactory(column -> new TableCell<Grade, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.1f", item));
                }
            }
        });

        TableColumn<Grade, String> gradeLevelCol = new TableColumn<>("等级");
        gradeLevelCol.setCellValueFactory(new PropertyValueFactory<>("gradeLevel"));

        TableColumn<Grade, Void> actionCol = new TableColumn<>("操作");
        actionCol.setCellFactory(param -> new TableCell<Grade, Void>() {
            private final Button editButton = new Button("编辑");

            {
                editButton.setOnAction(event -> {
                    Grade grade = getTableView().getItems().get(getIndex());
                    handleEditGrade(grade);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });

        gradeTable.getColumns().addAll(studentIdCol, studentNameCol, classNameCol,
                usualScoreCol, examScoreCol, totalScoreCol, gradeLevelCol, actionCol);
        gradeTable.setItems(gradeData);
    }

    private void initializeData() {
        loadCourses();
        loadGradeData();
    }

    private void loadCourses() {
        if (currentTeacher != null) {
            try {
                List<Course> courses = courseService.findByTeacherId(currentTeacher.getId());
                courseComboBox.getItems().clear();
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
                            setText(course.getName()); // 假设Course类有一个getName()方法

                    }
                });

                if (!courses.isEmpty())
                    courseComboBox.getSelectionModel().select(0);

            } catch (Exception e) {
                System.err.println("加载课程失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleQuery() {
        loadGradeData();
    }

    @FXML
    private void handleImport() {
        // 导入成绩功能
        System.out.println("导入成绩功能待实现");
    }

    @FXML
    private void handleExport() {
        // 导出成绩功能
        System.out.println("导出成绩功能待实现");
    }

    private void loadGradeData() {
        try {
            String selectedSemester = semesterComboBox.getValue();
            String selectedClass = classComboBox.getValue();
            Course selectedCourse = courseComboBox.getValue();

            List<Grade> grades;

            if (selectedCourse != null) {
                grades = gradeService.findByCourseId(selectedCourse.getId());
            } else {
                grades = gradeService.findByTeacherId(currentTeacher.getId());
            }

            // 学期过滤
            if (selectedSemester != null && !selectedSemester.equals("全部学期")) {
                grades = grades.stream()
                        .filter(g -> selectedSemester.equals(g.getSemester()))
                        .collect(Collectors.toList());
            }

            // 班级过滤
            if (selectedClass != null && !selectedClass.equals("全部班级")) {
                grades = grades.stream()
                        .filter(g -> selectedClass.equals(g.getStudent().getClassName()))
                        .collect(Collectors.toList());
            }

            gradeData.setAll(grades);
            System.out.println("成功加载 " + grades.size() + " 条成绩记录");

        } catch (Exception e) {
            System.err.println("加载成绩数据失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleEditGrade(Grade grade) {
        // 创建编辑对话框
        Dialog<Grade> dialog = new Dialog<>();
        dialog.setTitle("编辑成绩");
        dialog.setHeaderText("编辑学生成绩");

        // 创建表单
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField usualScoreField = new TextField();
        usualScoreField.setText(grade.getUsualScore() != null ? grade.getUsualScore().toString() : "");
        TextField examScoreField = new TextField();
        examScoreField.setText(grade.getExamScore() != null ? grade.getExamScore().toString() : "");

        grid.add(new Label("平时成绩:"), 0, 0);
        grid.add(usualScoreField, 1, 0);
        grid.add(new Label("期末成绩:"), 0, 1);
        grid.add(examScoreField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                try {
                    Double usualScore = usualScoreField.getText().isEmpty() ? null :
                            Double.parseDouble(usualScoreField.getText());
                    Double examScore = examScoreField.getText().isEmpty() ? null :
                            Double.parseDouble(examScoreField.getText());

                    grade.setUsualScore(usualScore);
                    grade.setExamScore(examScore);

                    return grade;
                } catch (NumberFormatException e) {
                    showAlert("错误", "请输入有效的数字");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedGrade -> {
            try {
                gradeService.save(updatedGrade);
                loadGradeData();
                showAlert("成功", "成绩更新成功");
            } catch (Exception e) {
                showAlert("错误", "更新成绩失败: " + e.getMessage());
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}