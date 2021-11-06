package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.Stack;

public class Main extends Application
{

    // primary stage
    static Stage primaryStage;
    static Stage secondaryStage;
    static Scene colorPickerScene;


    Stack<Image> undoStack = new Stack<>();
    Canvas canvas = new Canvas(500, 300);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception
    {
        // new stage for the popupwindow
        secondaryStage = new Stage();

        Pane colorPickerPane = FXMLLoader.load(getClass().getResource("/GUI/ColorPicker.fxml"));
        colorPickerScene = new Scene(colorPickerPane);

        primaryStage = stage;
        Pane root = FXMLLoader.load(getClass().getResource("/GUI/MainPane.fxml"));
        primaryStage.setTitle("Our Drawing App");

        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }

    public void pushToUndoStack()
    {
        Image snapshot = canvas.snapshot(null, null);
        undoStack.push(snapshot);
    }
}
