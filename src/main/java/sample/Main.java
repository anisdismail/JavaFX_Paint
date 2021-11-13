package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Stack;

public class Main extends Application
{
    // primary stage

    static Stage primaryStage;
    static Stage secondaryStage;
    static Scene colorPickerScene;
    static Scene startGameScene;
    static Pane colorPickerPane;
    static Pane startGamePane;

    Stack<Image> undoStack = new Stack<>();
    Canvas canvas = new Canvas(500, 300);

    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception
    {
        // new stage for the popup window
        secondaryStage = new Stage();
        secondaryStage.initStyle(StageStyle.UNDECORATED);
        secondaryStage.initModality(Modality.APPLICATION_MODAL);
        secondaryStage.initOwner(Main.primaryStage);

        colorPickerPane = FXMLLoader.load(getClass().getResource("/GUI/ColorPicker.fxml"));
        colorPickerScene = new Scene(colorPickerPane);

        startGamePane = FXMLLoader.load(getClass().getResource("/GUI/GuessPane.fxml"));
        startGameScene = new Scene(startGamePane);

        primaryStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/GUI/MainPane.fxml"));
        AnchorPane root = new AnchorPane();
        fxmlLoader.load();

        root = fxmlLoader.getRoot();
        primaryStage.setTitle("Our Drawing App");

        Scene mainScene = new Scene(root);
        (( Controller ) fxmlLoader.getController()).hookInto(mainScene);

        primaryStage.setScene(mainScene);
        primaryStage.show();
    }
}
