package menu;
import java.lang.String;
import smile.interpolation.KrigingInterpolation;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;
import java.util.stream.Stream;

enum AvaliableTraffic {
    LOW, HIGH, MEDIUM
}

public class Simulation{
    int temperature;
    String wind ; //w m/s
    String windDirection;
    double precipitation25;
    double precipitation10;
    double precipitation;
    double [] precipitationFromSensors= new double[3] ;
    String pmType ;
    int duration ;
    boolean raining;
    AvaliableTraffic traffic;

    double[][] sensorsCoordinates = { {100, 100},{100,200},{400,400}}; //{{row,col}} // sensorscoordinates[2][] - aleje
    int matrixSize = 596;
    int cuurentHour;
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
        boolean changed = this.wasPrecipitationChangedByUser();
        if(changed){
            this.precipitationFromSensors[0]=this.precipitation - 5;
            this.precipitationFromSensors[1]=this.precipitation + 5;
            this.precipitationFromSensors[2]=this.precipitation + 5;

        }
        else precipitationFromSensors = Stream.of(data.getMeasurements(this)).mapToDouble(Double::doubleValue).toArray();
        
        if(traffic == AvaliableTraffic.MEDIUM) this.precipitationFromSensors[2] +=5;
        else if(traffic == AvaliableTraffic.HIGH) this.precipitationFromSensors[2] +=10;

        if (this.pmType.equals("PM10")) this.precipitation10 =this.precipitation;
        if (this.pmType.equals("PM25")) this.precipitation25 = this.precipitation;
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

    private double[][] propagate(){
        double mulCoefficient = 1;
        double alejeMulCoefficient = 1;
        double[][] propagatedDataMatrix ;

        if (this.temperature >= 0 && this.temperature < 10) mulCoefficient *= 1.2;
        else if (this.temperature >= -5 && this.temperature < 0) mulCoefficient *= 1.3;
        else if (this.temperature < -5) mulCoefficient *= 1.4;

        if (this.raining){
            mulCoefficient *= 0.8;
            alejeMulCoefficient *= 0.9;
        }
        if((this.cuurentHour >=6 && this.cuurentHour <=9) || (this.cuurentHour >=15 && this.cuurentHour <=18)) alejeMulCoefficient *= 1.2;
        else if(this.cuurentHour >9 && this.cuurentHour <15) alejeMulCoefficient *= 1.1;
        else alejeMulCoefficient *= 0.95;

        for(int i = 0; i < this.precipitationFromSensors.length - 1; i++) this.precipitationFromSensors[i] *= mulCoefficient;
        this.precipitationFromSensors[2] *= alejeMulCoefficient;
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

    public Vector<double[][]> getDataForSimulation() {
        Vector<double[][]> finalData = new Vector<>();
        this.setCurrentHour();
        double[][] tempDataMatrix = this.kriging(precipitationFromSensors, sensorsCoordinates);
        finalData.add(tempDataMatrix);
        for (int hourOfSimulation = 1; hourOfSimulation < duration; hourOfSimulation++) {
            this.increaseTime();
            this.changeTemperature();
            tempDataMatrix = this.propagate();
            finalData.add(tempDataMatrix);
        }
        return finalData;
    }
}
