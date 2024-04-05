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

// Main class extending from JavaFX's Application class
public class BachelorProgramBuilder extends Application {
    // ComboBoxes for displaying bachelor programs and their associated study activities
    private ComboBox<String> bachelorProgramBox;
    private ComboBox<String> studyActivityBox;
    // Add Button
    private Button addButton;
    // Text Area
    private TextArea activityAdded;
    // Map to store program names and their associated IDs
    private Map<String, Integer> programsMap;
    private Map<String, Integer> activitiesMap;

    @Override
    public void start(Stage stage) {
        // Method to setup the stage
        setupStage(stage);
        stage.show();
    }

    // This method is responsible for setting up the stage - layout/GUI
    private void setupStage(Stage stage) {
        bachelorProgramBox = new ComboBox<>();
        studyActivityBox = new ComboBox<>();
        activityAdded = new TextArea();
        addButton = new Button("Add");

        // Fetch base programs from the database and populate the program map and bachelorProgramBox
        programsMap = fetchBachelorPrograms("SELECT program_id, name FROM BachelorProgram");
        bachelorProgramBox.getItems().addAll(programsMap.keySet());

        // Add an event listener to the bachelorProgramBox to react when a new program is selected
        // On selection, studyActivityBox is updated with the corresponding program's study activities
        bachelorProgramBox.valueProperty().addListener((obs, oldProgram, newProgram) -> {
            updateStudyActivityBox(newProgram);
        });

        addButton.setOnAction(event -> {
            Integer activityId = activitiesMap.get(studyActivityBox.getValue());
            Integer programId = programsMap.get(bachelorProgramBox.getValue());
            addActivityToDatabase(programId, activityId);
            activityAdded.appendText(studyActivityBox.getValue() + "\n");
        });

        // Set up GridPane
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10)); // Add some padding
        gridPane.setVgap(5); // Add vertical gap
        gridPane.setHgap(5); // Add horizontal gap

        // Add components to GridPane
        // Parameters are: Node, columnIndex, rowIndex, colSpan, rowSpan
        gridPane.addRow(0, bachelorProgramBox);
        gridPane.addRow(1, studyActivityBox);
        gridPane.addRow(2, addButton);
        gridPane.addRow(3, activityAdded);


        Scene scene = new Scene(gridPane, 500, 500);
        stage.setTitle("BachelorProgramBuilder");
        stage.setScene(scene);
    }

    /**
     * This method is called when a new bachelor program is selected.
     * It fetches and displays the selected program's associated study activities.
     */
    private void updateStudyActivityBox(String programName) {
        // Clear the previous study activities
        studyActivityBox.getItems().clear();
        // Fetch the ID for the selected program from the HashMap
        Integer programId = programsMap.get(programName);
        // Fetch study activities for the selected program from the database and update activitiesMap
        String sqlQuery = "SELECT activity_id, name FROM StudyActivity WHERE program_id = " + programId;
        activitiesMap = fetchStudyActivities(sqlQuery);
        List<String> studyActivities = new ArrayList<>(activitiesMap.keySet());
        // Add the new study activities to the studyActivityBox
        studyActivityBox.getItems().addAll(studyActivities);
    }
    private Map<String, Integer> activitiesMap(Integer activityId) {
        Map<String, Integer> data = new HashMap<>();
        String url = "jdbc:sqlite:identifier.sqlite";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT type_id, ects FROM StudyActivity WHERE activity_id = " + activityId)){
            while (rs.next()) {
                int typeId = rs.getInt("type_id");
                int ects = rs.getInt("ects");
                data.put("type_id", typeId);
                data.put("ects", ects);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    private void addActivityToDatabase(Integer programId, Integer activityId) {
        Map<String, Integer> activityInfo = activitiesMap(activityId);

        String url = "jdbc:sqlite:identifier.sqlite";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO StudentProgram (program_id, activity_id, type_id, ects) VALUES (?, ?, ?, ?)");) {
            pstmt.setInt(1, programId);
            pstmt.setInt(2, activityId);
            pstmt.setInt(3, activityInfo.get("type_id"));
            pstmt.setInt(4, activityInfo.get("ects"));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // This method fetches the bachelor program names and their IDs from the database.
    // A map of program names and their IDs is returned
    private Map<String, Integer> fetchBachelorPrograms(String query) {
        Map<String, Integer> data = new HashMap<>();
        String url = "jdbc:sqlite:identifier.sqlite";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                // Fetch the program ID and name
                int programId = rs.getInt("program_id");
                String name = rs.getString("name");

                // Add to data map
                data.put(name, programId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    // This method fetches the study activities of a certain bachelor program.
    // It returns a list of study activity names.
    private Map<String, Integer> fetchStudyActivities(String query) {
        Map<String, Integer> data = new HashMap<>();
        String url = "jdbc:sqlite:identifier.sqlite";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                // Fetch the activity ID and name
                int activityId = rs.getInt("activity_id");
                String activityName = rs.getString("name");

                // Add to data map
                data.put(activityName, activityId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    // Main method to launch the JavaFX application
    public static void main(String[] args) {
        launch(args);
    }
}