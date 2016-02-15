package edu.bc.luntc.phonehome;

/**
 * Created by cameronlunt on 2/15/16.
 */
public class AppointmentsStorage {
    private static final String FILENAME = "edu.bc.luntc.phonehome.AppointmentsStorage.appointments";

    private static AppointmentsStorage instance;

    private AppointmentsStorage(){};

    public AppointmentsStorage getInstance(){
        if(instance == null)
            instance = new AppointmentsStorage();

        return instance;
    }


}
