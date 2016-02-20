package edu.bc.luntc.phonehome;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import edu.bc.luntc.phonehome.AppointmentContract.AppointmentEntry;

public class AppointmentStorageManager {
    private static AppointmentStorageManager instance;

    private AppointmentStorageManager(){}

    public static AppointmentStorageManager getInstance(){
        if(instance == null)
            instance = new AppointmentStorageManager();
        return instance;
    }


    public void addNewAppointment(Appointment appointment, Context context){
        AppointmentDbHelper mDbHelper = new AppointmentDbHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AppointmentEntry.COLUMN_NAME_TIME, appointment.getTime());
        values.put(AppointmentEntry.COLUMN_NAME_GUEST_PHONE, appointment.getPhonenumber());
        values.put(AppointmentEntry.COLUMN_NAME_PLACE, appointment.getPlace());
        values.put(AppointmentEntry._ID, appointment.getId());

        db.insert(
                AppointmentEntry.TABLE_NAME,
                AppointmentEntry.COLUMN_NAME_NULLABLE,
                values);
        db.close();
    }

    public ArrayList<Appointment> queryAppointments(Context context){
        AppointmentDbHelper mDbHelper = new AppointmentDbHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                AppointmentEntry._ID,
                AppointmentEntry.COLUMN_NAME_TIME,
                AppointmentEntry.COLUMN_NAME_PLACE,
                AppointmentEntry.COLUMN_NAME_GUEST_PHONE
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                AppointmentEntry.COLUMN_NAME_TIME + " DESC";

        Cursor c = db.query(
                AppointmentEntry.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        boolean hasNext = c.moveToFirst();
        ArrayList<Appointment> appointments = new ArrayList<>();

        while(!c.isAfterLast()){
            String place = c.getString(c.getColumnIndex(AppointmentEntry.COLUMN_NAME_PLACE));
            String time = c.getString(c.getColumnIndex(AppointmentEntry.COLUMN_NAME_TIME));
            String phone = c.getString(c.getColumnIndex(AppointmentEntry.COLUMN_NAME_GUEST_PHONE));
            int id = c.getInt(c.getColumnIndex(AppointmentEntry._ID));

            Appointment appointment = new Appointment.Builder()
                    .place(place)
                    .time(time)
                    .phone(phone)
                    .id(id)
                    .build();

            appointments.add(appointment);

            c.moveToNext();
        }

        return appointments;
    }


    public void removeAppointment(Appointment appointment, Context context) {
        AppointmentDbHelper mDbHelper = new AppointmentDbHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Define 'where' part of query.
        String selection = AppointmentEntry._ID + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { appointment.getId()+"" };
        // Issue SQL statement.
        db.delete(AppointmentEntry.TABLE_NAME, selection, selectionArgs);
    }
}
