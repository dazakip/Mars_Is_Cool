package mic.model;

import java.sql.*;
import java.util.HashMap;

/**
 * Created by Dazak on 15/03/2017.
 */
public class DataEntry {
    private int timestamp;
    private int sol;
    private String time;
    private double elevation;
    private double airtemp;
    private double UVA;
    private double humidity;
    private double pressure;

    public DataEntry() {

    }

    public void setTimeStamp(int ts) {
        // make timestamp time since landing
        timestamp = ts - 397445868;
    }

    public void setSol(int s) {
        sol = s;
    }

    public void setTime(String s) {
        time = s;
    }

    public void setElevation(double e) {
        elevation = e;
    }

    public void setAirTemp(double at) {
        if (at!=11111)
            airtemp = at - 273.15;
        else
            airtemp = at;
    }

    public void setUVA(double u) {
        UVA = u;
    }

    public void setHumidity(double h) {
        humidity = h;
    }

    public void setPressure(double p) {
    pressure = p;
    }

    public int getTimeStamp(){
        return timestamp;
    }

    public int getSol(){
        return sol;
    }

    public String getTime(){
        return time;
    }

    public double getElevation() {
        return elevation;
    }

    public double getAirTemp(){
        return airtemp;
    }

    public double getUVA(){
        return UVA;
    }

    public double getHumidity(){
        return humidity;
    }

    public double getPressure(){
        return pressure;
    }

}
