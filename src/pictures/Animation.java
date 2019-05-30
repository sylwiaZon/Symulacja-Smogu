package pictures;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class Animation {
    public void animate(Vector<double[][]> matrixes, String choice){
        GifSequenceWriter writer = null;
        try {
            int num = matrixes.size();
            writer = new GifSequenceWriter();

            for (int i = 0; i < num; i++) {
                //Creating steps of simulation and combining them together into gif file
                File file = new File ("src/images/animation" + i + ".jpg");
                Picture picture = new Picture(matrixes.get(i),choice);
                picture.createPicture(file);
                BufferedImage next = ImageIO.read(file);
                writer.writeToSequence(next);
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
