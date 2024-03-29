/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menu;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author pyszczekk
 */
public class SimulationWindow {
    GridPane window;
    Simulation simulation;
    int play = 0;
    ImageView simulationGif;
    void setSimulation(Simulation sym){
        simulation = sym;
    }

    GridPane getWindow() throws FileNotFoundException{
         window = new GridPane();
         GridPane pane = new GridPane();
        pane.add(new Text("Wind: "+simulation.getWind()),0, 0);
        pane.add(new Text("Temperature: "+simulation.getTemperature()),0, 1);
        pane.add(new Text("Precipitation: "+simulation.getPrecipitation()),0, 2);
        pane.add(new Text("PM type: "+simulation.getPmType()),0, 3);
        pane.add(new Text("Traffic: "+simulation.getTraffic()),0, 4);
        pane.add(new Text("Duration of simulation: "+simulation.getDuration()+"h"),0, 5);
        if(simulation.raining== true)
            pane.add(new Text("Raining: yes"),0,6);
        else pane.add(new Text("Raining: no"),0,6);
       // pane.setPadding(new Insets(50, 50, 50, 50));
        pane.setVgap(10);
        pane.setHgap(10);
        GridPane menu = new GridPane();
        menu.add(home(),0,0);
        menu.add(play(),1,0);
        menu.setPadding(new Insets(150, 0, 50, 200));
        menu.setVgap(10);
        menu.setHgap(10);
        window.add(pane, 0,0);
        window.add(dragon(),0,1);
        window.add(menu,1,1);
        simulationGif = simulations();
        window.add(simulationGif,1,0);
        window.add(scale(),2,0);
        window.setPadding(new Insets(50, 50, 50, 50));
        window.setVgap(10);
        window.setHgap(10);
        window.setAlignment(Pos.BASELINE_CENTER);

        return window;
    }
     private ImageView simulations() throws FileNotFoundException{
        FileInputStream input = new FileInputStream("src/images/sym.gif");
        Image image = new Image(input);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(400);
        imageView.setFitWidth(400);
        return imageView;
    }

    private ImageView scale() throws FileNotFoundException{
        FileInputStream scale = new FileInputStream("src/images/scale.jpg");
        Image image = new Image(scale);
        ImageView scaleImage = new ImageView(image);
        scaleImage.setFitHeight(400);
        scaleImage.setFitWidth(25);
        return scaleImage;
    }
    
    private ImageView home() throws FileNotFoundException{
        FileInputStream input = new FileInputStream("src/images/home.png");
        Image image = new Image(input);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(40);
        imageView.setFitWidth(40);
        imageView.setOnMousePressed(value->{
            try {
                Menu menu = new Menu();
                Stage stage2 = (Stage) imageView.getScene().getWindow();
                stage2.close();
                Stage stage = new Stage();
                menu.start(stage);
            } catch (Exception ex) {
                Logger.getLogger(SimulationWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        return imageView;
    }
    private ImageView play() throws FileNotFoundException{
        FileInputStream play = new FileInputStream("src/images/play.png");
        Image image1 = new Image(play);
        ImageView imageView = new ImageView(image1);
        
        imageView.setFitHeight(40);
        imageView.setFitWidth(40);
        imageView.setOnMousePressed(value->{
            try {
                FileInputStream input = new FileInputStream("src/images/sym.gif");
                Image image = new Image(input);
                simulationGif.setImage(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
        return imageView;
    }
     private ImageView dragon() throws FileNotFoundException{
        
        FileInputStream input = new FileInputStream("src/images/dragon.gif");
        Image image = new Image(input);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(200);
        imageView.setFitWidth(200);
        
        return imageView;
    }
}
