package edu.bc.luntc.phonehome;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AppointmentAdapter extends ArrayAdapter<Appointment> {

    public AppointmentAdapter(Context context, int resource, List<Appointment> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        AppointmentViewHolder holder;
        Appointment temp = getItem(position);

        if(convertView == null){
            holder = new AppointmentViewHolder();

            convertView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.appointment_list_item, parent, false);
            holder.location = (TextView) convertView.findViewById(R.id.location);
            holder.phone = (TextView) convertView.findViewById(R.id.phone);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.travelTime = (TextView) convertView.findViewById(R.id.travelTime);
            convertView.setTag(holder);
        }
        else{
            holder = (AppointmentViewHolder) convertView.getTag();
        }

        holder.location.setText(temp.getPlace());
        holder.time.setText(temp.getTime());
        holder.phone.setText(temp.getPhonenumber());
        holder.date.setText(temp.getDate());
        holder.travelTime.setText(temp.getTravelTime());
        return convertView;
    }

}