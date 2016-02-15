package edu.bc.luntc.phonehome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity {

    private ListView aptList;
    private ArrayList<Appointment>  appointments ;
    private AppointmentAdapter adapter;

    private AppointmentStorageManager appointmentStorageManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        appointmentStorageManager = AppointmentStorageManager.getInstance();

        aptList = (ListView) findViewById(R.id.apt_list);
        appointments = readOrNewList(savedInstanceState);

        adapter = (new AppointmentAdapter(this, R.layout.appointment_list_item, appointments));
        aptList.setAdapter(adapter);
        aptList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateTravelTime(appointments.get(position));
            }
        });

    }

    private ArrayList<Appointment> readOrNewList(Bundle savedInstanceState) {
        if(savedInstanceState != null)
            return (ArrayList<Appointment>)savedInstanceState.getSerializable("stuff");
        else{
            return appointmentStorageManager.readAppointments(this);
        }
    }

    public void addNew(View view){
        Intent intent = new Intent(this, NewApointmentActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putSerializable("stuff", appointments);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        appointmentStorageManager.storeAppointments(this, appointments);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == Activity.RESULT_OK) {
            final Appointment appointment = (Appointment)
                    data.getExtras().getSerializable(NewApointmentActivity.EXTRA_APPOINTMENT);
            appointments.add(appointment);
            adapter.notifyDataSetChanged();
            updateTravelTime(appointment);
        }
    }

    private void updateTravelTime(final Appointment appointment) {
        TravelTimeAsyncTask travelTimeAsyncTask = new TravelTimeAsyncTask(){
            @Override
            protected void onPostExecute(String result){
                appointment.setTravelTime(result);
                int index = appointments.indexOf(appointment);
                appointments.set(index, appointment);
                adapter.notifyDataSetChanged();
            }
        };

        travelTimeAsyncTask.execute(appointment.getPlace(), this);
    }
}
