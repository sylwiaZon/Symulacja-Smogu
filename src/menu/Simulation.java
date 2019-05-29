package menu;
import java.lang.String;
import smile.interpolation.KrigingInterpolation;

import java.util.*;
import java.util.stream.Stream;

enum AvaliableTraffic {
    LOW, HIGH, MEDIUM
}

public class Simulation{
    int temperature;
    String wind ; //w m/s
    String windDirection;
    float windSpeed;
    double precipitation25;
    double precipitation10;
    double precipitation;
    double [] precipitationFromSensors= new double[9] ;
    String pmType ;
    int duration ;
    boolean raining;
    AvaliableTraffic traffic;
    double[][] sensorsCoordinates = {{268,488},{50,160},{226,200},{400,100},{143,60},{0,200},{595,320},{300,0},{282,500}}; //{{row,col}} // sensorscoordinates[3/4][] - aleje
    // [5][] - N  [6][] - S [7][] - W [8][] - E
    int matrixSize = 596;
    int cuurentHour;
    Vector<double[][]> finalDataforSimulation;
    ApiData data;


    public Simulation(ApiData data_){
        data = data_;
    }

    int getTemperature(){
        return temperature;
    }

    String getWind(){
        return wind;
    }

    double getPrecipitation2() {
        if (this.pmType.equals("PM10")) {
            this.precipitation =this.precipitation10;
        }
        if (this.pmType.equals("PM2")) {
            this.precipitation = this.precipitation25;
        }
        return this.precipitation;
    }

    double getPrecipitation() {
        return this.precipitation;
    }

    String getPmType(){
        return pmType;
    }

    int getDuration(){
        return duration;
    }
    Vector<double[][]> getFinalDataforSimulation(){
        return finalDataforSimulation;
    }

    AvaliableTraffic getTraffic(){
        return traffic;
    }

    double[][] getSensorsCoordinates(){
        return sensorsCoordinates;
    }

    public int getSize(){
        return matrixSize;
    }

    boolean wasPrecipitationChangedByUser(){
        if (this.pmType.equals("PM10") && this.precipitation10 == this.precipitation) {
            return false;
        }
        if (this.pmType.equals("PM25") && this.precipitation25 == this.precipitation) {
            return false;
        }
        return true;
    }

    void initializePrecipitation(){
        double[] precipitationFromApi;
        boolean changed = this.wasPrecipitationChangedByUser();
        if(changed){
            this.precipitationFromSensors[0] = this.precipitation;
            this.precipitationFromSensors[1] = this.precipitation - 2;
            this.precipitationFromSensors[2] = this.precipitation - 3;
            this.precipitationFromSensors[3] = this.precipitation + 5;
            this.precipitationFromSensors[4] = this.precipitation + 5;
            this.precipitationFromSensors[5] = this.precipitation + 3;
            this.precipitationFromSensors[6] = this.precipitation + 3;
            this.precipitationFromSensors[7]=  this.precipitation + 3;
            this.precipitationFromSensors[8]=this.precipitation - 3;
        }
        else {
            precipitationFromApi = Stream.of(data.getMeasurements(this)).mapToDouble(Double::doubleValue).toArray();
            this.precipitationFromSensors[0] = this.precipitation;
            this.precipitationFromSensors[1] = precipitationFromApi[0];
            this.precipitationFromSensors[2] = precipitationFromApi[1];
            this.precipitationFromSensors[3] = precipitationFromApi[2];
            this.precipitationFromSensors[4] = precipitationFromApi[2];
            this.precipitationFromSensors[5] = this.mean(this.precipitationFromSensors[4], precipitationFromApi[1]);
            this.precipitationFromSensors[6] = this.mean(this.precipitationFromSensors[3], precipitationFromApi[1]);
            this.precipitationFromSensors[7] = this.mean(this.precipitationFromSensors[4], precipitationFromApi[1]);
            this.precipitationFromSensors[8] = this.precipitation + 2;
        }

        if(traffic == AvaliableTraffic.MEDIUM) this.precipitationFromSensors[2] +=10;
        else if(traffic == AvaliableTraffic.HIGH) this.precipitationFromSensors[2] +=15;
    }

