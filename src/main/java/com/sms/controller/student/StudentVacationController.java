package com.sms.controller.student;

import com.sms.entity.Student;
import com.sms.entity.Vacation;
import com.sms.entity.Course;
import com.sms.service.VacationService;
import com.sms.service.CourseService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class StudentVacationController {

    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private ComboBox<String> vacationTypeComboBox;
    @FXML
    private ComboBox<Course> courseComboBox;
    @FXML
    private TextArea reasonTextArea;
    @FXML
    private TableView<Vacation> vacationTable;

    @Autowired
    private VacationService vacationService;

    @Autowired
    private CourseService courseService;

    private Student currentStudent;
    private ObservableList<Vacation> vacationData = FXCollections.observableArrayList();

    public void setCurrentStudent(Student student) {
        this.currentStudent = student;
        javafx.application.Platform.runLater(() -> {
            initializeData();
        });
    }

    @FXML
    public void initialize() {
        initializeComboBox();
        setupDatePickers();
        initializeTable();
    }

    private void initializeComboBox() {
        ObservableList<String> types = FXCollections.observableArrayList(
                "事假", "病假", "公假", "其他"
        );

        vacationTypeComboBox.setItems(types);

        if (!types.isEmpty())
            vacationTypeComboBox.getSelectionModel().select(0);

    }

    private void setupDatePickers() {
        startDatePicker.setValue(java.time.LocalDate.now());
        endDatePicker.setValue(java.time.LocalDate.now().plusDays(1));
    }

    private void initializeTable() {
        // 清除原有列
        vacationTable.getColumns().clear();

        // 创建表格列
        TableColumn<Vacation, String> startDateCol = new TableColumn<>("开始日期");
        startDateCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getStartDate() != null)
                return new SimpleStringProperty(
                        cellData.getValue().getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                );

            else
                return new SimpleStringProperty("");

        });

        TableColumn<Vacation, String> endDateCol = new TableColumn<>("结束日期");
        endDateCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getEndDate() != null)
                return new SimpleStringProperty(
                        cellData.getValue().getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                );

            else
                return new SimpleStringProperty("");

        });

        TableColumn<Vacation, String> typeCol = new TableColumn<>("请假类型");
        typeCol.setCellValueFactory(cellData -> {
            String type = cellData.getValue().getType();
            return new SimpleStringProperty(type != null ? type : "未指定");
        });

        TableColumn<Vacation, String> courseCol = new TableColumn<>("课程");
        courseCol.setCellValueFactory(cellData -> {
            Course course = cellData.getValue().getCourse();
            // 由于使用了 JOIN FETCH，course 应该已经被加载
            if (course != null)
                return new SimpleStringProperty(course.getName());

            else
                return new SimpleStringProperty("未指定");

        });

        TableColumn<Vacation, String> reasonCol = new TableColumn<>("原因");
        reasonCol.setCellValueFactory(new PropertyValueFactory<>("reason"));

        TableColumn<Vacation, String> statusCol = new TableColumn<>("状态");
        statusCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getStatus() != null)
                return new SimpleStringProperty(getStatusDisplayName(cellData.getValue().getStatus().toString()));

            else
                return new SimpleStringProperty("未知");

        });

        TableColumn<Vacation, String> approvedByCol = new TableColumn<>("审批人");
        approvedByCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getApprovedBy() != null)
                return new SimpleStringProperty(cellData.getValue().getApprovedBy().getName());

            else
                return new SimpleStringProperty("待审批");

        });

        // 设置列宽
        startDateCol.setPrefWidth(100);
        endDateCol.setPrefWidth(100);
        typeCol.setPrefWidth(80);
        courseCol.setPrefWidth(120);
        reasonCol.setPrefWidth(200);
        statusCol.setPrefWidth(80);
        approvedByCol.setPrefWidth(100);

        vacationTable.getColumns().addAll(startDateCol, endDateCol, typeCol, courseCol, reasonCol, statusCol, approvedByCol);
        vacationTable.setItems(vacationData);
    }

    private String getStatusDisplayName(String status) {
        switch (status) {
            case "PENDING":
                return "待审批";
            case "APPROVED":
                return "已批准";
            case "REJECTED":
                return "已拒绝";
            default:
                return status;
        }
    }

    private void initializeData() {
        loadCourses();
        loadVacationHistory();
    }

    private void loadCourses() {
        if (courseComboBox == null) {
            System.err.println("courseComboBox is null in loadCourses!");
            return;
        }

        if (currentStudent != null)
            try {
                List<Course> courses = courseService.findByStudentId(currentStudent.getId());
                ObservableList<Course> courseList = FXCollections.observableArrayList(courses);
                courseComboBox.setItems(courseList);

                if (!courseList.isEmpty())
                    courseComboBox.getSelectionModel().select(0);

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


                System.out.println("Loaded " + courses.size() + " courses for student");

            } catch (Exception e) {
                System.err.println("Error loading courses: " + e.getMessage());
                e.printStackTrace();
            }

        else
            System.err.println("currentStudent is null in loadCourses!");

    }

    @FXML
    private void handleSubmit() {
        if (validateInput())
            submitVacationApplication();

    }

    private boolean validateInput() {
        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            showAlert("错误", "请选择请假日期");
            return false;
        }

        if (startDatePicker.getValue().isAfter(endDatePicker.getValue())) {
            showAlert("错误", "开始日期不能晚于结束日期");
            return false;
        }

        if (courseComboBox.getSelectionModel().isEmpty()) {
            showAlert("错误", "请选择课程");
            return false;
        }

        if (reasonTextArea.getText().trim().isEmpty()) {
            showAlert("错误", "请输入请假原因");
            return false;
        }

        return true;
    }

    private void submitVacationApplication() {
        try {
            Vacation vacation = new Vacation();
            vacation.setStartDate(startDatePicker.getValue());
            vacation.setEndDate(endDatePicker.getValue());
            vacation.setType(vacationTypeComboBox.getValue());
            vacation.setReason(reasonTextArea.getText().trim());
            vacation.setStudent(currentStudent);
            vacation.setCourse(courseComboBox.getValue());

            showAlert("成功", "请假申请提交成功");
            resetForm();
            loadVacationHistory();

        } catch (Exception e) {
            showAlert("错误", "提交失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void resetForm() {
        startDatePicker.setValue(java.time.LocalDate.now());
        endDatePicker.setValue(java.time.LocalDate.now().plusDays(1));
        reasonTextArea.clear();
        if (courseComboBox != null)
            courseComboBox.getSelectionModel().clearSelection();

        loadCourses();
    }

    private void loadVacationHistory() {
        if (currentStudent != null) {
            try {
                List<Vacation> vacations = vacationService.findByStudentId(currentStudent.getId());
                vacationData.setAll(vacations);
                System.out.println("Loaded " + vacations.size() + " vacation records");

            } catch (Exception e) {
                System.err.println("Error loading vacation history: " + e.getMessage());
                e.printStackTrace();
            }
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