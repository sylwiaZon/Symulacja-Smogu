package menu;

enum AvaliableTraffic {
    LOW, HIGH, MEDIUM
}


public class Simulation {
    int temperature;
    String wind;
    int precipitation;
    String pmType;
    int duration;
    AvaliableTraffic traffic;
    
    int getTemperature(){
        return temperature;
    }
    String getWind(){
        return wind;
    }
    int getPrecipitation(){
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
}

