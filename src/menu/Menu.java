package menu;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Menu extends Application {
    private TextField temperature, wind, precipitation;
    ComboBox pmType, duration, traffic;
    Simulation simulation;
    CheckBox rain;

    ApiData a = new ApiData();
    @Override
    public void start(Stage stage) throws Exception{

        a.connect();
        simulation = new Simulation(a);
        a.getData(simulation); //wpisanie danych do symulacji

        Scene scene = new Scene(getMenu(),600,550);
        stage.setTitle("Smog Simulation");
        stage.setScene(scene);
        stage.show();
        setData();
    }
    private void setData(){
        wind.setText(simulation.wind);
        temperature.setText(""+simulation.getTemperature());
        precipitation.setText(""+simulation.getPrecipitation());
    }

    private GridPane getMenu() throws FileNotFoundException{
        GridPane gridPane = new GridPane();
        gridPane.add(new Text("Wind"),0, 0);
        gridPane.add(new Text("Force of the wind and \n its direction, eg 12E"),2,0);
        gridPane.add(new Text("Temperature"),0, 1);
        gridPane.add(new Text("Precipitation"),0, 2);
        gridPane.add(new Text("PM type"),0, 3);
        gridPane.add(new Text("Traffic"),0, 4);
        gridPane.add(new Text("Duration of simulation"),0, 5);
        gridPane.add(new Text("Raining"),0,6);
        wind = new TextField ();
        temperature = new TextField ();

        ObservableList<String> pm = FXCollections.observableArrayList (
                "PM10", "PM2");
        pmType = new ComboBox(pm);
        pmType.setValue("PM10");
        pmType.setMinWidth(150);
        pmType.setOnAction(value ->{
            simulation.pmType=pmType.getValue().toString();
            setData();
        });

        ObservableList<String> tr = FXCollections.observableArrayList (
                "low", "medium", "high");
        traffic = new ComboBox(tr);
        traffic.setValue("low");
        traffic.setMinWidth(150);

        ObservableList<String> dr = FXCollections.observableArrayList (
                "1h", "2h", "6h", "12h", "24h", "48h");
        duration = new ComboBox(dr);
        duration.setValue("12h");
        duration.setMinWidth(150);

        precipitation = new TextField();
        gridPane.add(wind,1,0);
        gridPane.add(temperature,1, 1);
        gridPane.add(precipitation,1, 2);
        gridPane.add(pmType,1, 3);
        gridPane.add(traffic,1, 4);
        gridPane.add(duration,1, 5);
        rain = new CheckBox();
        gridPane.add(rain,1,6);
        gridPane.add(simulate(),2,6);
        gridPane.add(dragon(),2,8);
        gridPane.setPadding(new Insets(50, 50, 50, 50));
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setAlignment(Pos.BASELINE_CENTER);

        return gridPane;
    }
    private ImageView dragon() throws FileNotFoundException{

        FileInputStream input = new FileInputStream("src/menu/images/dragon.gif");
        Image image = new Image(input);
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(200);
        imageView.setFitWidth(200);
        return imageView;
    }
    private Button simulate(){
        Button apply = new Button("Simulate!");
        apply.setOnAction(value -> {
            try {
                processData();
                simulation.initializePrecipitation();
//                System.out.println("Api");
//                for(int i =0;i<3;i++){
//                    System.out.println(a.getMeasurements(simulation)[i]);
//                }
//                System.out.println();
                Stage stage2 = (Stage) apply.getScene().getWindow();
                stage2.setTitle("Smog Simulation");
                SimulationWindow window = new SimulationWindow();
                window.setSimulation(simulation);
                Scene scene= new Scene(window.getWindow(),700,700); // zmienic
                stage2.setScene(scene);
                stage2.show();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        apply.setMinWidth(100);
        return apply;
    }

    private void processData(){
        simulation.wind = wind.getText();
        simulation.duration = getDuration();
        simulation.pmType = pmType.getValue().toString();
        simulation.precipitation = Double.parseDouble(precipitation.getText());
        simulation.temperature = Integer.parseInt(temperature.getText());
        simulation.traffic = getTraffic();
        simulation.raining=rain.isSelected();
    }

    private int getDuration(){
        if(!duration.getValue().toString().isEmpty()) {
            String dur = duration.getValue().toString();
            switch (dur) {
                case "1h":
                    return 1;
                case "2h":
                    return 2;
                case "6h":
                    return 6;
                case "12h":
                    return 12;
                case "24h":
                    return 24;
                case "48h":
                    return 48;
            }
        }
        return 0;
    }

    private AvaliableTraffic getTraffic(){
        if(!traffic.getValue().toString().isEmpty()) {
            String tr = traffic.getValue().toString();
            switch (tr) {
                case "low":
                    return AvaliableTraffic.LOW;
                case "medium":
                    return AvaliableTraffic.MEDIUM;
                case "high":
                    return AvaliableTraffic.HIGH;
            }
        }
        return AvaliableTraffic.LOW;
    }
    public static void main(String[] args) {
        launch(args);
    }
}
