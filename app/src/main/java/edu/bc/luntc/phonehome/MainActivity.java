package edu.bc.luntc.phonehome;

import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

import edu.bc.luntc.phonehome.DurationAPI.DurationResponse;
import edu.bc.luntc.phonehome.Geocode.GeocodeResponse;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private EditText location;
    private EditText time;
    private EditText contact;
    private Button startButton;
    private TextView result;
    private GoogleApiClient mGoogleApiClient;
    Gson gson;
    private Location mLastLocation;
    private TextView mLatitudeText;
    private TextView mLongitudeText;
    private TextView duration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        gson = new Gson();

        location = (EditText) findViewById(R.id.location);
        time = (EditText) findViewById(R.id.time);
        contact = (EditText) findViewById(R.id.contact);
        result = (TextView) findViewById(R.id.result);
        mLatitudeText = (TextView) findViewById(R.id.mLatitudeText);
        mLongitudeText = (TextView) findViewById(R.id.mLongitudeText);
        duration = (TextView) findViewById(R.id.duration);
        mLatitudeText.setText("42.336");
        mLongitudeText.setText("-71.0179");


        startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geocode();
                updateTravelTime();
            }
        });
    }

    private void updateTravelTime() {
        String destination = location.getText().toString();
        String current = String.valueOf(mLatitudeText.getText())+","+String.valueOf(mLongitudeText.getText());
        System.out.println("destination:"+destination);
        destination  = encode(destination);
        System.out.println("destination:"+destination);
        current = encode(current);
        final String url = "http://maps.googleapis.com/maps/api/distancematrix/json?origins="+current+"&destinations="+destination;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        DurationResponse durationResponse = gson.fromJson(response, DurationResponse.class);

                        System.out.println(url);
                        System.out.println(response);

                        String time = durationResponse.rows[0].elements[0].duration.text;
                        duration.setText(time);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                duration.setText("duration not available");
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    protected void geocode(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String query = String.valueOf(location.getText());
        query = encode(query);
        String url ="http://maps.googleapis.com/maps/api/geocode/json?address="+query;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        GeocodeResponse geocodeResponse = gson.fromJson(response, GeocodeResponse.class);
                        result.setText(geocodeResponse.results[0].formatted_address);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                result.setText("whoops");
            }
        });
        queue.add(stringRequest);
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
            mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        }
        else{
            mLatitudeText.setText("42.336");
            mLongitudeText.setText("-71.0179");
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
