package edu.bc.luntc.phonehome;

import java.io.Serializable;

public class Appointment implements Serializable{
    public static final String TRAVEL_MODE_WALK = "walk";
    public static final String TRAVEL_MODE_BIKE = "bike";
    public static final String TRAVEL_MODE_PUBLIC_TRANSIT = "public_transit";
    public static final String TRAVEL_MODE_DRIVE = "drive";


    private String phone;
    private String place;
    private String time;
    private String travelTime;
    private String plannedTravelMode;

    private long id;

    private Appointment(){}

    private Appointment(Builder builder){
        this.phone = builder.phone;
        this.place = builder.place;
        this.time = builder.time;
        this.id = builder.id;
    }

    public void setTravelTime(String travelTime){this.travelTime = travelTime;}


    public String getTravelTime(){ return travelTime; }

    public String getPhonenumber(){
        return phone;
    }

    public String getTime() {
        return time;
    }

    public String getPlace() {
        return place;
    }

    public long getId(){
        return id;
    }


    public static class Builder{
        private String phone = "";
        private String place = "";
        private String time = "";
        private String date = "";
        private String travelTime = "";
        private long id = -1;

        public Builder phone(String val){
            phone  = val; return this;
        }
        public Builder place(String val){
            place = val; return this;
        }
        public Builder time(String val){
            time = val; return this;
        }
        public Builder id(long val){
            id = val; return this;
        }

        public Appointment build(){
            return new Appointment(this);
        }

    }

}
