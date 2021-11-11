package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

import java.util.Random;

public class QuickDrawController {
    @FXML
    private Label sketchPromptId;

    @FXML
    public void initialize(){
        populateLabel();
    }
    @FXML
    public void StartButtonPressed(ActionEvent actionEvent) {
        populateLabel();
        Main.secondaryStage.close();
    }
    private void populateLabel(){
        Random rand= new Random();
        int index= rand.nextInt(RunModel.index2Class.length);
        sketchPromptId.setText(RunModel.index2Class[index]);
        sketchPromptId.setWrapText(true);

    }
}
