package edu.bc.luntc.phonehome;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class AppointmentDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Appointments.db";


    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + AppointmentContract.AppointmentEntry.TABLE_NAME + " (" +
                    AppointmentContract.AppointmentEntry._ID + " INTEGER PRIMARY KEY," +
                    AppointmentContract.AppointmentEntry.COLUMN_NAME_DESCRIPTION+ TEXT_TYPE + COMMA_SEP +
                    AppointmentContract.AppointmentEntry.COLUMN_NAME_TIME+ TEXT_TYPE + COMMA_SEP +
                    AppointmentContract.AppointmentEntry.COLUMN_NAME_PLACE_NAME+ TEXT_TYPE + COMMA_SEP +
                    AppointmentContract.AppointmentEntry.COLUMN_NAME_PLACE+ TEXT_TYPE + COMMA_SEP +
                    AppointmentContract.AppointmentEntry.COLUMN_NAME_GUEST_PHONE+ TEXT_TYPE + COMMA_SEP +
                    AppointmentContract.AppointmentEntry.COLUMN_NAME_GUEST_NAME+ TEXT_TYPE + COMMA_SEP +
                    AppointmentContract.AppointmentEntry.COLUMN_NAME_GUEST_EMAIL+ TEXT_TYPE + COMMA_SEP +
                    AppointmentContract.AppointmentEntry.COLUMN_NAME_ID+ TEXT_TYPE +
            " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + AppointmentContract.AppointmentEntry.TABLE_NAME;

    public AppointmentDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
