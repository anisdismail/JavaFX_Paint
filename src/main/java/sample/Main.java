package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.sonatype.plexus.components.sec.dispatcher.model.io.xpp3.SecurityConfigurationXpp3Reader;

public class Main extends Application
{
    // primary stage

    static Stage primaryStage;
    static Stage secondaryStage;
    static Scene colorPickerScene;
    static Scene PredictorScene;

    static Pane colorPickerPane;
    static Pane predictorPane;

    // Controllers

    static GuessController currentGuessController;


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

        predictorPane = FXMLLoader.load(getClass().getResource("/GUI/GuessPane.fxml"));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/GuessPane.fxml"));
        AnchorPane predictorAnchorPane = new AnchorPane();
        loader.load();
        predictorAnchorPane = loader.getRoot();
        PredictorScene = new Scene(predictorAnchorPane);

        // assign the controller
        currentGuessController = loader.getController();

        primaryStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/GUI/MainPane.fxml"));
        AnchorPane root = new AnchorPane();
        fxmlLoader.load();

        root = fxmlLoader.getRoot();
        primaryStage.setTitle("Our Drawing App");

        Scene mainScene = new Scene(root);
        (( Controller ) fxmlLoader.getController()).hookInto(mainScene);

        primaryStage.setResizable(false);
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }
}
