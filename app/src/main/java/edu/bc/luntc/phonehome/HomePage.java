package edu.bc.luntc.phonehome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity {

    private final String EXTRA_APPOINTMENTS = "edu.bc.luntc.phonehome.HomePage.appointments";
    private static final int REQUEST_ADD_NEW = 1;

    private ListView aptList;
    private ArrayList<Appointment>  appointments ;
    private AppointmentAdapter adapter;

    private AppointmentStorageManager appointmentStorageManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
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
        registerForContextMenu(aptList);

    }

    private ArrayList<Appointment> readOrNewList(Bundle savedInstanceState) {
        if(savedInstanceState != null)
            return (ArrayList<Appointment>)savedInstanceState.getSerializable(EXTRA_APPOINTMENTS);
        else{
            return appointmentStorageManager.readAppointments(this);
        }
    }

    public void launchNewAppointmentForm(View view){
        Intent intent = new Intent(this, NewApointmentActivity.class);
        startActivityForResult(intent, REQUEST_ADD_NEW);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.apt_list) {
            getMenuInflater().inflate(R.menu.appointment_context_menu, menu);
            menu.setHeaderTitle("Choose an Option");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem menuItem){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();

        switch (menuItem.getItemId()){
            case R.id.menu_delete_appointment:
                removeAppointment(info.position);
                return true;
            case R.id.menu_refresh_appointment:
                updateTravelTime(appointments.get(info.position));
                return true;
        }
        return false;
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        savedInstanceState.putSerializable(EXTRA_APPOINTMENTS, appointments);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        appointmentStorageManager.storeAppointments(this, appointments);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == Activity.RESULT_OK) {
            switch (requestCode){
                case REQUEST_ADD_NEW:
                    final Appointment appointment = (Appointment)
                        data.getExtras().getSerializable(NewApointmentActivity.EXTRA_APPOINTMENT);
                    addNewAppointment(appointment);
                    break;
            }
        }
    }

    private void addNewAppointment(Appointment appointment) {
        appointments.add(appointment);
        adapter.notifyDataSetChanged();
        updateTravelTime(appointment);
    }


    private void removeAppointment(int index){
        appointments.remove(index);
        adapter.notifyDataSetChanged();
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
