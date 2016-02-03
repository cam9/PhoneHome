package edu.bc.luntc.phonehome;

import android.text.Editable;

/**
 * Created by cameronlunt on 12/10/15.
 */
public class MeetingModel {
    private String phonenumber;
    private String place;
    private String time;
    private MainActivity mainActivity;

    public MeetingModel(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void update(Editable phonenumber, Editable place, Editable time){
        this.phonenumber = String.valueOf(phonenumber);
        this.place = String.valueOf(place);
        this.time = String.valueOf(time);

        mainActivity.updateTravelTime();
    }

}
