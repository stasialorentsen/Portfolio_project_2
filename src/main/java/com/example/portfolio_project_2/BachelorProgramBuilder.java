package com.example.portfolio_project_2;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.geometry.Insets;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
public class BachelorProgramBuilder extends Application {
    private static final String DATABASE_URL = "jdbc:sqlite:identifier.sqlite";
    private static final String PROGRAM_QUERY = "SELECT program_id, name FROM BachelorProgram";
    private static final String ACTIVITY_QUERY = "SELECT activity_id, name, type_id FROM StudyActivity where program_id = ";
    private ComboBox<String> bachelorProgramBox = new ComboBox<>();
    private ComboBox<String> basicStudiesBox = new ComboBox<>();
    private ComboBox<String> SubjectModule1Box = new ComboBox<>();
    private ComboBox<String> SubjectModule2Box = new ComboBox<>();
    private ComboBox<String> ElectiveBox = new ComboBox<>();
    private ComboBox<String> Subject1Box = new ComboBox<>();
    private ComboBox<String> Subject2Box = new ComboBox<>();
    private Button addBasicsButton = new Button("Add");
    private Button addSubject1Button = new Button("Add");
    private Button addSubject2Button = new Button("Add");
    private Button addElectiveButton = new Button("Add");
    private TextArea basicsAdded = new TextArea();
    private TextArea subject1Added = new TextArea();
    private TextArea subject2Added = new TextArea();
    private TextArea electiveAdded = new TextArea();
    private Map<String, Integer> programsMap;
    private Map<String, Integer> activitiesMap;
    @Override
    public void start(Stage stage) {
        // GUI layout setup
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(5);
        gridPane.setHgap(5);
        // Use 4 ColumnConstraints to allow each column to be sizing equally
        for (int i=0; i<4; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setPercentWidth(25);
            gridPane.getColumnConstraints().add(column);
        }
        // create labels
        Label basicsLabel = new Label("Basics");
        Label subject1Label = new Label("Subject Module 1");
        Label subject2Label = new Label("Subject Module 2");
        Label electiveLabel = new Label("Elective");
        bachelorProgramBox.setMaxWidth(Double.MAX_VALUE);
        basicStudiesBox.setMaxWidth(Double.MAX_VALUE);
        SubjectModule1Box.setMaxWidth(Double.MAX_VALUE);
        SubjectModule2Box.setMaxWidth(Double.MAX_VALUE);
        Subject1Box.setMaxWidth(Double.MAX_VALUE);
        Subject2Box.setMaxWidth(Double.MAX_VALUE);
        ElectiveBox.setMaxWidth(Double.MAX_VALUE);
        addBasicsButton.setMaxWidth(Double.MAX_VALUE);
        addSubject1Button.setMaxWidth(Double.MAX_VALUE);
        addSubject2Button.setMaxWidth(Double.MAX_VALUE);
        addElectiveButton.setMaxWidth(Double.MAX_VALUE);

        gridPane.add(bachelorProgramBox, 0, 0); // Top of the first column
        gridPane.add(basicsLabel, 0, 1); // 2nd row in the first column
        gridPane.add(basicStudiesBox, 0, 2); // 3rd row in the first column
        gridPane.add(addBasicsButton, 0, 4); // 4th row in the first column
        gridPane.add(basicsAdded, 0, 5); // 5th row in the first column

        gridPane.add(subject1Label, 1, 1);
        gridPane.add(SubjectModule1Box, 1, 2);
        gridPane.add(Subject1Box, 1, 3);
        gridPane.add(addSubject1Button, 1, 4);
        gridPane.add(subject1Added, 1, 5);

        gridPane.add(subject2Label, 2, 1);
        gridPane.add(SubjectModule2Box, 2, 2);
        gridPane.add(Subject2Box, 2, 3);
        gridPane.add(addSubject2Button, 2, 4);
        gridPane.add(subject2Added, 2, 5);

        gridPane.add(electiveLabel, 3, 1);
        gridPane.add(ElectiveBox, 3, 2);
        gridPane.add(addElectiveButton, 3, 4);
        gridPane.add(electiveAdded, 3, 5);

        programsMap = fetchBachelorPrograms(PROGRAM_QUERY);
        bachelorProgramBox.getItems().addAll(programsMap.keySet());
        bachelorProgramBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            Integer selectedProgramId = programsMap.get(newValue);
            // Clear combo boxes
            basicStudiesBox.getItems().clear();
            SubjectModule1Box.getItems().clear();
            SubjectModule2Box.getItems().clear();
            ElectiveBox.getItems().clear();
            // Basics
            basicStudiesBox.focusedProperty().addListener((observable1, oldValue1, newValue1) -> {
                if (newValue1) { // When focus is gained
                    activitiesMap = fetchStudyActivities(selectedProgramId, 1, 3, 5);
                    basicStudiesBox.getItems().addAll(activitiesMap.keySet());
                }
            });
            // Subject Modules
            SubjectModule1Box.focusedProperty().addListener((observable1, oldValue1, newValue1) -> {
                if (newValue1) { // When focus is gained
                    activitiesMap = fetchStudyActivities(selectedProgramId, 2, 4);
                    SubjectModule1Box.getItems().addAll(activitiesMap.keySet());
                }
            });
            SubjectModule2Box.focusedProperty().addListener((observable1, oldValue1, newValue1) -> {
                if (newValue1) { // When focus is gained
                    activitiesMap = fetchStudyActivities(selectedProgramId, 2, 4);
                    SubjectModule2Box.getItems().addAll(activitiesMap.keySet());
                }
            });
            // Elective
            ElectiveBox.focusedProperty().addListener((observable1, oldValue1, newValue1) -> {
                if (newValue1) { // When focus is gained
                    activitiesMap = fetchStudyActivities(selectedProgramId, 1, 2);
                    ElectiveBox.getItems().addAll(activitiesMap.keySet());
                }
            });
        });
        Scene scene = new Scene(gridPane, 500, 500);
        stage.setTitle("BachelorProgramBuilder");
        stage.setScene(scene);
        stage.show();
    }
    // Fetch bachelor program names and IDs
    private Map<String, Integer> fetchBachelorPrograms(String query) {
        Map<String, Integer> data = new HashMap<>();
        try {
            Connection conn = DriverManager.getConnection(DATABASE_URL);
            Statement stmt = conn.createStatement();
            System.out.println(query); // Print SQL statement
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                data.put(rs.getString("name"), rs.getInt("program_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
    // Fetch study activities for a program
    private Map<String, Integer> fetchStudyActivities(int programId, int... activityTypes) {
        StringBuilder sb = new StringBuilder(ACTIVITY_QUERY);
        sb.append(programId);
        sb.append(" AND type_id IN (");
        for (int i = 0; i < activityTypes.length; i++) {
            sb.append(activityTypes[i]);
            if (i < activityTypes.length - 1) {
                sb.append(",");
            }
        }
        sb.append(")");
        Map<String, Integer> data = new HashMap<>();
        try {
            Connection conn = DriverManager.getConnection(DATABASE_URL);
            Statement stmt = conn.createStatement();
            String query = sb.toString();
            System.out.println(query);
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                data.put(rs.getString("name"), rs.getInt("type_id"));
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