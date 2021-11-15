package sample;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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

    GraphicsContext graphicsContext;

    Text currentHookedLabel;
    String currentText = "" ;

    double currentScale = 1;
    private int MAX_UNDO_HISTORY_SIZE = 50;

    private Scene mainScene;

    // for drawing lines

    double lineFirstPointX = 0 , lineFirstPointY = 0;

    // for drawing rects

    double rectFirstPointX = 0 , rectFirstPointY = 0;

    // for drawing circles

    double circleFirstPointX = 0 , circleFirstPointY = 0;

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

        ADD_LINE_FIRST_POINT,
        ADD_LINE_SECOND_POINT,

        ADD_RECT_FIRST_POINT,
        ADD_RECT_SECOND_POINT,

        ADD_CIRCLE_FIRST_POINT,
        ADD_CIRCLE_SECOND_POINT,
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
    private JFXButton AddLineButton;


    @FXML
    public void initialize() throws IOException
    {
        RunModel.loadModel();

        previewWindowMainWindow.fillProperty().bind(ColorPickerController.previewColor.fillProperty());

        graphicsContext = canvas.getGraphicsContext2D();

        ClearCanvas();
        // init the pen size combobox

        penSizeComboBox.getItems().addAll("Small", "Medium", "Large");
        penSizeComboBox.getSelectionModel().select(0); // first item

        // init some handlers

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED , e ->
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
            else if ( currentMode == Mode.ADD_LINE_FIRST_POINT )
            {
                lineFirstPointX = e.getX();
                lineFirstPointY = e.getY();
                currentMode = Mode.ADD_LINE_SECOND_POINT;
            }
            else if ( currentMode == Mode.ADD_LINE_SECOND_POINT )
            {
                pushToUndoStack();
                graphicsContext.strokeLine( lineFirstPointX , lineFirstPointY , e.getX() , e.getY() );
                currentMode = Mode.ADD_LINE_FIRST_POINT;
            }
            else if ( currentMode == Mode.ADD_RECT_FIRST_POINT )
            {
                rectFirstPointX = e.getX();
                rectFirstPointY = e.getY();
                currentMode = Mode.ADD_RECT_SECOND_POINT; // next click is to add second point
            }
            else if ( currentMode == Mode.ADD_RECT_SECOND_POINT )
            {
                pushToUndoStack();
                DrawRect(  rectFirstPointX , rectFirstPointY , e.getX() , e.getY() ); // draw rect between these points
                currentMode = Mode.ADD_RECT_FIRST_POINT; // next click is to re-add a new rect
            }
            else if ( currentMode == Mode.ADD_CIRCLE_FIRST_POINT )
            {
                circleFirstPointX = e.getX();
                circleFirstPointY = e.getY();
                currentMode = Mode.ADD_CIRCLE_SECOND_POINT;
            }
            else if ( currentMode == Mode.ADD_CIRCLE_SECOND_POINT )
            {
                pushToUndoStack();
                double radius = Math.sqrt( (e.getX()-circleFirstPointX)*(e.getX()-circleFirstPointX) +
                        (e.getY()-circleFirstPointY)*(e.getY()-circleFirstPointY));

                graphicsContext.strokeOval( circleFirstPointX - radius , circleFirstPointY - radius , radius * 2  , radius * 2  );
                currentMode = Mode.ADD_CIRCLE_FIRST_POINT;
            }

        });
    }

    void ClearCanvas()
    {
        graphicsContext.setFill(Color.WHITE);
        graphicsContext.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        graphicsContext.setFill(ColorPickerController.currentColor);
    }

    void DrawRect ( double p1x , double p1y , double p2x , double p2y )
    {
        // the first point we pass to strokeRect should be the one in the top left corner, the other one in the bottom right corner

        double firstX = p1x < p2x ? p1x : p2x ;
        double lengthInX = (p2x > p1x ? p2x : p1x ) - firstX ;
        double firstY = p1y < p2y ? p1y : p2y ;
        double lengthInY = ( p2y > p1y ? p2y : p1y ) - firstY ;

        graphicsContext.strokeRect( firstX , firstY , lengthInX , lengthInY  );
    }

    void UseCurrentTool( double x , double y )
    {
        if ( currentTool == Tool.PEN ) // if pen ,draw
        {
            DrawOval( x , y );
        }
        else // if eraser, erase ,no shit!
        {
            graphicsContext.clearRect(x - 2, y - 2, 7 * currentScale, 7 * currentScale);
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
        graphicsContext.setLineWidth(currentScale);
    }

    @FXML
    void onClearButtonPressed(ActionEvent event)
    {
        ClearCanvas();
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
        currentMode = Mode.NORMAL;
        currentTool = Tool.ERASER;
    }

    @FXML
    void onSelectPenButtonPressed( ActionEvent event )
    {
        currentMode = Mode.NORMAL;
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
    void onAddLineButtonPressed ( ActionEvent event )
    {
        currentMode = Mode.ADD_LINE_FIRST_POINT;
    }

    @FXML
    void onAddRectButtonPressed ( ActionEvent event )
    {
        currentMode = Mode.ADD_RECT_FIRST_POINT;
    }

    @FXML
    void onAddCircleButtonPressed ( ActionEvent event )
    {
        currentMode = Mode.ADD_CIRCLE_FIRST_POINT;
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
                    // exit text entering mode, and write the text to the canvas
                    pushToUndoStack();
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
