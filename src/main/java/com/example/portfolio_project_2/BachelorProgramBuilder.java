package com.example.portfolio_project_2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class BachelorProgramBuilder extends Application {

    private static final String DATABASE_URL = "jdbc:sqlite:identifier.sqlite";
    private static final String PROGRAM_QUERY = "SELECT program_id, name FROM BachelorProgram";
    private static final String ACTIVITY_QUERY = "SELECT type_id, ects FROM StudyActivity WHERE activity_id = ";
    private static final String ACTIVITY_INFO_QUERY = "SELECT activity_id, name FROM StudyActivity WHERE program_id = ";

    private ComboBox<String> bachelorProgramBox;
    private ComboBox<String> studyActivityBox;
    private Button addButton;
    private TextArea activityAdded;
    private Map<String, Integer> programsMap;
    private Map<String, Integer> activitiesMap;

    @Override
    public void start(Stage stage) {
        setupStage(stage);
        stage.show();
    }

    // Handle GUI setup
    private void setupStage(Stage stage) {
        bachelorProgramBox = new ComboBox<>();
        studyActivityBox = new ComboBox<>();
        activityAdded = new TextArea();
        addButton = new Button("Add");

        // Fetch base programs
        programsMap = fetchBachelorPrograms(PROGRAM_QUERY);
        bachelorProgramBox.getItems().addAll(programsMap.keySet());

        // Listener to handle program selection
        bachelorProgramBox.valueProperty().addListener((obs, oldProgram, newProgram) -> {
            updateStudyActivityBox(newProgram);
        });

        // Set the Add button action
        addButton.setOnAction(event -> {
            String studyBoxValue = studyActivityBox.getValue();
            Integer activityId = activitiesMap.get(studyBoxValue);
            Integer programId = programsMap.get(bachelorProgramBox.getValue());
            addActivityToDatabase(programId, activityId);
            activityAdded.appendText(studyBoxValue + "\n");
        });

        // GUI layout setup
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        gridPane.addRow(0, bachelorProgramBox);
        gridPane.addRow(1, studyActivityBox);
        gridPane.addRow(2, addButton);
        gridPane.addRow(3, activityAdded);

        Scene scene = new Scene(gridPane, 500, 500);
        stage.setTitle("BachelorProgramBuilder");
        stage.setScene(scene);
    }

    // Update the study activities box
    private void updateStudyActivityBox(String programName) {
        studyActivityBox.getItems().clear();
        Integer programId = programsMap.get(programName);
        activitiesMap = fetchStudyActivities(ACTIVITY_INFO_QUERY + programId);
        List<String> studyActivities = new ArrayList<>(activitiesMap.keySet());
        studyActivityBox.getItems().addAll(studyActivities);
    }

    // Fetch activity type and ECTS credits
    private Map<String, Integer> fetchActivityInformation(Integer activityId) {
        Map<String, Integer> data = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(ACTIVITY_QUERY + activityId)) {
            while (rs.next()) {
                data.put("type_id", rs.getInt("type_id"));
                data.put("ects", rs.getInt("ects"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    // Add an activity to the database
    private void addActivityToDatabase(Integer programId, Integer activityId) {
        Map<String, Integer> activityInfo = fetchActivityInformation(activityId);
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO StudentProgram (program_id, activity_id, type_id, ects) VALUES (?, ?, ?, ?)")) {
            pstmt.setInt(1, programId);
            pstmt.setInt(2, activityId);
            pstmt.setInt(3, activityInfo.get("type_id"));
            pstmt.setInt(4, activityInfo.get("ects"));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Fetch bachelor program names and IDs
    private Map<String, Integer> fetchBachelorPrograms(String query) {
        Map<String, Integer> data = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                data.put(rs.getString("name"), rs.getInt("program_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    // Fetch study activities for a program
    private Map<String, Integer> fetchStudyActivities(String query) {
        Map<String, Integer> data = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                data.put(rs.getString("name"), rs.getInt("activity_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void main(String[] args) {
        launch(args);
    }
}