package sample;

import com.jfoenix.controls.JFXSlider;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.text.DecimalFormat;

public class ColorPickerController
{
    // current selected colors

    static Color currentColor = Color.BLACK;
    static Rectangle previewColor;

    private static final DecimalFormat format = new DecimalFormat("#.##");

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
        // set preview color
        previewColor = previewColorPane;

        // init fields
        previewColorPane.setFill(currentColor);

        redSlider.setValue(0);
        blueSlider.setValue(0);
        greenSlider.setValue(0);
        alphaSlider.setValue(0.5);

        redTextField.setText("0");
        blueTextField.setText("0");
        greenTextField.setText("0");
        alphaTextField.setText("0.5");

        // add textField listeners manually

        redTextField.textProperty().addListener((observableValue, oldValue , newValue ) ->
        {
            OnTextFieldChanged( redTextField , redSlider , oldValue , newValue );
            ReEvaluatePickedColor();
        });

        blueTextField.textProperty().addListener((observableValue, oldValue , newValue ) ->
        {
            OnTextFieldChanged( blueTextField , blueSlider , oldValue , newValue );
            ReEvaluatePickedColor();
        });

        greenTextField.textProperty().addListener((observableValue, oldValue , newValue ) ->
        {
            OnTextFieldChanged( greenTextField , greenSlider , oldValue , newValue );
            ReEvaluatePickedColor();
        });

        // can't use OnTextFieldChanged since the other labels use ints instead of a double value
        alphaTextField.textProperty().addListener((observableValue, oldValue , newValue ) ->
        {
//            OnTextFieldChanged( alphaTextField , alphaSlider , oldValue , newValue );
            if ( newValue.isEmpty() ) newValue = "0";

            try
            {
                double value = Double.parseDouble(newValue);

                if ( value >= 0 && value <= 1)// valid
                {
                    // update what should be updated
                    alphaSlider.setValue(value);
                }
                else
                    alphaTextField.setText(oldValue);
            }
            catch (Exception exc)
            {
                System.out.println("error when setting text field value");
            }

            ReEvaluatePickedColor();
        });

        // add slider listeners manually

        redSlider.valueProperty().addListener(( observableValue, oldNumber , newNumber ) ->
        {
            redTextField.setText(""+ newNumber.intValue());
            ReEvaluatePickedColor();
        });

        blueSlider.valueProperty().addListener((observableValue, oldNumber , newNumber ) ->
        {
            blueTextField.setText(""+ newNumber.intValue());
            ReEvaluatePickedColor();
        });

        greenSlider.valueProperty().addListener(( observableValue, oldNumber , newNumber ) ->
        {
            greenTextField.setText(""+ newNumber.intValue());
            ReEvaluatePickedColor();
        });

        alphaSlider.valueProperty().addListener(( observableValue, oldNumber , newNumber ) ->
        {
            alphaTextField.setText(format.format(newNumber));
            ReEvaluatePickedColor();
        });

    }
    @FXML
    void OnDoneButtonPressed(ActionEvent event)
    {
        // save the color
        currentColor = CalculateColor();

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
        }
    }

    void ReEvaluatePickedColor() // update the preview color pane
    {
        previewColorPane.setFill(CalculateColor());
    }

    Color CalculateColor()
    {
        // pull the values from the sliders directly
        return new Color(
                /*red value */ (redSlider.getValue()/255) ,
                /*blue value */ (greenSlider.getValue()/255) ,
                /*green value */ (blueSlider.getValue()/255) ,
                /*alpha value */ (alphaSlider.getValue())
        );
    }

}
