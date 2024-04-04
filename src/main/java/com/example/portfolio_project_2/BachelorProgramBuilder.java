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

// Main class extending from JavaFX's Application class
public class BachelorProgramBuilder extends Application {
    // ComboBoxes for displaying bachelor programs and their associated study activities
    private ComboBox<String> bachelorProgramBox;
    private ComboBox<String> studyActivityBox;
    // Map to store program names and their associated IDs
    private Map<String, Integer> programsMap;

    @Override
    public void start(Stage stage) {
        // Method to setup the stage
        setupStage(stage);
        stage.show();
    }

    // This method is responsible for setting up the stage i.e., creating the ComboBoxes,
    // fetching initial data, setting up listeners, and defining layout of the GUI
    private void setupStage(Stage stage) {
        bachelorProgramBox = new ComboBox<>();
        studyActivityBox = new ComboBox<>();

        // Fetch base programs from the database and populate the program map and bachelorProgramBox
        programsMap = fetchDataFromDatabase("SELECT program_id, name FROM BachelorProgram");
        bachelorProgramBox.getItems().addAll(programsMap.keySet());

        // Add an event listener to the bachelorProgramBox to react when a new program is selected
        // On selection, studyActivityBox is updated with the corresponding program's study activities
        bachelorProgramBox.valueProperty().addListener((obs, oldProgram, newProgram) -> {
            updateStudyActivityBox(newProgram);
        });

        // Create a VBox layout and add the ComboBoxes to it
        VBox root = new VBox(bachelorProgramBox, studyActivityBox);
        Scene scene = new Scene(root, 500, 500);
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

        // Fetch study activities for the selected program from the database
        List<String> studyActivities = fetchDataActivitiesFromDatabase("SELECT name FROM StudyActivity WHERE program_id = " + programId);

        // Add the new study activities to the studyActivityBox
        studyActivityBox.getItems().addAll(studyActivities);
    }

    // This method fetches the bachelor program names and their IDs from the database.
    // A map of program names and their IDs is returned
    private Map<String, Integer> fetchDataFromDatabase(String query) {
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

    // Main method to launch the JavaFX application
    public static void main(String[] args) {
        launch(args);
    }
}