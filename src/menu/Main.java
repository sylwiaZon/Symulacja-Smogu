package menu;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Main extends Application {
    private TextField temperature, wind, precipitation;
    ComboBox pmType, duration, traffic;
    Simulation simulation;

    @Override
    public void start(Stage stage) throws Exception{
        simulation = new Simulation();
        Scene scene = new Scene(getMenu(),500,600);
        stage.setTitle("Smog Simulation");
        stage.setScene(scene);
        stage.show();
    }

    private GridPane getMenu(){
        GridPane gridPane = new GridPane();
        gridPane.add(new Text("Wind"),0, 0);
        gridPane.add(new Text("Temperature"),0, 1);
        gridPane.add(new Text("Precipitation"),0, 2);
        gridPane.add(new Text("PM type"),0, 3);
        gridPane.add(new Text("Traffic"),0, 4);
        gridPane.add(new Text("Duration of simulation"),0, 5);
        wind = new TextField ();
        temperature = new TextField ();

        ObservableList<String> pm = FXCollections.observableArrayList (
                "PM10", "PM2");
        pmType = new ComboBox(pm);
        pmType.setMinWidth(150);

        ObservableList<String> tr = FXCollections.observableArrayList (
                "low", "medium", "high");
        traffic = new ComboBox(tr);
        traffic.setMinWidth(150);

        ObservableList<String> dr = FXCollections.observableArrayList (
                "1h", "2h", "6h", "12h", "24h", "48h");
        duration = new ComboBox(dr);
        duration.setMinWidth(150);

        precipitation = new TextField();
        gridPane.add(wind,1,0);
        gridPane.add(temperature,1, 1);
        gridPane.add(precipitation,1, 2);
        gridPane.add(pmType,1, 3);
        gridPane.add(traffic,1, 4);
        gridPane.add(duration,1, 5);

        gridPane.add(simulate(),2,6);

        gridPane.setPadding(new Insets(50, 100, 50, 50));
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        gridPane.setAlignment(Pos.BASELINE_CENTER);

        return gridPane;
    }

    private Button simulate(){
        Button apply = new Button("Simulate!");
        apply.setMinWidth(100);
        return apply;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
