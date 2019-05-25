package menu;

import java.lang.String;
import smile.interpolation.KrigingInterpolation;

import java.util.Vector;

enum AvaliableTraffic {
    LOW, HIGH, MEDIUM
}

public class Simulation {
    int temperature;
    String wind = "2N"; //w m/s
    String windDirection;
    double[] precipitation25;
    double[] precipitation10 = {20, 30};
    double[] precipitation;
    String pmType = "PM10";
    int duration = 3;
    boolean raining;
    AvaliableTraffic traffic;
    double[][] sensorsCoordinates = {{1, 1}, {3, 4}}; //{{row,col}}
    double[] tempPM10 = {5, 10};
    int matrixSize = 5;


//    public Simulation(int givenTemperature, String givenWind, String givenPmType, double [] givenPm10,
//                      double [] givenPm25, boolean isRaining ,AvaliableTraffic givenTraffic, int givenDuration){
//          if(matrixSize <= givenPm10 || matrixSize <= givenPm25 ){
//            throw new IllegalArgumentException("Size of matrix must be smaller than length of PM10/25 array");
//    }
//        temperature = givenTemperature;
//        wind = givenWind;
//        pmType = givenPmType;
//        precipitation10 = givenPm10;
//        precipitation25 = givenPm25;
//        raining = isRaining;
//        traffic = givenTraffic;
//        duration = givenDuration;
//    }

    int getTemperature(){
        return temperature;
    }

    String getWind(){
        return wind;
    }

    double[] getPrecipitation() {
        if (this.pmType.equals("PM10")) {
            precipitation = precipitation10;
        }
        if (this.pmType.equals("PM25")) {
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

    double[][] getSensorsCoordinates() {
        return sensorsCoordinates;
    }

    public int getSize() {
        return matrixSize;
    }

    private int distanceToPixels() {
        int pixels;
        String windSpeedString = wind.substring(0, wind.length() - 1);
        windDirection = wind.substring(wind.length() - 1);
        float windSpeed = Float.parseFloat(windSpeedString);
        float realDistanceInOneHour = windSpeed * 3600;
        float metersPerPixel;

        if (windDirection.equals("N") || windDirection.equals("S")) {
            float mapHeightInMeters = 10000;
            metersPerPixel = mapHeightInMeters / matrixSize;
            pixels = Math.round(realDistanceInOneHour / metersPerPixel);
        } else {
            float mapWidthInMeters = 5000;
            metersPerPixel = mapWidthInMeters / matrixSize;
            pixels = Math.round(realDistanceInOneHour / metersPerPixel);
        }
        return pixels;
    }


    double[][] multiply(double[][] matrix, double value) {
        for (int rows = 0; rows < matrix.length; rows++) {
            for (int cols = 0; cols < matrix[0].length; cols++) {
                matrix[rows][cols] *= value;
            }
        }
        return matrix;
    }

    boolean[][] getFlagArray() {
        boolean[][] wasMoved = new boolean[matrixSize][matrixSize];
        for (int rows = 0; rows < wasMoved.length; rows++) {
            for (int cols = 0; cols < wasMoved.length; cols++) {
                wasMoved[rows][cols] = false;
            }
        }
        return wasMoved;
    }

    double[][] moveCols(double[][] dataMatrix, int shift, boolean moveLeft) {
        double[][] movedColsMatrix = dataMatrix;
        boolean[][] wasMoved = this.getFlagArray();
        if (moveLeft) {
            for (int rows = dataMatrix.length - 1; rows >= 0; rows--) {
                for (int cols = dataMatrix.length - 1; cols >= 0; cols--) {
                    if (cols - shift >= 0) {
                        if (!wasMoved[rows][cols - shift]) {
                            movedColsMatrix[rows][cols] = movedColsMatrix[rows][cols - shift];
                            wasMoved[rows][cols] = true;
                        }
                    }
                    if (!wasMoved[rows][cols]) movedColsMatrix[rows][cols] = 0.0;
                }
            }
        } else {
            for (int rows = 0; rows < matrixSize; rows++) {
                for (int cols = 0; cols < matrixSize; cols++) {
                    if (cols + shift < matrixSize) {
                        if (!wasMoved[rows][cols + shift]) {
                            movedColsMatrix[rows][cols] = movedColsMatrix[rows][cols + shift];
                            wasMoved[rows][cols] = true;
                        }
                    }
                    if (!wasMoved[rows][cols]) movedColsMatrix[rows][cols] = 0.0;
                }
            }
        }
        return movedColsMatrix;
    }

    double[][] moveRows(double[][] dataMatrix, int shift, boolean moveUp) {
        double[][] movedColsMatrix = dataMatrix;
        boolean[][] wasMoved = this.getFlagArray();
        if (moveUp) {
            for (int rows = 0; rows < matrixSize; rows++) {
                for (int cols = 0; cols < matrixSize; cols++) {
                    if (rows + shift < matrixSize) {
                        if (!wasMoved[rows + shift][cols]) {
                            movedColsMatrix[rows][cols] = movedColsMatrix[rows + shift][cols];
                            wasMoved[rows][cols] = true;
                        }
                    }
                    if (!wasMoved[rows][cols]) movedColsMatrix[rows][cols] = 0.0;
                }
            }
        } else {
            for (int rows = dataMatrix.length - 1; rows >= 0; rows--) {
                for (int cols = dataMatrix.length - 1; cols >= 0; cols--) {
                    if (rows - shift >= 0) {
                        if (!wasMoved[rows - shift][cols]) {
                            movedColsMatrix[rows][cols] = movedColsMatrix[rows - shift][cols];
                            wasMoved[rows][cols] = true;
                        }
                    }
                    if (!wasMoved[rows][cols]) movedColsMatrix[rows][cols] = 0.0;
                }
            }
        }
        return movedColsMatrix;
    }

    Object[] convertMatrixDataForKrigingFunction(double[][] dataMatrix) {
        int size = 0;
        int index = 0;

        for (int rows = 0; rows < dataMatrix.length; rows++) {
            for (int cols = 0; cols < dataMatrix[0].length; cols++) {
                if (dataMatrix[rows][cols] != 0.0) {
                    size++;
                }
            }
        }
        double[][] coordinates = new double[size][2];
        double[] weights = new double[size];

        for (int rows = 0; rows < matrixSize; rows++) {
            for (int cols = 0; cols < matrixSize; cols++) {
                if (dataMatrix[rows][cols] != 0.0) {
                    coordinates[index][0] = rows;
                    coordinates[index][1] = cols;
                    weights[index] = dataMatrix[rows][cols];
                    index++;
                }
            }
        }
        return new Object[]{weights, coordinates};
    }


    double[][] kriging(double[] weights, double[][] sensorsCoordinates) {
        double[][] interpolatedMatrix = new double[matrixSize][matrixSize];

        for (int i = 0; i < sensorsCoordinates.length; i++) {
            interpolatedMatrix[(int) sensorsCoordinates[i][0]][(int) sensorsCoordinates[i][1]] = weights[i];
        }

        KrigingInterpolation kriging = new KrigingInterpolation(sensorsCoordinates, weights);
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (interpolatedMatrix[i][j] == 0.0) {
                    interpolatedMatrix[i][j] = kriging.interpolate(j, i);
                }
            }
        }
        return interpolatedMatrix;
    }

