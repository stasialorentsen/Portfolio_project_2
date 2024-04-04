package com.example.portfolio_project_2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BachelorProgramBuilder extends Application {

    private ComboBox<String> baseProgramBox;
    private ComboBox<String> subjectModuleBox;

    @Override
    public void start(Stage stage) {
        setupStage(stage);
        stage.show();
    }

    private void setupStage(Stage stage) {
        baseProgramBox = new ComboBox<>();
        subjectModuleBox = new ComboBox<>();

        // Load the SQLite JDBC driver
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }

        // Fetch base programs from the database
        List<String> basePrograms = fetchDataFromDatabase("SELECT name FROM BachelorProgram");

        baseProgramBox.getItems().addAll(basePrograms);

        // Add an event listener to the baseProgramBox to react when a value is selected
        baseProgramBox.valueProperty().addListener((obs, oldProgram, newProgram) -> {
            updateSubjectModuleBox(newProgram);
        });

        VBox root = new VBox(baseProgramBox, subjectModuleBox);
        Scene scene = new Scene(root, 500, 500);
        stage.setTitle("BachelorProgramBuilder");
        stage.setScene(scene);
    }

    private void updateSubjectModuleBox(String program) {
        // Clear the values of the subjectModuleBox
        subjectModuleBox.getItems().clear();

        // Fetch subject modules for the selected program from the database
        List<String> subjectModules = fetchDataFromDatabase("SELECT name FROM StudyActivity WHERE type_id = 2 AND program_id = (SELECT program_id FROM BachelorProgram WHERE name = '" + program + "')");

        // Add the new subject modules to the subjectModuleBox
        subjectModuleBox.getItems().addAll(subjectModules);
    }

    private List<String> fetchDataFromDatabase(String query) {
        List<String> data = new ArrayList<>();
        String url = "jdbc:sqlite:identifier.sqlite";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String value = rs.getString(1); // Assuming the result set contains a single column
                data.add(value);
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
