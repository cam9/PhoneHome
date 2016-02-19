package edu.bc.luntc.phonehome;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import edu.bc.luntc.phonehome.DurationAPI.DurationItem;
import edu.bc.luntc.phonehome.DurationAPI.DurationResponse;

public abstract class TravelTimeAsyncTask extends AsyncTask<Object, Integer, String>{

    Gson gson = new Gson();

    private GoogleApiClient mGoogleApiClient;

    private DurationItem duration;

    public void travelTimeFromHere(String destination, Context context) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if(mLastLocation != null) {
            String durationRequest = buildDurationRequest(destination, mLastLocation);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, durationRequest,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            DurationResponse durationResponse = gson.fromJson(response, DurationResponse.class);
                            duration = durationResponse.rows[0].elements[0].duration;
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            RequestQueue queue = Volley.newRequestQueue(context);
            queue.add(stringRequest);
        }
        else{
            duration = new DurationItem();
            duration.value = 0;
            duration.text = "travel time not available";
        }

        }


    private String buildDurationRequest(String destination, Location mLastLocation) {
        String current = mLastLocation.getLatitude()+","+mLastLocation.getLongitude();
        destination = encode(destination);
        current = encode(current);
        return "http://maps.googleapis.com/maps/api/distancematrix/json?" +
                "origins=" + current +
                "&destinations=" + destination +
                "&traffic_model=pessimistic" +
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
    protected String doInBackground(Object[] params) {
        mGoogleApiClient = new GoogleApiClient.Builder((Context) params[1])
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.blockingConnect();
        String destination = (String) params[0];
        Context context = (Context) params[1];
        travelTimeFromHere(destination, context);
        while(duration == null);
        return duration.text;
    }

}
