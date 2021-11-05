package sample;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Stack;

public class Main extends Application {
    Stack<Image> undoStack = new Stack<>();
    Canvas canvas = new Canvas(500, 300);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Drawing Operations Test");
        Group root = new Group();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        //Button colorPicker= new Button("Choose Color");
        //ComboBox selectThickness= new ComboBox();
        //selectThickness.getItems().addAll("Small","Medium","Large");
        Button addText = new Button("Text");
        Button clearBtn = new Button("Clear");
        Button undoBtn = new Button("Undo");
        Button saveBtn = new Button("Save");
        Button loadBtn=new Button("Load");
        String path = "C:\\Users\\Hp\\Desktop\\test.png";
        String textVal = "test";

        final boolean[] isActiveText = {false};
        addText.setOnAction(e -> isActiveText[0] = true);
        double scale = 2;
        Color colorChoice = Color.BLACK;
        root.getChildren().addAll(canvas, saveBtn);
        clearBtn.setOnAction(e -> {
            pushToUndoStack();
            //TODO: set all values to default
            gc.setFill(Color.WHITE);
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        });
        undoBtn.setOnAction(e -> {
            if (!undoStack.empty()) {
                Image undoImage = undoStack.pop();
                gc.drawImage(undoImage, 0, 0);
            }
        });
        saveBtn.setOnAction(e -> {
            Image img = canvas.snapshot(null, null);
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", new File(path));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        loadBtn.setOnAction(e -> {
            Image img = canvas.snapshot(null, null);
            try {
                Image image = new Image(new FileInputStream(path));
                gc.drawImage(image, 0, 0);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        pushToUndoStack();
                        gc.setFill(colorChoice);
                        gc.fillRoundRect(e.getX() - 2, e.getY() - 2, 2 * scale, 2 * scale, 5, 5);

                    }
                });
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        if (isActiveText[0]) {
                            pushToUndoStack();
                            gc.fillText(textVal, e.getX(), e.getY());
                            isActiveText[0] = false;

                        }
                    }
                });

        primaryStage.setScene(new Scene(root));
        primaryStage.show();

    }

    public void pushToUndoStack() {
        Image snapshot = canvas.snapshot(null, null);
        undoStack.push(snapshot);
    }
}
