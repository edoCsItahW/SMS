package com.sms.controller.student;

import com.sms.entity.Grade;
import com.sms.entity.Student;
import com.sms.service.GradeService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class StudentGradesController {

    @FXML private ComboBox<String> semesterComboBox;
    @FXML private TableView<Grade> gradeTable;
    @FXML private Label totalCreditsLabel;
    @FXML private Label averageScoreLabel;
    @FXML private Label gpaLabel;

    @Autowired
    private GradeService gradeService;

    private Student currentStudent;
    private ObservableList<Grade> gradeData = FXCollections.observableArrayList();

    public void setCurrentStudent(Student student) {
        this.currentStudent = student;
        initializeData();
    }

    @FXML
    public void initialize() {
        initializeComboBox();
        initializeTable();
    }

    private void initializeComboBox() {
        ObservableList<String> semesters = FXCollections.observableArrayList(
            "全部学期",
            "2023-2024学年第一学期",
            "2023-2024学年第二学期",
            "2024-2025学年第一学期"
        );
        semesterComboBox.setItems(semesters);
        semesterComboBox.getSelectionModel().select(0);
    }

    private void initializeTable() {
        gradeTable.getColumns().clear();

        TableColumn<Grade, String> courseNameCol = new TableColumn<>("课程名称");
        courseNameCol.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getCourse().getName()));

        TableColumn<Grade, String> courseCodeCol = new TableColumn<>("课程代码");
        courseCodeCol.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getCourse().getCode()));

        TableColumn<Grade, Double> usualScoreCol = new TableColumn<>("平时成绩");
        usualScoreCol.setCellValueFactory(new PropertyValueFactory<>("usualScore"));

        TableColumn<Grade, Double> examScoreCol = new TableColumn<>("期末成绩");
        examScoreCol.setCellValueFactory(new PropertyValueFactory<>("examScore"));

        TableColumn<Grade, Double> totalScoreCol = new TableColumn<>("总成绩");
        totalScoreCol.setCellValueFactory(new PropertyValueFactory<>("totalScore"));

        TableColumn<Grade, String> gradeLevelCol = new TableColumn<>("等级");
        gradeLevelCol.setCellValueFactory(new PropertyValueFactory<>("gradeLevel"));

        TableColumn<Grade, String> semesterCol = new TableColumn<>("学期");
        semesterCol.setCellValueFactory(new PropertyValueFactory<>("semester"));

        gradeTable.getColumns().addAll(courseNameCol, courseCodeCol, usualScoreCol,
                                     examScoreCol, totalScoreCol, gradeLevelCol, semesterCol);
        gradeTable.setItems(gradeData);
    }

    private void initializeData() {
        loadGrades();
        calculateStatistics();
    }

    @FXML
    private void handleQuery() {
        loadGrades();
        calculateStatistics();
    }

    private void loadGrades() {
        if (currentStudent != null) {
            try {
                String selectedSemester = semesterComboBox.getValue();
                List<Grade> grades;

                if ("全部学期".equals(selectedSemester)) {
                    grades = gradeService.findByStudentId(currentStudent.getId());
                } else {
                    grades = gradeService.findByStudentIdAndSemester(currentStudent.getId(), selectedSemester);
                }

                gradeData.setAll(grades);
                System.out.println("成功加载 " + grades.size() + " 条成绩记录");
            } catch (Exception e) {
                System.err.println("加载成绩失败: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void calculateStatistics() {
        if (gradeData.isEmpty()) {
            totalCreditsLabel.setText("0");
            averageScoreLabel.setText("0");
            gpaLabel.setText("0");
            return;
        }

        double totalScore = 0;
        int totalCourses = gradeData.size();
        double totalCredits = totalCourses * 2.0; // 假设每门课2学分

        for (Grade grade : gradeData) {
            if (grade.getTotalScore() != null) {
                totalScore += grade.getTotalScore();
            }
        }

        double averageScore = totalScore / totalCourses;
        double gpa = calculateGPA(gradeData);

        totalCreditsLabel.setText(String.format("%.1f", totalCredits));
        averageScoreLabel.setText(String.format("%.1f", averageScore));
        gpaLabel.setText(String.format("%.2f", gpa));
    }

    private double calculateGPA(ObservableList<Grade> grades) {
        double totalPoints = 0;
        int totalCredits = 0;

        for (Grade grade : grades) {
            Double score = grade.getTotalScore();
            if (score != null) {
                double point = scoreToGPA(score);
                totalPoints += point * 2; // 每门课2学分
                totalCredits += 2;
            }
        }

        return totalCredits > 0 ? totalPoints / totalCredits : 0;
    }

    private double scoreToGPA(double score) {
        if (score >= 90) return 4.0;
        if (score >= 80) return 3.0;
        if (score >= 70) return 2.0;
        if (score >= 60) return 1.0;
        return 0.0;
    }
}