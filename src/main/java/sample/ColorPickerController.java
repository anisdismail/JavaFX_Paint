package sample;

import com.jfoenix.controls.JFXSlider;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlurType;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.w3c.dom.Text;

import java.text.NumberFormat;

public class ColorPickerController
{
    @FXML
    private JFXSlider alphaSlider;

    @FXML
    private TextField alphaTextField;

    @FXML
    private JFXSlider blueSlider;

    @FXML
    private TextField blueTextField;

    @FXML
    private JFXSlider greenSlider;

    @FXML
    private TextField greenTextField;

    @FXML
    private Rectangle previewColorPane;

    @FXML
    private JFXSlider redSlider;

    @FXML
    private TextField redTextField;

    @FXML
    public void initialize()
    {
        // init fields
        previewColorPane.setFill(Color.RED);

        // add textField listeners manually

        redTextField.textProperty().addListener((observableValue, oldValue , newValue ) ->
        {
            OnTextFieldChanged( redTextField , redSlider , oldValue , newValue );
        });

        blueTextField.textProperty().addListener((observableValue, oldValue , newValue ) ->
        {
            OnTextFieldChanged( blueTextField , blueSlider , oldValue , newValue );
        });

        greenTextField.textProperty().addListener((observableValue, oldValue , newValue ) ->
        {
            OnTextFieldChanged( greenTextField , greenSlider , oldValue , newValue );
        });

        alphaTextField.textProperty().addListener((observableValue, oldValue , newValue ) ->
        {
            OnTextFieldChanged( alphaTextField , alphaSlider , oldValue , newValue );
        });

        // add slider listeners manually

        redSlider.valueProperty().addListener(( observableValue, oldNumber , newNumber ) ->
        {
            redTextField.setText(""+ newNumber);
        });

        blueSlider.valueProperty().addListener((observableValue, oldNumber , newNumber ) ->
        {
            blueTextField.setText(""+ newNumber);
        });

        greenSlider.valueProperty().addListener(( observableValue, oldNumber , newNumber ) ->
        {
            greenTextField.setText(""+ newNumber);
        });

        alphaSlider.valueProperty().addListener(( observableValue, oldNumber , newNumber ) ->
        {
            alphaTextField.setText(""+ newNumber);
        });

    }
    @FXML
    void OnDoneButtonPressed(ActionEvent event)
    {
        Main.secondaryStage.close();
    }

    void OnTextFieldChanged(TextField field , JFXSlider slider , String oldValue , String newValue )
    {
        if ( newValue.isEmpty() ) newValue = "0";

        try
        {
            int value = Integer.parseInt(newValue);

            if ( value >= 0 && value <= 255 )// valid
            {
                // update what should be updated
                slider.setValue(value);
            }
            else
                field.setText(oldValue);
        }
        catch (Exception exc)
        {
            System.out.println("error when setting text field value");
            exc.printStackTrace();
        }
    }

    void ReEvaluatePickedColor()
    {
//        previewColorPane;
    }
}
