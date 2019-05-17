package menu;

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
    AvaliableTraffic traffic;
    
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
}

