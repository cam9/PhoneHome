package edu.bc.luntc.phonehome;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class HomePage extends AppCompatActivity {

    private ListView aptList;
    private ArrayList<Appointment> appointments;
    private AppointmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

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
            ArrayList<Appointment> r = new ArrayList<>();
            ObjectInputStream objectInputStream = null;
            try{
                objectInputStream = new ObjectInputStream(openFileInput("stuff"));
                Appointment appointment = (Appointment) objectInputStream.readObject();
                while (appointment != null) {
                    r.add(appointment);
                    appointment = (Appointment) objectInputStream.readObject();
                }
                objectInputStream.close();
            }
            catch (Exception e){
                Log.e("PhoneHome", e.toString());
                e.printStackTrace();
            }
            return r;
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
        String FILENAME = "stuff";
        try {
            ObjectOutputStream fos = new ObjectOutputStream(openFileOutput(FILENAME, Context.MODE_PRIVATE));
            for(Appointment appointment: appointments)
                fos.writeObject(appointment);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

        travelTimeAsyncTask.execute(new Object[]{appointment.getPlace(), this});
    }
}
