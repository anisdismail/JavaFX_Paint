package sample;

import org.tensorflow.SavedModelBundle;
import org.tensorflow.Tensor;
import org.tensorflow.ndarray.Shape;
import org.tensorflow.types.TFloat32;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RunModel {
    private static SavedModelBundle model;
    private static String[] index2Class = {
            "Apple", "Bowtie", "Candle", "Door", "Envelope", "Fish", "Guitar", "Ice Cream",
            "Lightning", "Moon", "Mountain", "Star", "Tent", "Toothbrush", "Wristwatch"};

    public static void loadModel() {
        // get path to model folder in resources
        String modelPath = "./src/main/models/sketchCNN";
        // load saved model
        model = SavedModelBundle.load(modelPath, "serve");
    }

    public static String predict(String imagePath) throws IOException {
        BufferedImage inputImage = ImageIO.read(new File(imagePath));
        Tensor input_tensor = preprocess(inputImage, 28, 28, 1);
        HashMap feed_dict = new HashMap();
        feed_dict.put("conv2d_1_input", input_tensor);
        Float[] probs = new Float[15];
        Map<String, Tensor> predictions = model.function("serving_default").call(feed_dict);
        predictions.get("dense_3").asRawTensor().data().asFloats().read(probs);
        return argMax(probs);
    }

    private static String argMax(Float[] probs) {
        double maxProb = Arrays.stream(probs).mapToDouble(e -> e).max().getAsDouble();
        int maxIndex = 0;
        for (int i = 0; i < probs.length; i++) {
            if (Math.abs(probs[i] - maxProb) < 0.00001)
                maxIndex = i;
        }
        return index2Class[maxIndex];
    }

    private static BufferedImage blurrImage(BufferedImage input) throws IOException {
        Color[] color;

        // Again creating an object of BufferedImage to
        // create output Image
        BufferedImage output = new BufferedImage(
                input.getWidth(), input.getHeight(),
                BufferedImage.TYPE_INT_RGB);

        // Setting dimensions for the image to be processed
        int i = 0;
        int max = 400, rad = 10;
        int a1 = 0, r1 = 0, g1 = 0, b1 = 0;
        color = new Color[max];

        // Now this core section of code is responsible for
        // blurring of an image

        int x = 1, y = 1, x1, y1, ex = 5, d = 0;

        // Running nested for loops for each pixel
        // and blurring it
        for (x = rad; x < input.getHeight() - rad; x++) {
            for (y = rad; y < input.getWidth() - rad; y++) {
                for (x1 = x - rad; x1 < x + rad; x1++) {
                    for (y1 = y - rad; y1 < y + rad; y1++) {
                        color[i++] = new Color(
                                input.getRGB(y1, x1));
                    }
                }

                // Smoothing colors of image
                i = 0;
                for (d = 0; d < max; d++) {
                    a1 = a1 + color[d].getAlpha();
                }

                a1 = a1 / (max);
                for (d = 0; d < max; d++) {
                    r1 = r1 + color[d].getRed();
                }

                r1 = r1 / (max);
                for (d = 0; d < max; d++) {
                    g1 = g1 + color[d].getGreen();
                }

                g1 = g1 / (max);
                for (d = 0; d < max; d++) {
                    b1 = b1 + color[d].getBlue();
                }

                b1 = b1 / (max);
                int sum1 = (a1 << 24) + (r1 << 16)
                        + (g1 << 8) + b1;
                output.setRGB(y, x, sum1);
            }
        }

        // Writing the blurred image on the disc where
        // directory is passed as an argument
        ImageIO.write(
                output, "png",
                new File("./temp/test_smoothed.png"));
        return output;
    }
    private static BufferedImage scaleImage(BufferedImage inputImage,int imageHeight, int imageWidth, int imageChannels){
    // Scale the image to required dimensions if needed
        BufferedImage scaledImage = new BufferedImage(imageWidth,
                imageHeight, BufferedImage.TYPE_BYTE_GRAY);

        if (inputImage.getWidth() != imageWidth || inputImage.getHeight() != imageHeight) {
            // scales the input image to the output image
            Graphics2D g2d = scaledImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(inputImage, 0, 0, imageWidth, imageHeight, null);
            g2d.dispose();

        } else {
            scaledImage = inputImage;
        }
        try {
            ImageIO.write(scaledImage, "png", new File("./temp/test_resized.png"));
        } catch (IOException e) {
            System.out.println("Error in Writing image");
        }
        return scaledImage;
    }

    private static TFloat32 preprocess(BufferedImage sourceImage, int imageHeight, int imageWidth, int imageChannels) throws IOException {
        Shape imageShape = Shape.of(1, imageHeight, imageWidth, imageChannels);
        BufferedImage blurredImage,scaledImage;
        blurredImage=blurrImage(sourceImage);
        scaledImage=scaleImage(blurredImage,imageHeight,imageWidth,imageChannels);
        return TFloat32.tensorOf(imageShape, tensor -> {
            // Converts the image to floats and convert them to grayscale
            int i = 0;
            for (int h = 0; h < imageHeight; ++h) {
                for (int w = 0; w < imageWidth; ++w) {
                    tensor.setFloat(1 - (scaledImage.getData().getDataBuffer().getElemFloat(i++) / 255), 0, h, w, 0);
                }
            }
        });
    }
    public static void main(String[] args){
    }
}
