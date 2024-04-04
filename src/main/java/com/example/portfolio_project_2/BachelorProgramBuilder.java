package com.example.portfolio_project_2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class BachelorProgramBuilder extends Application {
    private ComboBox<String> bachelorProgramBox;
    private ComboBox<String> studyActivityBox;
    private Map<String, Integer> programsMap;

    @Override
    public void start(Stage stage) {
        setupStage(stage);
        stage.show();
    }

    private void setupStage(Stage stage) {
        bachelorProgramBox = new ComboBox<>();
        studyActivityBox = new ComboBox<>();
        // Fetch base programs from the database
        programsMap = fetchDataFromDatabase("SELECT program_id, name FROM BachelorProgram");
        bachelorProgramBox.getItems().addAll(programsMap.keySet());
        // Add an event listener to the bachelorProgramBox to react when a value is selected
        bachelorProgramBox.valueProperty().addListener((obs, oldProgram, newProgram) -> {
            updateStudyActivityBox(newProgram);
        });
        VBox root = new VBox(bachelorProgramBox, studyActivityBox);
        Scene scene = new Scene(root, 500, 500);
        stage.setTitle("BachelorProgramBuilder");
        stage.setScene(scene);
    }

    /**
     * Fetches the program_id of the selected program and then fetch the study activity.
     */
    private void updateStudyActivityBox(String programName) {
        // Clear the values of the studyActivityBox
        studyActivityBox.getItems().clear();
        // Fetch the program_id for the selected program from the HashMap
        Integer programId = programsMap.get(programName);
        // Fetch study activities for the selected program from the database
        List<String> studyActivities = fetchDataActivitiesFromDatabase("SELECT name FROM StudyActivity WHERE program_id = " + programId);
        // Add the new study activities to the studyActivityBox
        studyActivityBox.getItems().addAll(studyActivities);
    }

    private Map<String, Integer> fetchDataFromDatabase(String query) {
        Map<String, Integer> data = new HashMap<>();
        String url = "jdbc:sqlite:identifier.sqlite";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                // Fetch both program ID and name from the result set
                int programId = rs.getInt("program_id");
                String name = rs.getString("name");
                // Add the program_id and name pair to data
                data.put(name, programId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    private List<String> fetchDataActivitiesFromDatabase(String query) {
        List<String> data = new ArrayList<>();
        String url = "jdbc:sqlite:identifier.sqlite";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String name = rs.getString("name");
                // Add only the activity name to the list
                data.add(name);
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