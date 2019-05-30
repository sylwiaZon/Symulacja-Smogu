package pictures;

import javafx.util.Pair;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;


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

    public Picture (double [][]_matrix, String choice){
        matrix = _matrix;
        size = matrix.length;
        precipitation = choice;
        if(choice == "PM10"){
            mapColors = fillMap10();
        } else {
            mapColors = fillMap25();
        }
    }

    //Filling two structures holding values combined with its colors
    //The maps are different due to dwo different types of precipitation
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
    //Getting color from map
    private Color getColor(double value){
        if(value < mapColors[0].values.getKey()){
            return mapColors[0].color;
        }
        for(int i = 0; i < 6; i++){
            if(value >= mapColors[i].values.getKey() && value < mapColors[i].values.getValue()){
                return mapColors[i].color;
            }
        }
        return null;
    }
    //Creating image consisting of colors meant to be on top of the map
    public void createSmogImage() throws IOException {
        BufferedImage fgImage = ImageIO.read( new File( "src/images/map.jpg" ) );
        int width = fgImage.getWidth();
        int height = fgImage.getHeight();
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB );
        int r,g,b,col;
        Color color;

        for(int x = 0; x < size; x++){
            for(int y = 0; y < size; y++){
                //get color corresponding to the level of precipitation at defined place
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
    //Combining together image of map and the colors corresponding to smog levels
    public void createPicture (File file){
        try {
            createSmogImage();
            OutputStream outStream = new FileOutputStream( file );

            BufferedImage fgImage = ImageIO.read( new File( "src/images/map.jpg" ) );
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
