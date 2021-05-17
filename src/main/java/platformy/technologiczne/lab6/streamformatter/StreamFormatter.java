package platformy.technologiczne.lab6.streamformatter;

import org.apache.commons.lang3.tuple.Pair;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class StreamFormatter {

    /**
     * destination path for new files
     */
    private final Path destination;

    /**
     * source path to files
     */
    private final Path source;


    /**
     * Initialize the object
     * @param _source source path
     * @param _destination destination path
     */
    public StreamFormatter(Path _source, Path _destination) {
        this.source = _source;
        this.destination = _destination;
    }


    /**
     * Create pair of objects (String, BufferedImage)
     * @param source source path of the image
     * @return pair of objects or null when sth has gone wrong
     */
    public Pair<String, BufferedImage> createNameWithImagePair(Path source) {
        BufferedImage image;
        String fileName;

        try{
            image = ImageIO.read(source.toFile());
            fileName = source.getFileName().toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return Pair.of(fileName, image);
    }


    /**
     * Create pair of objects (String, BufferedImage)
     * @param imagePair pair of objects (String, BufferedImage)
     * @return new pair of objects with new changed image
     */
    public Pair<String, BufferedImage> createNameWithNewImagePair(Pair<String, BufferedImage> imagePair) {

        BufferedImage image = imagePair.getRight();
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());

        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                int rgb = image.getRGB(i, j);
                Color color = new Color(rgb);

                int red = (int)(color.getRed() * 0.299);
                int green = (int)(color.getGreen() * 0.587);
                int blue = (int)(color.getBlue() * 0.114);
                Color outColor = new Color(red, green, blue);

                int outRGB = outColor.getRGB();
                newImage.setRGB(i, j, outRGB);
            }
        }

        return Pair.of(imagePair.getLeft(), newImage);
    }


    /**
     * Save image in destination path
     * @param imageToSave pair of objects to save
     */
    public void save(Pair<String, BufferedImage> imageToSave) {

        BufferedImage image = imageToSave.getRight();
        String destPath = this.destination + "\\" + imageToSave.getLeft();
        File file = new File(destPath);

        boolean savingResult;
        try{
            savingResult = file.createNewFile();
            if( savingResult ) {
                ImageIO.write(image, "JPG", file);
            }
            else {
                System.out.println("File is existing");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
