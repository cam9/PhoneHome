package edu.bc.luntc.phonehome;

/*
    src: http://developer.android.com/training/basics/data-storage/databases.html
 */

import android.provider.BaseColumns;

public final class AppointmentContract {
    public AppointmentContract(){}

    public static abstract class AppointmentEntry implements BaseColumns{
        public static final String TABLE_NAME = "appointment";
        public static final String COLUMN_NAME_DESCRIPTION      = "description";
        public static final String COLUMN_NAME_TIME             = "time";
        public static final String COLUMN_NAME_PLACE_NAME       = "place_name";
        public static final String COLUMN_NAME_PLACE            = "place";
        public static final String COLUMN_NAME_GUEST_PHONE      = "guest_phone";
        public static final String COLUMN_NAME_GUEST_NAME       = "guest_name";
        public static final String COLUMN_NAME_GUEST_EMAIL      = "guest_email";
        public static final String COLUMN_NAME_ID               = "id";
        public static final String COLUMN_NAME_NULLABLE         = "null";
    }
}
