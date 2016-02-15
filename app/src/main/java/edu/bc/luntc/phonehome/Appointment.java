package edu.bc.luntc.phonehome;

import java.io.Serializable;

public class Appointment implements Serializable{
    private String phonenumber;
    private String place;
    private String time;
    private String date;
    private String travelTime;

    Appointment(String phonenumber, String place, String time, String date){
        this.phonenumber = phonenumber;
        this.place = place;
        this.time = time;
        this.date = date;
    }

    public void setTravelTime(String travelTime){this.travelTime = travelTime;}

    public String getTravelTime(){ return travelTime; }

    public String getPhonenumber(){
        return phonenumber;
    }

    public String getTime() {
        return time;
    }

    public String getPlace() {
        return place;
    }

    public String getDate(){ return date;}

}
