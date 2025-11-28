package com.sms.controller.student;

import com.sms.entity.Student;
import com.sms.entity.Course;
import com.sms.service.CourseService;
import com.sms.service.StudentService;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class StudentCourseSelectionController {

    @FXML private TextField searchField;
    @FXML private TableView<Course> courseTable;
    @FXML private TableView<Course> selectedCourseTable;

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentService studentService;

    private Student currentStudent;

    public void setCurrentStudent(Student student) {
        this.currentStudent = student;
        loadAvailableCourses();
        loadSelectedCourses();
    }

    @FXML
    public void initialize() {
        initializeTables();
    }

    private void initializeTables() {
        // 初始化可选课程表格
        TableColumn<Course, String> codeCol = new TableColumn<>("课程代码");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));

        TableColumn<Course, String> nameCol = new TableColumn<>("课程名称");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Course, String> teacherCol = new TableColumn<>("授课教师");
        teacherCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getTeacher() != null) {
                return new SimpleStringProperty(cellData.getValue().getTeacher().getName());
            } else {
                return new SimpleStringProperty("未分配");
            }
        });

        TableColumn<Course, Integer> creditCol = new TableColumn<>("学分");
        creditCol.setCellValueFactory(new PropertyValueFactory<>("credit"));

        TableColumn<Course, String> capacityCol = new TableColumn<>("容量");
        capacityCol.setCellValueFactory(cellData -> {
            Course course = cellData.getValue();
            return new SimpleStringProperty(course.getSelectedCount() + "/" + course.getCapacity());
        });

        TableColumn<Course, Void> actionCol = new TableColumn<>("操作");
        actionCol.setCellFactory(new Callback<TableColumn<Course, Void>, TableCell<Course, Void>>() {
            @Override
            public TableCell<Course, Void> call(TableColumn<Course, Void> param) {
                return new TableCell<Course, Void>() {
                    private final Button selectButton = new Button("选课");

                    {
                        selectButton.setOnAction(event -> {
                            Course course = getTableView().getItems().get(getIndex());
                            handleSelectCourse(course);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(selectButton);
                        }
                    }
                };
            }
        });

        courseTable.getColumns().setAll(codeCol, nameCol, teacherCol, creditCol, capacityCol, actionCol);

        // 初始化已选课程表格
        TableColumn<Course, String> selectedCodeCol = new TableColumn<>("课程代码");
        selectedCodeCol.setCellValueFactory(new PropertyValueFactory<>("code"));

        TableColumn<Course, String> selectedNameCol = new TableColumn<>("课程名称");
        selectedNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Course, String> selectedTeacherCol = new TableColumn<>("授课教师");
        selectedTeacherCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getTeacher() != null) {
                return new SimpleStringProperty(cellData.getValue().getTeacher().getName());
            } else {
                return new SimpleStringProperty("未分配");
            }
        });

        TableColumn<Course, Integer> selectedCreditCol = new TableColumn<>("学分");
        selectedCreditCol.setCellValueFactory(new PropertyValueFactory<>("credit"));

        TableColumn<Course, Void> selectedActionCol = new TableColumn<>("操作");
        selectedActionCol.setCellFactory(new Callback<TableColumn<Course, Void>, TableCell<Course, Void>>() {
            @Override
            public TableCell<Course, Void> call(TableColumn<Course, Void> param) {
                return new TableCell<Course, Void>() {
                    private final Button dropButton = new Button("退课");

                    {
                        dropButton.setOnAction(event -> {
                            Course course = getTableView().getItems().get(getIndex());
                            handleDropCourse(course);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(dropButton);
                        }
                    }
                };
            }
        });

        selectedCourseTable.getColumns().setAll(selectedCodeCol, selectedNameCol, selectedTeacherCol, selectedCreditCol, selectedActionCol);
    }

    private void loadAvailableCourses() {
        if (currentStudent != null) {
            try {
                // 加载所有课程，但排除已选课程
                List<Course> allCourses = courseService.findAll();
                List<Course> selectedCourses = courseService.findByStudentId(currentStudent.getId());
                List<Course> availableCourses = allCourses.stream()
                        .filter(course -> {
                            // 使用 ID 进行比较，确保正确过滤
                            return selectedCourses.stream()
                                    .noneMatch(selected -> selected.getId().equals(course.getId()));
                        })
                        .collect(Collectors.toList());
                courseTable.getItems().setAll(availableCourses);
                System.out.println("加载了 " + availableCourses.size() + " 门可选课程");
            } catch (Exception e) {
                System.err.println("加载可选课程失败: " + e.getMessage());
                e.printStackTrace();
                showAlert("错误", "加载可选课程失败: " + e.getMessage());
            }
        }
    }

    private void loadSelectedCourses() {
        if (currentStudent != null) {
            try {
                List<Course> selectedCourses = courseService.findByStudentId(currentStudent.getId());
                selectedCourseTable.getItems().setAll(selectedCourses);
                System.out.println("加载了 " + selectedCourses.size() + " 门已选课程");
            } catch (Exception e) {
                System.err.println("加载已选课程失败: " + e.getMessage());
                e.printStackTrace();
                showAlert("错误", "加载已选课程失败: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        System.out.println("搜索关键词: " + keyword);

        if (!keyword.isEmpty()) {
            try {
                // 使用新的搜索方法，同时搜索名称和代码
                List<Course> courses = courseService.findByNameOrCodeContaining(keyword);
                System.out.println("搜索到 " + courses.size() + " 门课程");

                // 过滤已选课程
                List<Course> selectedCourses = courseService.findByStudentId(currentStudent.getId());
                List<Course> availableCourses = courses.stream()
                        .filter(course -> {
                            return selectedCourses.stream()
                                    .noneMatch(selected -> selected.getId().equals(course.getId()));
                        })
                        .collect(Collectors.toList());

                courseTable.getItems().setAll(availableCourses);
                System.out.println("过滤后剩余 " + availableCourses.size() + " 门可选课程");

                // 如果没有搜索结果，显示提示
                if (availableCourses.isEmpty()) {
                    showAlert("提示", "没有找到匹配的课程");
                }
            } catch (Exception e) {
                System.err.println("搜索课程失败: " + e.getMessage());
                e.printStackTrace();
                showAlert("错误", "搜索课程失败: " + e.getMessage());
            }
        } else {
            loadAvailableCourses();
        }
    }

    @FXML
    private void handleReset() {
        searchField.clear();
        loadAvailableCourses();
    }

    private void handleSelectCourse(Course course) {
        try {
            // 检查课程容量
            if (course.getSelectedCount() >= course.getCapacity()) {
                showAlert("错误", "该课程已满，无法选课");
                return;
            }

            courseService.addStudentToCourse(course.getId(), currentStudent.getId());
            showAlert("成功", "选课成功");
            loadAvailableCourses();
            loadSelectedCourses();
        } catch (Exception e) {
            showAlert("错误", "选课失败: " + e.getMessage());
        }
    }

    private void handleDropCourse(Course course) {
        try {
            courseService.removeStudentFromCourse(course.getId(), currentStudent.getId());
            showAlert("成功", "退课成功");
            loadAvailableCourses();
            loadSelectedCourses();
        } catch (Exception e) {
            showAlert("错误", "退课失败: " + e.getMessage());
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