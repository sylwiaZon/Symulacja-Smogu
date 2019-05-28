package menu;

import smile.interpolation.KrigingInterpolation;

enum AvaliableTraffic {
    LOW, HIGH, MEDIUM
}


public class Simulation {
    int temperature;
    String wind; //w m/s 
    int precipitation25;
    int precipitation10;
    int precipitation;
    String pmType;
    int duration;
    boolean raining; //true - pada, false - nie 
    AvaliableTraffic traffic;
    int[] sensorsCoordinatesX={1,2,3,4,5};
    int[] sensorsCoordinatesY ={1,2,3,4,5};
    double[] tempPM10 = {1,2,3,4,5};
    public int size = 596;
    
    int getTemperature(){
        return temperature;
    }
    String getWind(){
        return wind;
    }
    int getPrecipitation(){
        if(this.pmType =="PM10"){
            precipitation = precipitation10;
        }
        if(this.pmType =="PM2"){
            precipitation = precipitation25;
        }        
        return precipitation;
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
    int[] getSensorsCoordinateX(){
        return sensorsCoordinatesX;
    }
    int[] getSensorsCoordinateY(){
        return sensorsCoordinatesY;
    }
    double[] getTempPM10(){
        return tempPM10;
    }


    double[][] kriging(double[] weights) {

        double[][] dataMatrix = new double[size][size];
        double[][] coordinates = new double[sensorsCoordinatesX.length][2];

        for(int i = 0; i< sensorsCoordinatesX.length; i++){
            dataMatrix[sensorsCoordinatesX[i]][sensorsCoordinatesY[i]] = weights[i];
            coordinates[i][0] = sensorsCoordinatesX[i];
            coordinates[i][1] = sensorsCoordinatesY[i];
        }

        KrigingInterpolation kriging = new KrigingInterpolation(coordinates, weights);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if(dataMatrix[i][j] == 0.0){
                    dataMatrix[i][j]= kriging.interpolate(j, i);
                }
            }
        }
//        for (int i = 0; i < size; i++) {
//            for (int j = 0; j <size; j++) {
//                System.out.print(dataMatrix[i][j] + " ");
//            }            System.out.println();
//
//        }
        return dataMatrix;
    }

    double[][] propagation(double[][] dataMatrix){
        double[][] finalData = new double[size][size];

        return finalData;
    }



}

