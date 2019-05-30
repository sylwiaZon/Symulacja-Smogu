package pictures;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class ColorsScale {

    int height = 596;
    int width = 100;




    private void createColor (Color color, String comm, String name, boolean first) throws IOException {
        int h = 100;
        if(first){
            h = 96;
        }
        BufferedImage img = new BufferedImage(width, h, BufferedImage.TYPE_INT_RGB );
        int r,g,b,col;

        for(int x = 0; x < width; x++){
            for(int y = 0; y < h; y++){
                r = color.getRed();
                g = color.getGreen();
                b = color.getBlue();
                col = (r << 16) | (g << 8) | b;
                img.setRGB(x, y, col);
            }
        }

        ImageIO.write(img, "jpg", new File("src/images/scale" + name + ".jpg"));
    }

    public void createScale() {
        try {
            createColor(new Color(0, 165, 0), "Very low", "0", true);
            createColor(new Color(0, 220, 0), "Low", "1", false);
            createColor(new Color(255, 245, 0), "Medium", "2", false);
            createColor(new Color(230, 175, 0), "High", "3", false);
            createColor(new Color(255, 30, 0), "Very High", "4", false);
            createColor(new Color(165, 0, 0), "Extreme", "5", false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = img.createGraphics();
        int currheight = 0;
        for (int i = 5; i >= 0; i--) {
            try {
                BufferedImage color = ImageIO.read( new File( "src/images/scale" + i + ".jpg" ) );
                Graphics2D g2d = color.createGraphics();
                g2d.drawImage(color, null, color.getHeight(),0);
                g2d.dispose();
                graphics.drawImage(color,0,currheight,null);
                currheight += color.getHeight();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            ImageIO.write(img, "jpg", new File("src/images/scale.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
