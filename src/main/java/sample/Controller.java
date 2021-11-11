package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;
import java.util.Stack;

public class Controller {

    // consider moving it to a better place
    Stack<Image> undoStack = new Stack<>();
    GraphicsContext graphicsContext;
    String textVal = "test";
    boolean enterTextOnNextClick = false;
    boolean eraserOnNextClick = false;
    double currentScale = 1;
    private int MAX_UNDO_HISTORY_SIZE = 50;
    @FXML
    private Canvas canvas;
    @FXML
    private JFXComboBox<String> penSizeComboBox;

    @FXML
    private Rectangle previewWindowMainWindow;

    @FXML
    private JFXButton eraserBtn;

    @FXML
    public void initialize() {
        previewWindowMainWindow.fillProperty().bind(ColorPickerController.previewColor.fillProperty());

        graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());


        // init the pen size combobox

        penSizeComboBox.getItems().addAll("Small", "Medium", "Large");
        penSizeComboBox.getSelectionModel().select(0); // first item

        // init some handlers

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                e ->
                {
                    pushToUndoStack();
                    if (!eraserOnNextClick) {
                        graphicsContext.setFill(ColorPickerController.currentColor);
                        graphicsContext.fillOval(e.getX() - 2, e.getY() - 2, 5 * currentScale, 5 * currentScale);
                    } else {

                        graphicsContext.clearRect(e.getX() - 2, e.getY() - 2, 5 * currentScale, 5 * currentScale);
                    }

                });

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, e ->
        {
            if (enterTextOnNextClick) {
                pushToUndoStack();
                graphicsContext.fillText(textVal, e.getX(), e.getY());
                enterTextOnNextClick = false;
            }
        });

    }


    @FXML
    void onLoadButtonPressed(ActionEvent event) {
        String loadPath = Utilities.BrowseForFile("Select File to Load");

        try {
            Image image = new Image(new FileInputStream(loadPath));
            graphicsContext.drawImage(image, 0, 0);
        } catch (IOException ex) {
            System.out.println("failed to load saved filed file");
            ex.printStackTrace();
        }

    }

    @FXML
    void onSaveButtonPressed(ActionEvent event) {
        String savePath = Utilities.SaveFileLocation("Select location to Save");
        Image img = canvas.snapshot(null, null);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", new File(savePath));
        } catch (IOException ex) {
            System.out.println("failed to save file");
            ex.printStackTrace();
        }
    }

    @FXML
    void OnChangeColorButtonPressed(ActionEvent event) {
        // create a new popup window
        Main.secondaryStage.setScene(Main.colorPickerScene);
        Main.secondaryStage.show();
    }

    @FXML
    void OnMenuItemChanged(ActionEvent event) {
        switch (penSizeComboBox.getValue()) {
            case "Small":
                currentScale = 1;
                break;
            case "Medium":
                currentScale = 2;
                break;
            case "Large":
                currentScale = 3;
                break;
        }
    }

    @FXML
    void onClearButtonPressed(ActionEvent event) {
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void pushToUndoStack() {
        if (undoStack.size() >= MAX_UNDO_HISTORY_SIZE) {
            undoStack.remove(0); // sacrifice oldest undo snapshot
        }

        Image snapshot = canvas.snapshot(null, null);
        undoStack.push(snapshot);
    }

    @FXML
    void onUndoButtonPressed(ActionEvent event) {
        if (!undoStack.empty()) // any action to be undone ?
        {
            Image undoImage = undoStack.pop();
            graphicsContext.drawImage(undoImage, 0, 0);
        }
    }

    @FXML
    void onAddTextButtonPressed(ActionEvent event) {
        enterTextOnNextClick = true; // next click on canvas will let user enter text
    }

    @FXML
    void onEraserButtonPressed(ActionEvent actionEvent) {
        eraserOnNextClick = !eraserOnNextClick;

    }
    @FXML
    public void QuickDrawBtnPressed(ActionEvent actionEvent) {
        Main.secondaryStage.setScene(Main.startGameScene);
        Main.secondaryStage.show();

    }

}
