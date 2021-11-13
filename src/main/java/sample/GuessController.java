package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class GuessController {
    Label predClassLabel;

    @FXML
    public void StartButtonPressed(ActionEvent actionEvent) {
        Main.secondaryStage.close();
    }

    public void setName(String name) {
        predClassLabel=new Label(name);
        predClassLabel.setTextFill(Color.color(1, 1, 1));
        predClassLabel.setAlignment(Pos.CENTER);
        predClassLabel.setStyle("-fx-font: 24; -fx-font-weight: bold;");
        predClassLabel.setLayoutX(60);
        predClassLabel.setLayoutY(90);
        predClassLabel.setPrefHeight(50);
        predClassLabel.setPrefWidth(223);
        Main.startGamePane.getChildren().add(predClassLabel);

    }


}
