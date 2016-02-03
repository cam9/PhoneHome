package edu.bc.luntc.phonehome;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import edu.bc.luntc.phonehome.DurationAPI.DurationItem;
import edu.bc.luntc.phonehome.DurationAPI.DurationResponse;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    Gson gson;
    private EditText locationInput;
    private EditText timeInput;
    private EditText contactInput;
    private TextView durationView;
    private TextView travelStatusView;
    private Button startButton;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private AlarmManager alarmMgr;

    private double latitude = 42.336;
    private double longitude = -71.0179;

    private DurationItem duration;
    private String travelStatus;
    private PendingIntent alarmIntent;

    private MeetingModel meetingModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        gson = new Gson();

        locationInput = (EditText) findViewById(R.id.location);
        timeInput = (EditText) findViewById(R.id.time);
        contactInput = (EditText) findViewById(R.id.contact);

        durationView = (TextView) findViewById(R.id.duration);
        travelStatusView = (TextView) findViewById(R.id.travelStatus);

        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        startButton = (Button) findViewById(R.id.startButton);

        meetingModel = new MeetingModel(this);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meetingModel.update(contactInput.getText(), locationInput.getText(), timeInput.getText());
            }
        });
    }

    public void updateTravelTime() {
        String durationRequest = buildDurationRequest();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, durationRequest,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        DurationResponse durationResponse = gson.fromJson(response, DurationResponse.class);
                        duration = durationResponse.rows[0].elements[0].duration;
                        durationView.setText(duration.text);

                        updateTravelStatus();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                durationView.setText("durationView not available");
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    private void updateTravelStatus() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern("hh:mma");
        String time = String.valueOf(timeInput.getText());
        MutableDateTime goal = formatter.parseMutableDateTime(time);
        goal.setYear(DateTime.now().getYear());
        goal.setMonthOfYear(DateTime.now().getMonthOfYear());
        goal.setDayOfMonth(DateTime.now().getDayOfMonth());

        DateTime now = DateTime.now();
        DateTime arrival = now.plusSeconds(duration.value);

        if(arrival.isAfter(goal)) {
            travelStatus = "You will be late!";
            alertLate(arrival);
        }
        else
            travelStatus = "You will be on time";

        travelStatusView.setText(travelStatus);

    }

    private void alertLate(DateTime arrival) {
        String phone = String.valueOf(contactInput.getText());
        String message = "Hey! I'm running late. I should be there at"+arrival.hourOfDay()+":"+arrival.minuteOfDay();

        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (manager.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE) {
            SmsManager.getDefault().sendTextMessage(phone, null, message, null, null);
        }
        else{
            System.out.println(message + "to: "+ phone);
        }
    }

    private String buildDurationRequest() {
        String destination = locationInput.getText().toString();
        String current = latitude+","+longitude;
        destination = encode(destination);
        current = encode(current);
        return "http://maps.googleapis.com/maps/api/distancematrix/json?"+
                "origins="+current+
                "&destinations="+ destination+
                "&traffic_model=pessimistic"+
                "&departure_time=now";
    }

    private String encode(String s) {
        String encoded;
        try {
            encoded = URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            encoded = URLEncoder.encode(s);
        }
        return encoded;
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        System.out.println("failure"+connectionResult);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
}
