package edu.bc.luntc.phonehome;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;

import edu.bc.luntc.phonehome.Geocode.GeocodeResponse;
import edu.bc.luntc.phonehome.Geocode.GeocodeResults;


public class MainActivity extends AppCompatActivity{

    private EditText location;
    private EditText time;
    private EditText contact;
    private Button startButton;
    private TextView result;
    private GoogleApiClient mGoogleApiClient;
    Gson gson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gson = new Gson();

        location = (EditText) findViewById(R.id.location);
        time = (EditText) findViewById(R.id.time);
        contact = (EditText) findViewById(R.id.contact);
        result = (TextView) findViewById(R.id.result);



        startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                geocode();
            }
        });
    }

    protected void geocode(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="http://maps.googleapis.com/maps/api/geocode/json?address="+location.getText();

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


}
