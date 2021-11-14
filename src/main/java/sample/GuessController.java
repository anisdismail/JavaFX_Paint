package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GuessController
{
    @FXML
    private Label predictionLabel;

    @FXML
    public void StartButtonPressed(ActionEvent actionEvent)
    {
        Main.secondaryStage.close();
    }

    public void setName(String name)
    {
        predictionLabel.setText(name);
    }
}
