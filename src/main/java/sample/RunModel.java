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
    static SavedModelBundle model;
    static String[] index2Class = {
            "Apple", "Bowtie", "Candle", "Door", "Envelope", "Fish", "Guitar", "Ice Cream",
            "Lightning", "Moon", "Mountain", "Star", "Tent", "Toothbrush", "Wristwatch"};

    public static void loadModel() {
        // get path to model folder in resources
        String modelPath = "C:\\Users\\Hp\\Downloads";
        // load saved model
        model = SavedModelBundle.load(modelPath, "serve");
        System.out.println(model.signatures());
    }

    public static String predict() throws IOException {
        //FloatNdArray input_matrix = NdArrays.ofFloats(Shape.of(1, 28, 28, 1));
        Tensor input_tensor = preprocess(ImageIO.read(new File("C:\\Users\\Hp\\Desktop\\test.png")), 28, 28, 1);
        System.out.println(input_tensor.shape());

        HashMap feed_dict = new HashMap();
        feed_dict.put("conv2d_1_input", input_tensor);
        Float[] probs = new Float[15];
        Map<String, Tensor> predictions = model.function("serving_default").call(feed_dict);
        predictions.get("dense_3").asRawTensor().data().asFloats().read(probs);
        System.out.println(Arrays.asList(probs));
        double maxProb = Arrays.stream(probs).mapToDouble(e -> e).max().getAsDouble();
        Arrays.stream(probs).mapToDouble(e -> e).forEach(System.out::println);
        int maxIndex = 0;
        for (int i = 0; i < probs.length; i++) {
            if (Math.abs(probs[i] - maxProb) < 0.00001)
                maxIndex = i;
        }
        // System.out.println(probs);
        return index2Class[maxIndex];

    }

    static TFloat32 preprocess(BufferedImage sourceImage, int imageHeight, int imageWidth, int imageChannels) {
        Shape imageShape = Shape.of(1, imageHeight, imageWidth, imageChannels);
        return TFloat32.tensorOf(imageShape, tensor -> {
            // Scale the image to required dimensions if needed
            BufferedImage image = new BufferedImage(imageWidth,
                    imageHeight, BufferedImage.TYPE_BYTE_GRAY);

            if (sourceImage.getWidth() != imageWidth || sourceImage.getHeight() != imageHeight) {
                // scales the input image to the output image
                Graphics2D g2d = image.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.drawImage(sourceImage, 0, 0, imageWidth, imageHeight, null);
                g2d.dispose();

            } else {
                image = sourceImage;
            }
            try {
                ImageIO.write(image, "png", new File("C:\\Users\\Hp\\Desktop\\newtest2.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Converts the image to floats and convert them to grayscale
            int i = 0;
            for (int h = 0; h < imageHeight; ++h) {
                for (int w = 0; w < imageWidth; ++w) {

                    tensor.setFloat(1 - (image.getData().getDataBuffer().getElemFloat(i++) / 255), 0, h, w, 0);

                }
            }


        });
    }

    public static void main(String[] args) throws IOException {
        loadModel();
        System.out.println(predict());
    }
}
