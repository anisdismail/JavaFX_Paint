package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Stack;

public class Controller
{

    // consider moving it to a better place
    static final int TIME_TO_PLAY = 20;

    Stack<Image> undoStack = new Stack<>();
    Stack<Image> redoStack = new Stack<>();

    GraphicsContext graphicsContext;

    boolean enterTextOnNextClick = false;
    boolean eraserOnNextClick = false;
    boolean enteringTextRightNow = false;

    Text currentHookedLabel;
    String currentText = "" ;

    double currentScale = 1;
    private int MAX_UNDO_HISTORY_SIZE = 50;

    private Scene mainScene;

    // CURRENT SELECTED TOOL

    enum Tool
    {
        PEN,
        ERASER
    }

    Tool currentTool = Tool.PEN ;

    // CURRENT EDITOR MODE

    enum Mode
    {
        NORMAL, // drawing...
        ADD_TEXT, // adding a text label
        WRITING_TEXT,// writing to a text label
    }

    Mode currentMode = Mode.NORMAL; // drawing is the default mode

    @FXML
    private Canvas canvas;
    @FXML
    private JFXComboBox<String> penSizeComboBox;

    @FXML
    private Rectangle previewWindowMainWindow;

    @FXML
    private JFXButton selectPenButton;

    @FXML
    private JFXButton selectEraserButton;

    @FXML
    private AnchorPane canvasAnchorPane;

    @FXML
    private JFXButton addTextButton;



    @FXML
    public void initialize() throws IOException
    {
        RunModel.loadModel();

        previewWindowMainWindow.fillProperty().bind(ColorPickerController.previewColor.fillProperty());

        graphicsContext = canvas.getGraphicsContext2D();

        graphicsContext.setFill(Color.WHITE);
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        graphicsContext.setFill(Color.BLACK);


        // init the pen size combobox

        penSizeComboBox.getItems().addAll("Small", "Medium", "Large");
        penSizeComboBox.getSelectionModel().select(0); // first item


        // init some handlers
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, e ->
        {
            if ( currentMode == Mode.NORMAL ) // paint if we are in paint mode
            {
                pushToUndoStack();
                UseCurrentTool( e.getX() , e.getY() );
            }
        });

        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, e ->
        {
            if( currentMode == Mode.NORMAL )
                UseCurrentTool( e.getX() , e.getY() );
            else if ( currentMode == Mode.ADD_TEXT )
            {
                // add the text label that the user will subsequently write to
                AddWritingLabel( e.getX() , e.getY() );

                // change mode
                currentMode = Mode.WRITING_TEXT;
            }
        });
    }

    void UseCurrentTool( double x , double y )
    {
        if ( currentTool == Tool.PEN ) // if pen ,draw
        {
            DrawOval( x , y );
        }
        else // if eraser, erase ,no shit!
        {
            graphicsContext.clearRect(x - 2, y - 2, 5 * currentScale, 5 * currentScale);
        }
    }

    void AddWritingLabel( double x , double y )
    {
        currentText = "" ;
        currentHookedLabel = new Text(currentText ); // get written to

        canvasAnchorPane.getChildren().add(currentHookedLabel);
        currentHookedLabel.setLayoutX(x);
        currentHookedLabel.setLayoutY(y);
    }

    void DestroyWritingLabel()
    {
        canvasAnchorPane.getChildren().remove(currentHookedLabel);
    }

    void DrawOval ( double x , double y ) // draw at mouse X and Y
    {
        graphicsContext.setFill(ColorPickerController.currentColor);
        graphicsContext.fillOval(x - 2, y - 2, 5 * currentScale, 5 * currentScale);
    }

    @FXML
    void onLoadButtonPressed(ActionEvent event)
    {
        String loadPath = Utilities.BrowseForFile("Select File to Load");

        try {
            assert loadPath != null;
            Image image = new Image(new FileInputStream(loadPath));
            graphicsContext.drawImage(image, 0, 0);
        } catch (IOException ex) {
            System.out.println("failed to load saved filed file");
            ex.printStackTrace();
        }

    }

    private void saveImage(String savePath) {
        Image img = canvas.snapshot(null, null);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", new File(savePath));
        } catch (IOException ex) {
            System.out.println("failed to save file");
            ex.printStackTrace();
        }

    }

    @FXML
    void onSaveButtonPressed(ActionEvent event) {
        String savePath = Utilities.SaveFileLocation("Select location to Save");
        saveImage(savePath);
    }

    @FXML
    void OnChangeColorButtonPressed(ActionEvent event)
    {
        // create a new popup window
        Main.secondaryStage.setScene(Main.colorPickerScene);
        Main.secondaryStage.show();
    }

    @FXML
    void OnMenuItemChanged(ActionEvent event)
    {
        switch (penSizeComboBox.getValue())
        {
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
    void onClearButtonPressed(ActionEvent event)
    {
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void pushToUndoStack()
    {
        if (undoStack.size() >= MAX_UNDO_HISTORY_SIZE) {
            undoStack.remove(0); // sacrifice oldest undo snapshot
        }

        Image snapshot = canvas.snapshot(null, null);
        undoStack.push(snapshot);
    }

    //TODO: need to add some indicator as to what tool is currently selected
    @FXML
    void onSelectEraserButtonPressed ( ActionEvent event )
    {
        currentTool = Tool.ERASER;
    }

    @FXML
    void onSelectPenButtonPressed( ActionEvent event )
    {
        currentTool = Tool.PEN;
    }

    @FXML
    void onUndoButtonPressed(ActionEvent event)
    {
        if (!undoStack.empty()) // any action to be undone ?
        {
            Image undoImage = undoStack.pop();
            graphicsContext.drawImage(undoImage, 0, 0);
        }
    }

    @FXML
    void onRedoButtonPressed ( ActionEvent event )
    {

    }

    @FXML
    void onAddTextButtonPressed(ActionEvent event)
    {
        currentMode = Mode.ADD_TEXT;
        canvasAnchorPane.requestFocus(); // remove focus from button to be able to detect a pressed SPACE
    }

    @FXML
    public void GuessBtnPressed(ActionEvent actionEvent) throws IOException
    {
        String savePath = "./temp/test.png";
        saveImage(savePath);
        String predClass = RunModel.predict(savePath);
        Main.currentGuessController.setName(predClass);
        Main.secondaryStage.setScene(Main.PredictorScene);
        Main.secondaryStage.show();
    }

    public void hookInto( Scene scene )
    {
        mainScene = scene; // save it for later


        scene.setOnKeyPressed(keyEvent ->
        {
            if ( currentMode == Mode.WRITING_TEXT )
            {
                if ( keyEvent.getCode() == KeyCode.ESCAPE ) // if ESCAPE, stop writing text
                {
                    System.out.println("fill was set to " + currentText);
                    // exit text entering mode, and write the text to the canvas
                    graphicsContext.setFont(currentHookedLabel.getFont());

                    graphicsContext.fillText( currentHookedLabel.getText() ,
                            currentHookedLabel.getLayoutX(), currentHookedLabel.getLayoutY());

                    // remove label
                    DestroyWritingLabel();
                    // change mode
                    currentMode = Mode.NORMAL;
                }
                else if ( keyEvent.getCode() == KeyCode.BACK_SPACE ) // handle backspace manually
                {
                    if ( currentText.length() > 0 )
                        currentText = currentText.substring(0,currentText.length()-1);
                    // update
                    currentHookedLabel.setText( currentText );
                }
                else // let the user write characters
                {
                    currentHookedLabel.setText( currentText += keyEvent.getText());
                }
            }
        });
    }

}
