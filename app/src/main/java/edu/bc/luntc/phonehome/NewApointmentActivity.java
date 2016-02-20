package edu.bc.luntc.phonehome;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

public class NewApointmentActivity extends FragmentActivity {

    public final static String EXTRA_APPOINTMENT = "edu.bc.luntc.phonehome.NewApointmentActivity.APT";

    private Button timeInput;
    private Button dateInput;
    private AutoCompleteTextView contactInput;


    private Place aptPlace;

    private Button addButton;

    private ArrayList<Contact> contacts;
    private final NewApointmentActivity ME = this;

    private MutableDateTime jodaDateTime = new MutableDateTime();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_new_apt);

        timeInput = (Button) findViewById(R.id.time);
        dateInput = (Button) findViewById(R.id.date);

        contactInput = (AutoCompleteTextView) findViewById(R.id.contact);
        addButton = (Button) findViewById(R.id.startButton);
        addButton.setEnabled(false);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                aptPlace = place;
                addButton.setEnabled(true);
            }

            @Override
            public void onError(Status status) {
                addButton.setEnabled(false);
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                contacts = fetchContacts();
                ContactsAdapter contactsAdapter = new ContactsAdapter(ME, R.layout.contact_autocomplete, contacts);
                contactInput.setAdapter(contactsAdapter);
                contactInput.setThreshold(1);
            }
        }).run();

    }

    private ArrayList<Contact> fetchContacts() {
        ArrayList<Contact> contacts = new ArrayList<>();

        ContentResolver cr = getBaseContext()
                .getContentResolver();

        Cursor cur = cr
                .query(ContactsContract.Contacts.CONTENT_URI,
                        null,
                        null,
                        null,
                        null);

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur
                        .getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur
                        .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                Contact contact = new Contact();
                contact.name = name;
                contacts.add(contact);
            }
        }
        return contacts;
    }

    public void addNewApt(View view) {
        if(aptPlace != null) {
            Intent returnHome = new Intent(this, HomePage.class);

            Appointment appointment = new Appointment.Builder()
                    .phone(contactInput.getText().toString())
                    .place(aptPlace.getAddress().toString())
                    .time(jodaDateTime.toString())
                    .id(System.currentTimeMillis()+"")
                    .build();

            returnHome.putExtra(EXTRA_APPOINTMENT, appointment);
            setResult(Activity.RESULT_OK, returnHome);
            finish();
        }
    }

    public void pickTime(View view) {
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.registerListener(this);
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public void pickDate(View view) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.registerListener(this);
        newFragment.show(getFragmentManager(), "datePicker");
    }


    void newDate(int year, int month, int day) {
        jodaDateTime.setYear(year);
        jodaDateTime.setMonthOfYear(month);
        jodaDateTime.setDayOfMonth(day);

        DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("MMMMM dd, yyyy");

        dateInput.setText(dateFormatter.print(jodaDateTime));
    }

    void newTime(int hourOfDay, int minute) {
        jodaDateTime.setHourOfDay(hourOfDay);
        jodaDateTime.setMinuteOfDay(minute);

        DateTimeFormatter timeFormatter = DateTimeFormat.forPattern("hh:mma");

        timeInput.setText(timeFormatter.print(jodaDateTime));
    }
}
