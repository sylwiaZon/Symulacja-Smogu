package pictures;

import javafx.scene.image.Image;
import javafx.util.Pair;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Predicate;
import javax.imageio.ImageIO;
import javax.swing.*;


public class Picture {
    double [][]matrix;
    int size;
    String precipitation;
    MapColor[] mapColors;

    private class MapColor {
        Pair<Double,Double> values;
        Color color;
        MapColor(Pair<Double,Double> pair, Color _color){
            color = _color;
            values = pair;
        }
    }
    public Picture (double [][]_matrix, int _size, String choice){
        matrix = _matrix;
        size = _size;
        precipitation = choice;

        if(choice == "PM10"){
            mapColors = fillMap10();
        } else {
            mapColors = fillMap25();
        }
    }


    private MapColor[] fillMap10(){
        MapColor[] PM10 = new MapColor[6];
        PM10[0] = new MapColor(new Pair<>(0.0,20.0),new Color(0,165,0));
        PM10[1] = new MapColor(new Pair<>(20.0,60.0),new Color(0,220,0));
        PM10[2] = new MapColor(new Pair<>(60.0,100.0),new Color(255,245,0));
        PM10[3] = new MapColor(new Pair<>(100.0,140.0),new Color(230,175,0));
        PM10[4] = new MapColor(new Pair<>(140.0,200.0),new Color(255,30,0));
        PM10[5] = new MapColor(new Pair<>(200.0,100000.0),new Color(165,0,0));
        return PM10;
    }

    private MapColor[] fillMap25(){
        MapColor[] PM25 = new MapColor[6];
        PM25[0] = new MapColor(new Pair<>(0.0,12.0),new Color(0,165,0));
        PM25[1] = new MapColor(new Pair<>(12.0,36.0),new Color(0,220,0));
        PM25[2] = new MapColor(new Pair<>(36.0,60.0),new Color(255,245,0));
        PM25[3] = new MapColor(new Pair<>(60.0,84.0),new Color(230,175,0));
        PM25[4] = new MapColor(new Pair<>(84.0,120.0),new Color(255,30,0));
        PM25[5] = new MapColor(new Pair<>(120.0,100000.0),new Color(165,0,0));
        return PM25;
    }

    private Color getColor(double value){
        for(int i = 0; i < 6; i++){
            if(value >= mapColors[i].values.getKey() && value < mapColors[i].values.getValue()){
                return mapColors[i].color;
            }
        }
        return null;
    }

    public void createSmogImage() throws IOException {
        BufferedImage fgImage = ImageIO.read( new File( "src/images/sym.jpg" ) );
        int width = fgImage.getWidth();
        int height = fgImage.getHeight();
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB );
        int r,g,b,col;
        Color color;
        //Do usunięcia jak bd ok mapa
        color = Color.CYAN;
        r = color.getRed();
        g = color.getGreen();
        b = color.getBlue();
        col = (r << 16) | (g << 8) | b;
        for(int i = 0; i <width;i++){
            for(int j = 0;j<height;j++){
                img.setRGB(i,j, col);
            }
        }
        for(int x = 0; x < size; x++){
            for(int y = 0; y < size; y++){
                color = getColor(matrix[x][y]);
                r = color.getRed();
                g = color.getGreen();
                b = color.getBlue();
                col = (r << 16) | (g << 8) | b;
                img.setRGB(x, y, col);
            }
        }
        ImageIO.write(img, "jpg", new File("src/images/colors.jpg"));

    }

    public void createPicture (){
        try {
            createSmogImage();
            OutputStream outStream = new FileOutputStream( "src/images/jpg.jpg" );

            BufferedImage fgImage = ImageIO.read( new File( "src/images/sym.jpg" ) );
            BufferedImage bgImage = ImageIO.read( new File( "src/images/colors.jpg" ) );

            BufferedImage tmpImage = new BufferedImage( fgImage.getWidth(), fgImage.getHeight(), BufferedImage.TYPE_INT_ARGB );
            Graphics gOut = tmpImage.createGraphics();

            gOut.drawImage( fgImage, 0, 0, null );
            gOut.dispose();

            int width = tmpImage.getWidth();
            int height = tmpImage.getHeight();
            int[] pixels = new int[ width * height ];
            pixels = tmpImage.getRGB( 0, 0, width, height, pixels, 0, width );
            for ( int i = 0; i < pixels.length; i++ ) {
                Color c = new Color( pixels[i] );
                int r = c.getRed();
                int g = c.getGreen();
                int b = c.getBlue();
                c = new Color( r, g, b, 150 );
                pixels[i] = c.getRGB();
            }
            tmpImage.setRGB( 0, 0, width, height, pixels, 0, width );

            Graphics bgc = bgImage.createGraphics();
            bgc.drawImage( tmpImage, 0, 0, null );
            bgc.dispose();

            ImageIO.write( bgImage, "jpg", outStream );
            outStream.close();
        }
        catch ( Exception x ) {
            x.printStackTrace();
        }

    }

}