    double[][] kriging(double[] weights,double[][] sensorsCoordinates) {
        double[][] interpolatedMatrix = new double[matrixSize][matrixSize];

        for(int i = 0; i< sensorsCoordinates.length; i++){
            interpolatedMatrix[(int)sensorsCoordinates[i][0]][(int)sensorsCoordinates[i][1]] = weights[i];
        }

        KrigingInterpolation kriging = new KrigingInterpolation(sensorsCoordinates, weights);
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if(interpolatedMatrix[i][j] == 0.0){
                    interpolatedMatrix[i][j]= kriging.interpolate(j, i);
                }
            }
        }
        return interpolatedMatrix;
    }

    double mean(double firstValue, double secondValue){
        return (firstValue + secondValue) / 2;
    }

    private double[][] propagate(){
        double mulCoefficient = 1;
        double alejeMulCoefficient = 1;
        double[][] propagatedDataMatrix ;
        if (this.temperature >= 0 && this.temperature < 10) mulCoefficient *= 1.05;
        else if (this.temperature >= -5 && this.temperature < 0) mulCoefficient *= 1.1;
        else if (this.temperature < -5) mulCoefficient *= 1.2;

        if (this.raining){
            mulCoefficient *= 0.8;
        }
        if((this.cuurentHour >=6 && this.cuurentHour <=9) || (this.cuurentHour >=15 && this.cuurentHour <=18)) alejeMulCoefficient *= 1.1;
        else if(this.cuurentHour >9 && this.cuurentHour <15) alejeMulCoefficient *= 1.05;
        else alejeMulCoefficient *= 1;

        if(this.windSpeed > 15) mulCoefficient *= 0.5;
        else {
            switch (windDirection) {
                case ("N"):
                    this.precipitationFromSensors[5] = this.mean(this.precipitationFromSensors[5],this.precipitationFromSensors[4]);
                    break;
                case ("S"):
                    this.precipitationFromSensors[6] = this.mean(this.precipitationFromSensors[6],this.precipitationFromSensors[4]);
                    break;
                case ("W"):
                    this.precipitationFromSensors[7] = this.mean(this.precipitationFromSensors[7],this.precipitationFromSensors[4]);
                    break;
                case ("E"):
                    this.precipitationFromSensors[8] = this.mean(this.precipitationFromSensors[8],this.precipitationFromSensors[4]);
                    break;
            }
        }


        for(int i = 0; i < this.precipitationFromSensors.length; i++) this.precipitationFromSensors[i] *= mulCoefficient;
        this.precipitationFromSensors[3] *= alejeMulCoefficient;
        this.precipitationFromSensors[4] *= alejeMulCoefficient;

        propagatedDataMatrix = this.kriging(this.precipitationFromSensors,this.sensorsCoordinates);
        return propagatedDataMatrix;
    }

    void setCurrentHour(){
        Date date = new Date();
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        this.cuurentHour = calendar.get(Calendar.HOUR_OF_DAY);
    }

    void increaseTime(){
        if(this.cuurentHour == 23) this.cuurentHour = 0;
        else this.cuurentHour++;
    }
    void setWindSpeedAndDirection(){
        String windSpeedString = wind.substring(0, wind.length() - 1);
        this.windSpeed = Float.parseFloat(windSpeedString);
        this.windDirection = wind.substring(wind.length() - 1);
    }

    void changeTemperature(){
        if(this.cuurentHour >= 7 && this.cuurentHour <= 18){
            if(this.temperature <= 32) this.temperature ++;
            else this.temperature --;
        }
        else{
            if(this.temperature >= -15) this.temperature --;
            else this.temperature ++;
        }
    }

    void initializeSimulation() {
        Vector<double[][]> finalData = new Vector<>();
        this.setCurrentHour();
        this.setWindSpeedAndDirection();
        double[][] tempDataMatrix = this.kriging(precipitationFromSensors, sensorsCoordinates);
        finalData.add(tempDataMatrix);
        for (int hourOfSimulation = 1; hourOfSimulation < duration; hourOfSimulation++) {
            this.increaseTime();
            this.changeTemperature();
            tempDataMatrix = this.propagate();
            finalData.add(tempDataMatrix);
//            for (int i = 0; i < matrixSize; i++) {
//                for (int j = 0; j < matrixSize; j++) {
//                    System.out.print(tempDataMatrix[i][j] + " ");
//                }
//                System.out.println();
//            }
        }
        this.finalDataforSimulation = finalData;
    }
}
