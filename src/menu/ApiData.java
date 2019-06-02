/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package menu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author pyszczekk
 */
public class ApiData {

    int temperature;
    String wind;
    double precipitationPM25;
    double precipitationPM10;

    void connect() throws MalformedURLException, IOException{
        /* ----------------- Airly ---------------*/
        
            String stringUrl = "https://airapi.airly.eu/v2/measurements/installation?indexType=AIRLY_CAQI&installationId=8077";
            URL url = new URL(stringUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            String apiKey = "yXGd92NFP1fqqEJrVr93ZIuxInESv1eW";
            connection.setRequestProperty("apikey", apiKey);

            StringBuilder response = new StringBuilder();

            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }           }

            JSONObject jsonObj = new JSONObject(response.toString());

            for(int i=0;i<6;i++){
                JSONArray values = (JSONArray) jsonObj.getJSONObject("current").get("values");
                JSONObject obj =values.getJSONObject(i);
                String name = (String) obj.get("name");

                if (name.equals("PM25")){
                    precipitationPM25=obj.getInt("value");
                }
                if (name.equals("PM10")){
                    precipitationPM10=obj.getInt("value");
                }
                if (name.equals("TEMPERATURE")){
                    temperature=obj.getInt("value");
                }



            }
            connection.disconnect();
        

        /* ----------------- Weather Open Map ---------------*/
       
            String stringUrl2 ="http://api.openweathermap.org/data/2.5/weather?q=Krakow,PL&APPID=8c53f908060ea53a90a656761306d06c";
            URL url2 = new URL(stringUrl2);
            HttpURLConnection connection2 = (HttpURLConnection) url2.openConnection();

            connection2.setRequestMethod("GET");
            connection2.setRequestProperty("Accept", "application/json");
            connection2.setRequestProperty("apikey", "8c53f908060ea53a90a656761306d06c");
            StringBuilder response2 = new StringBuilder();

            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection2.getInputStream()))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    response2.append(line);
                }           }

            JSONObject jsonObj2 = new JSONObject(response2.toString());
            int dir=300;
            if(jsonObj2.getJSONObject("wind").has("deg")){
                dir=(int) jsonObj2.getJSONObject("wind").get("deg");
            }
            if((dir<=56.25 && dir>=0)|| (dir>303.75 && dir<=360) )this.wind =  jsonObj2.getJSONObject("wind").get("speed")+"N";
            if((dir>56.25 && dir<=123.75))this.wind =  jsonObj2.getJSONObject("wind").get("speed")+"E";
            if((dir>123.75 && dir<=236.25))this.wind =  jsonObj2.getJSONObject("wind").get("speed")+"S";
            if((dir>236.25 && dir<=303.75))this.wind =  jsonObj2.getJSONObject("wind").get("speed")+"W";
            connection2.disconnect();
        
    }
    void getData(Simulation simulation){
        simulation.temperature = this.temperature;
        simulation.traffic = AvaliableTraffic.MEDIUM;
        simulation.duration = 12;
        simulation.wind = this.wind;
        simulation.precipitation10=this.precipitationPM10;
        simulation.precipitation25 = this.precipitationPM25;
        simulation.pmType="PM10";
    }

    Double[] getMeasurements(Simulation simulation){

        Double[] tab = new Double[3];

        Integer[] installationId = new Integer[3];
        installationId[0]=1096; // Karmelicka
        installationId[1]=189; // Studencka
        installationId[2]=17; // na Alejach
        if (simulation.getPmType()=="PM10"){
            try {
                for(int k =0;k<3;k++){
                    String stringUrl = "https://airapi.airly.eu/v2/measurements/installation?indexType=AIRLY_CAQI&installationId="+installationId[k];
                    URL url = new URL(stringUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Accept", "application/json");
                    String apiKey = "yXGd92NFP1fqqEJrVr93ZIuxInESv1eW";
                    connection.setRequestProperty("apikey", apiKey);

                    StringBuilder response = new StringBuilder();

                    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            response.append(line);
                        }           }

                    JSONObject jsonObj = new JSONObject(response.toString());

                    for(int i=0;i<6;i++){
                        JSONArray values = (JSONArray) jsonObj.getJSONObject("current").get("values");
                        JSONObject obj =values.getJSONObject(i);
                        String name = (String) obj.get("name");


                        if (name.equals("PM10")){
                            tab[k]=obj.getDouble("value");
                            break;
                        }
                    }
                    connection.disconnect();
                }
            } catch (IOException ex) {
                Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            try {
                for(int k =0;k<3;k++){
                    String stringUrl = "https://airapi.airly.eu/v2/measurements/installation?indexType=AIRLY_CAQI&installationId="+installationId[k];
                    URL url = new URL(stringUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Accept", "application/json");
                    String apiKey = "yXGd92NFP1fqqEJrVr93ZIuxInESv1eW";
                    connection.setRequestProperty("apikey", apiKey);

                    StringBuilder response = new StringBuilder();

                    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        String line;
                        while ((line = bufferedReader.readLine()) != null) {
                            response.append(line);
                        }           }

                    JSONObject jsonObj = new JSONObject(response.toString());

                    for(int i=0;i<6;i++){
                        JSONArray values = (JSONArray) jsonObj.getJSONObject("current").get("values");
                        JSONObject obj =values.getJSONObject(i);
                        String name = (String) obj.get("name");


                        if (name.equals("PM25")){
                            tab[k]=obj.getDouble("value");

                            break;
                        }
                    }
                    connection.disconnect();
                }
            } catch (IOException ex) {
                Logger.getLogger(Menu.class.getName()).log(Level.SEVERE, null, ex);
            }
        }



        return tab;
    }

}