    private double[][] propagate(double[][] dataMatrix) {
        double mulCoefficient = 1;
        double[][] propagatedDataMatrix = new double[matrixSize][matrixSize];
        int numberOfPixelsToMoveBy = this.distanceToPixels();
        Object[] convertedData;
        switch (windDirection) {
            case ("N"):
                propagatedDataMatrix = this.moveRows(dataMatrix, numberOfPixelsToMoveBy, true);
                convertedData = this.convertMatrixDataForKrigingFunction(propagatedDataMatrix);
                propagatedDataMatrix = this.kriging((double[]) convertedData[0], (double[][]) convertedData[1]);

                break;
            case ("S"):
                propagatedDataMatrix = this.moveRows(dataMatrix, numberOfPixelsToMoveBy, false);
                convertedData = this.convertMatrixDataForKrigingFunction(propagatedDataMatrix);
                propagatedDataMatrix = this.kriging((double[]) convertedData[0], (double[][]) convertedData[1]);
                break;
            case ("W"):
                propagatedDataMatrix = this.moveCols(dataMatrix, numberOfPixelsToMoveBy, true);
                convertedData = this.convertMatrixDataForKrigingFunction(propagatedDataMatrix);
                propagatedDataMatrix = this.kriging((double[]) convertedData[0], (double[][]) convertedData[1]);

                break;
            case ("E"):
                propagatedDataMatrix = this.moveCols(dataMatrix, numberOfPixelsToMoveBy, false);
                convertedData = this.convertMatrixDataForKrigingFunction(propagatedDataMatrix);
                propagatedDataMatrix = this.kriging((double[]) convertedData[0], (double[][]) convertedData[1]);
                break;
        }

        if (this.temperature >= 0 && this.temperature < 10) mulCoefficient *= 1.1;
        else if (this.temperature >= -5 && this.temperature < 0) mulCoefficient *= 1.2;
        else if (this.temperature < -5) mulCoefficient *= 1.4;

        if (raining) mulCoefficient *= 0.7;
        propagatedDataMatrix = this.multiply(propagatedDataMatrix, mulCoefficient);
        return propagatedDataMatrix;
    }

    public Vector<double[][]> getDataForSimulation() {
        Vector<double[][]> finalData = new Vector<>();
        double[][] tempDataMatrix = this.kriging(precipitation, sensorsCoordinates);
        finalData.add(tempDataMatrix);
        for (int hourOfSimulation = 1; hourOfSimulation < duration; hourOfSimulation++) {
            finalData.add(this.propagate(tempDataMatrix));
        }
        return finalData;
    }

}


