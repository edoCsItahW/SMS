package com.sms.controller.student;

import com.sms.entity.Course;
import com.sms.entity.Student;
import com.sms.service.CourseService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class StudentCourseScheduleController {

    @FXML private ComboBox<String> semesterComboBox;
    @FXML private ComboBox<Integer> weekComboBox;
    @FXML private TableView<Course> courseTable;

    @Autowired
    private CourseService courseService;

    private Student currentStudent;
    private ObservableList<Course> courseData = FXCollections.observableArrayList();

    public void setCurrentStudent(Student student) {
        this.currentStudent = student;
        initializeData();
    }

    @FXML
    public void initialize() {
        initializeComboBoxes();
        initializeTable();
    }

    private void initializeComboBoxes() {
        // 初始化学期选择
        ObservableList<String> semesters = FXCollections.observableArrayList(
            "2023-2024学年第一学期",
            "2023-2024学年第二学期",
            "2024-2025学年第一学期"
        );
        semesterComboBox.setItems(semesters);
        semesterComboBox.getSelectionModel().select(0);

        // 初始化周次选择
        ObservableList<Integer> weeks = FXCollections.observableArrayList();
        for (int i = 1; i <= 20; i++) {
            weeks.add(i);
        }
        weekComboBox.setItems(weeks);
        weekComboBox.getSelectionModel().select(0);
    }

    private void initializeTable() {
        // 清除原有列
        courseTable.getColumns().clear();

        // 创建表格列
        TableColumn<Course, String> nameCol = new TableColumn<>("课程名称");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Course, String> codeCol = new TableColumn<>("课程代码");
        codeCol.setCellValueFactory(new PropertyValueFactory<>("code"));

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

        TableColumn<Course, String> timeCol = new TableColumn<>("上课时间");
        timeCol.setCellValueFactory(cellData ->
            new SimpleStringProperty(getCourseTime(cellData.getValue())));

        TableColumn<Course, String> locationCol = new TableColumn<>("上课地点");
        locationCol.setCellValueFactory(cellData ->
            new SimpleStringProperty(getCourseLocation(cellData.getValue())));

        // 设置列宽
        nameCol.setPrefWidth(150);
        codeCol.setPrefWidth(100);
        teacherCol.setPrefWidth(100);
        creditCol.setPrefWidth(80);
        timeCol.setPrefWidth(200);
        locationCol.setPrefWidth(150);

        courseTable.getColumns().addAll(nameCol, codeCol, teacherCol, creditCol, timeCol, locationCol);
        courseTable.setItems(courseData);
    }

    private String getCourseTime(Course course) {
        return course.getClassTime() != null ? course.getClassTime() : "未安排";
    }

    private String getCourseLocation(Course course) {
        return course.getClassLocation() != null ? course.getClassLocation() : "未安排";
    }

    private void initializeData() {
        loadCourseSchedule();
    }

    @FXML
    private void handleQuery() {
        loadCourseSchedule();
    }

    private void loadCourseSchedule() {
        if (currentStudent != null) {
            try {
                // 从数据库获取学生所选课程（包含教师信息）
                List<Course> courses = courseService.findByStudentId(currentStudent.getId());
                courseData.setAll(courses);

                System.out.println("成功加载 " + courses.size() + " 门课程");
            } catch (Exception e) {
                System.err.println("加载课程表失败: " + e.getMessage());
                e.printStackTrace();
            }
        } else
            System.err.println("当前学生信息为空");

    }
}