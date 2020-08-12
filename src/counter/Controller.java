package counter;

/* Exercise 4
 */
/**
 * This class is the controller of a the
 * words counter program
 *
 * @author  Galvan Jim
 * @version 1.0 8/11/2020
 */

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Controller {

    @FXML
    private Button addFileButton;

    @FXML
    private Button countWordsButton;

    @FXML
    private Button removeFileButton;
    ;

    @FXML
    private ListView<String> listView;

    @FXML
    private ListView<String> listViewResult;

    @FXML
    private Label statusLabel;

    Alert alert = new Alert(Alert.AlertType.INFORMATION);

    FileChooser fileChooser = new FileChooser();

    ArrayList<FileTask> taskList = new ArrayList<>();

    boolean doneCounting = false;

    public void initialize() {
        setUpFileChooser();
        clearStatusLabel();
        setWarningWindow();
    }

    private void setUpFileChooser() {
        // Set file chooser to only accept .txt files
        fileChooser.getExtensionFilters().addAll(new
                FileChooser.ExtensionFilter("Text Files", "*.txt"));
    }

    @FXML
    void addFile(ActionEvent event) {
        clearStatusLabel();
        // Reset all fields and arraylist
        if (isDoneCounting()) {
            clearFields();
            taskList.clear();
            setDoneCounting(false);
        }

        // Get stage to use in file chooser
        Window theStage = getStage(event);

        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(theStage);

        // add a new file to the list
        try {
            taskList.add(new FileTask(file));
        } catch (Exception e) {
            e.printStackTrace();
        }

        listView.getItems().add(file.getName());

        setTaskResult();
    }

    private void setTaskResult() {
        // Set task status
        for (FileTask task : taskList) {
            task.setOnRunning((succeededEvent) -> {
                statusLabel.setText("Counting words");
            });
        }

        // Set a task succeeded state task listener
        for (FileTask task : taskList) {
            task.setOnSucceeded((succeededEvent) -> {
                doneCounting = true;

                listViewResult.getItems().add(task.getResult());
                listView.getItems().add(task.getName());

                clearStatusLabel();
                statusLabel.setText("Done counting");
            });
        }
    }

    @FXML
    void removeFile(ActionEvent event) {
        // Remove items from all fields
        try {
            int selectedTaskIndex = listView.getSelectionModel().getSelectedIndex();
            taskList.remove(selectedTaskIndex);
            listView.getItems().remove(selectedTaskIndex);
            listViewResult.getItems().remove(selectedTaskIndex);
        } catch (ArrayIndexOutOfBoundsException e) {
            alert.showAndWait();
        } catch (IndexOutOfBoundsException e){
            System.out.println(e);
        }
    }

    @FXML
    void countWords(ActionEvent event) {

        try {
            if (!doneCounting || taskList.isEmpty()) {
                clearFields();
                listViewResult.getItems().clear();

                // Create a new Thread pool to store tasks
                ExecutorService executorService =
                        Executors.newFixedThreadPool(taskList.size());

                for (FileTask task : taskList) {
                    executorService.execute(task);
                }
                executorService.shutdown();
            }
        } catch (Exception e) {
            alert.showAndWait();
        }
    }

    // Clear file list and result list
    void clearFields() {
        listView.getItems().clear();
        listViewResult.getItems().clear();
    }

    private Window getStage(ActionEvent event) {
        Node source = (Node) event.getSource();
        return source.getScene().getWindow();
    }

    private void setWarningWindow() {
        alert.setTitle("Warning");
        alert.setHeaderText("Not file selected");
    }

    private boolean isDoneCounting() {
        return doneCounting;
    }

    private void setDoneCounting(boolean value) {
        doneCounting = value;
    }

    private void clearStatusLabel() {
        statusLabel.setText("");
    }
}