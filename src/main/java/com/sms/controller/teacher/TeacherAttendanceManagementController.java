package com.sms.controller.teacher;

import com.sms.entity.Attendance;
import com.sms.entity.Teacher;
import com.sms.entity.Course;
import com.sms.entity.Student;
import com.sms.enums.AttendanceStatus;
import com.sms.service.AttendanceService;
import com.sms.service.CourseService;
import com.sms.service.StudentService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class TeacherAttendanceManagementController {

    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<String> classComboBox;
    @FXML
    private ComboBox<Course> courseComboBox;
    @FXML
    private TableView<Attendance> attendanceTable;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private StudentService studentService;

    private Teacher currentTeacher;
    private ObservableList<Attendance> attendanceData = FXCollections.observableArrayList();

    public void setCurrentTeacher(Teacher teacher) {
        this.currentTeacher = teacher;
        initializeData();
    }

    @FXML
    public void initialize() {
        initializeComboBoxes();
        initializeTable();
        datePicker.setValue(LocalDate.now());
    }

    private void initializeComboBoxes() {
        classComboBox.getItems().addAll("全部班级", "计算机1班", "计算机2班", "软件工程1班");
        classComboBox.getSelectionModel().select(0);
    }

    private void initializeTable() {
        attendanceTable.getColumns().clear();

        TableColumn<Attendance, String> studentIdCol = new TableColumn<>("学号");
        studentIdCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudent().getStudentId()));

        TableColumn<Attendance, String> studentNameCol = new TableColumn<>("姓名");
        studentNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudent().getName()));

        TableColumn<Attendance, String> classNameCol = new TableColumn<>("班级");
        classNameCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudent().getClassName()));

        TableColumn<Attendance, AttendanceStatus> statusCol = new TableColumn<>("考勤状态");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Attendance, String> remarkCol = new TableColumn<>("备注");
        remarkCol.setCellValueFactory(new PropertyValueFactory<>("remark"));

        TableColumn<Attendance, Void> actionCol = new TableColumn<>("操作");
        actionCol.setCellFactory(param -> new TableCell<Attendance, Void>() {
            private final ComboBox<AttendanceStatus> statusCombo = new ComboBox<>();

            {
                statusCombo.getItems().addAll(AttendanceStatus.values());
                statusCombo.setOnAction(event -> {
                    Attendance attendance = getTableView().getItems().get(getIndex());
                    attendance.setStatus(statusCombo.getValue());
                    handleUpdateAttendance(attendance);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Attendance attendance = getTableView().getItems().get(getIndex());
                    statusCombo.setValue(attendance.getStatus());
                    setGraphic(statusCombo);
                }
            }
        });

        attendanceTable.getColumns().addAll(studentIdCol, studentNameCol, classNameCol, statusCol, remarkCol, actionCol);
        attendanceTable.setItems(attendanceData);
    }

    private void initializeData() {
        loadCourses();
        loadAttendanceData();
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
                            setText(course.getName());

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
        loadAttendanceData();
    }

    @FXML
    private void handleRecordAttendance() {
        Course selectedCourse = courseComboBox.getValue();
        LocalDate selectedDate = datePicker.getValue();

        if (selectedCourse == null || selectedDate == null) {
            showAlert("错误", "请选择课程和日期");
            return;
        }

        try {
            // 获取该课程的所有学生
            List<Student> students = studentService.findByCourseId(selectedCourse.getId());

            for (Student student : students) {
                Attendance attendance = new Attendance();
                attendance.setStudent(student);
                attendance.setCourse(selectedCourse);
                attendance.setAttendanceDate(selectedDate);
                attendance.setStatus(AttendanceStatus.PRESENT);
                attendance.setRecordedBy(currentTeacher);

                attendanceService.save(attendance);
            }

            showAlert("成功", "考勤记录初始化成功");
            loadAttendanceData();

        } catch (Exception e) {
            showAlert("错误", "记录考勤失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadAttendanceData() {
        try {
            Course selectedCourse = courseComboBox.getValue();
            LocalDate selectedDate = datePicker.getValue();
            String selectedClass = classComboBox.getValue();

            List<Attendance> attendances;

            if (selectedCourse != null && selectedDate != null) {
                attendances = attendanceService.findByCourseIdAndDate(selectedCourse.getId(), selectedDate);
            } else {
                attendances = attendanceService.findByTeacherId(currentTeacher.getId());
            }

            // 班级过滤
            if (selectedClass != null && !selectedClass.equals("全部班级")) {
                attendances = attendances.stream()
                        .filter(a -> selectedClass.equals(a.getStudent().getClassName()))
                        .collect(Collectors.toList());
            }

            attendanceData.setAll(attendances);
            System.out.println("成功加载 " + attendances.size() + " 条考勤记录");

        } catch (Exception e) {
            System.err.println("加载考勤数据失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleUpdateAttendance(Attendance attendance) {
        try {
            attendanceService.save(attendance);
            System.out.println("考勤记录更新成功");
        } catch (Exception e) {
            showAlert("错误", "更新考勤记录失败: " + e.getMessage());
            e.printStackTrace();
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