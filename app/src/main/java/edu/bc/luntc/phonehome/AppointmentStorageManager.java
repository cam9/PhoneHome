package edu.bc.luntc.phonehome;


import android.content.Context;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class AppointmentStorageManager {
    private static final String FILENAME = "edu.bc.luntc.phonehome.AppointmentsStorage.appointments";

    private static AppointmentStorageManager instance;

    private AppointmentStorageManager(){};

    public static AppointmentStorageManager getInstance(){
        if(instance == null)
            instance = new AppointmentStorageManager();
        return instance;
    }


    public void storeAppointments(Context context, List<Appointment> appointments){
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(context.openFileOutput(FILENAME, Context.MODE_APPEND));
            try{
                for(Appointment appointment: appointments){
                    objectOutputStream.writeObject(appointment);
                }
            }
            finally {
                objectOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Appointment> readAppointments(Context context){
        ArrayList<Appointment> appointments = new ArrayList<>();
        ObjectInputStream objectInputStream = null;
        try{
            objectInputStream = new ObjectInputStream(context.openFileInput(FILENAME));
            Appointment appointment = (Appointment) objectInputStream.readObject();
            try {
                while (appointment != null) {
                    appointments.add(appointment);
                    appointment = (Appointment) objectInputStream.readObject();
                }
            }
            finally {
                objectInputStream.close();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return appointments;
    }


}
