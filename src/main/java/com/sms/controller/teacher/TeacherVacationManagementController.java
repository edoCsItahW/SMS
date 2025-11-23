package com.sms.controller.teacher;

import com.sms.entity.Teacher;
import com.sms.entity.Vacation;
import com.sms.entity.Course;
import com.sms.enums.VacationStatus;
import com.sms.service.VacationService;
import com.sms.service.CourseService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class TeacherVacationManagementController {

    @FXML private ComboBox<VacationStatus> statusComboBox;
    @FXML private ComboBox<String> classComboBox;
    @FXML private ComboBox<Course> courseComboBox;
    @FXML private TextField nameField;
    @FXML private TableView<Vacation> vacationTable;

    @Autowired
    private VacationService vacationService;

    @Autowired
    private CourseService courseService;

    private Teacher currentTeacher;
    private ObservableList<Vacation> vacationData = FXCollections.observableArrayList();

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
        // 初始化状态选择
        statusComboBox.setItems(FXCollections.observableArrayList(VacationStatus.values()));
        statusComboBox.getSelectionModel().select(0);

        // 初始化班级选择
        classComboBox.getItems().addAll("全部班级", "计算机1班", "计算机2班", "软件工程1班");
        classComboBox.getSelectionModel().select(0);
    }

    private void initializeTable() {
        // 清除原有列
        vacationTable.getColumns().clear();

        // 初始化表格列
        TableColumn<Vacation, String> studentIdCol = new TableColumn<>("学号");
        studentIdCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getStudent() != null) {
                return new SimpleStringProperty(cellData.getValue().getStudent().getStudentId());
            } else {
                return new SimpleStringProperty("未知");
            }
        });

        TableColumn<Vacation, String> studentNameCol = new TableColumn<>("姓名");
        studentNameCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getStudent() != null) {
                return new SimpleStringProperty(cellData.getValue().getStudent().getName());
            } else {
                return new SimpleStringProperty("未知");
            }
        });

        TableColumn<Vacation, String> classNameCol = new TableColumn<>("班级");
        classNameCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getStudent() != null) {
                return new SimpleStringProperty(cellData.getValue().getStudent().getClassName());
            } else {
                return new SimpleStringProperty("未知");
            }
        });

        TableColumn<Vacation, String> courseNameCol = new TableColumn<>("课程");
        courseNameCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCourse() != null) {
                return new SimpleStringProperty(cellData.getValue().getCourse().getName());
            } else {
                return new SimpleStringProperty("未指定");
            }
        });

        TableColumn<Vacation, String> startDateCol = new TableColumn<>("开始日期");
        startDateCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getStartDate() != null) {
                return new SimpleStringProperty(
                    cellData.getValue().getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                );
            } else {
                return new SimpleStringProperty("");
            }
        });

        TableColumn<Vacation, String> endDateCol = new TableColumn<>("结束日期");
        endDateCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getEndDate() != null) {
                return new SimpleStringProperty(
                    cellData.getValue().getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                );
            } else {
                return new SimpleStringProperty("");
            }
        });

        TableColumn<Vacation, String> reasonCol = new TableColumn<>("请假原因");
        reasonCol.setCellValueFactory(new PropertyValueFactory<>("reason"));

        TableColumn<Vacation, String> statusCol = new TableColumn<>("状态");
        statusCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getStatus() != null) {
                return new SimpleStringProperty(getStatusDisplayName(cellData.getValue().getStatus().toString()));
            } else {
                return new SimpleStringProperty("未知");
            }
        });

        TableColumn<Vacation, Void> actionCol = new TableColumn<>("操作");
        actionCol.setCellFactory(new Callback<TableColumn<Vacation, Void>, TableCell<Vacation, Void>>() {
            @Override
            public TableCell<Vacation, Void> call(TableColumn<Vacation, Void> param) {
                return new TableCell<Vacation, Void>() {
                    private final Button approveButton = new Button("批准");
                    private final Button rejectButton = new Button("拒绝");

                    {
                        approveButton.setOnAction(event -> {
                            Vacation vacation = getTableView().getItems().get(getIndex());
                            handleApprove(vacation);
                        });

                        rejectButton.setOnAction(event -> {
                            Vacation vacation = getTableView().getItems().get(getIndex());
                            handleReject(vacation);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Vacation vacation = getTableView().getItems().get(getIndex());
                            if (vacation.getStatus() == VacationStatus.PENDING) {
                                setGraphic(new javafx.scene.layout.HBox(5, approveButton, rejectButton));
                            } else {
                                setGraphic(null);
                            }
                        }
                    }
                };
            }
        });

        // 设置列宽
        studentIdCol.setPrefWidth(100);
        studentNameCol.setPrefWidth(100);
        classNameCol.setPrefWidth(120);
        courseNameCol.setPrefWidth(150);
        startDateCol.setPrefWidth(100);
        endDateCol.setPrefWidth(100);
        reasonCol.setPrefWidth(200);
        statusCol.setPrefWidth(80);
        actionCol.setPrefWidth(150);

        vacationTable.getColumns().addAll(studentIdCol, studentNameCol, classNameCol, courseNameCol,
                                         startDateCol, endDateCol, reasonCol, statusCol, actionCol);
        vacationTable.setItems(vacationData);
    }

    private String getStatusDisplayName(String status) {
        switch (status) {
            case "PENDING": return "待审批";
            case "APPROVED": return "已批准";
            case "REJECTED": return "已拒绝";
            default: return status;
        }
    }

    private void initializeData() {
        loadCourses();
        loadVacationData();
    }

    private void loadCourses() {
        // 初始化课程选择：加载教师所教课程
        if (currentTeacher != null) {
            try {
                List<Course> courses = courseService.findByTeacherId(currentTeacher.getId());
                courseComboBox.getItems().clear();
                courseComboBox.getItems().add(null); // 添加一个空选项表示全部课程
                courseComboBox.getItems().addAll(courses);
                courseComboBox.getSelectionModel().select(0);
                System.out.println("加载了 " + courses.size() + " 门课程");
            } catch (Exception e) {
                System.err.println("加载课程失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleQuery() {
        loadVacationData();
    }

    private void loadVacationData() {
        try {
            // 根据课程过滤请假数据
            Course selectedCourse = courseComboBox.getSelectionModel().getSelectedItem();
            VacationStatus selectedStatus = statusComboBox.getSelectionModel().getSelectedItem();
            String className = classComboBox.getValue();
            String studentName = nameField.getText().trim();

            List<Vacation> vacations;

            if (selectedCourse != null) {
                // 查询特定课程的请假
                vacations = vacationService.findByCourseId(selectedCourse.getId());
            } else {
                // 查询教师所教所有课程的请假
                List<Course> teacherCourses = courseService.findByTeacherId(currentTeacher.getId());
                vacations = teacherCourses.stream()
                        .flatMap(course -> vacationService.findByCourseId(course.getId()).stream())
                        .collect(Collectors.toList());
            }

            // 根据状态过滤
            if (selectedStatus != null) {
                vacations = vacations.stream()
                        .filter(v -> v.getStatus() == selectedStatus)
                        .collect(Collectors.toList());
            }

            // 根据班级过滤
            if (className != null && !className.equals("全部班级")) {
                vacations = vacations.stream()
                        .filter(v -> v.getStudent() != null && className.equals(v.getStudent().getClassName()))
                        .collect(Collectors.toList());
            }

            // 根据学生姓名过滤
            if (!studentName.isEmpty()) {
                vacations = vacations.stream()
                        .filter(v -> v.getStudent() != null && v.getStudent().getName().contains(studentName))
                        .collect(Collectors.toList());
            }

            vacationData.setAll(vacations);
            System.out.println("加载了 " + vacations.size() + " 条请假记录");

        } catch (Exception e) {
            System.err.println("加载请假数据失败: " + e.getMessage());
            e.printStackTrace();
            showAlert("错误", "加载请假数据失败: " + e.getMessage());
        }
    }

    private void handleApprove(Vacation vacation) {
        try {
            vacationService.approveVacation(vacation.getId(), currentTeacher.getId());
            showAlert("成功", "已批准请假申请");
            loadVacationData();
        } catch (Exception e) {
            showAlert("错误", "批准失败: " + e.getMessage());
        }
    }

    private void handleReject(Vacation vacation) {
        try {
            vacationService.rejectVacation(vacation.getId());
            showAlert("成功", "已拒绝请假申请");
            loadVacationData();
        } catch (Exception e) {
            showAlert("错误", "拒绝失败: " + e.getMessage());
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