package edu.bc.luntc.phonehome;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class ContactsAdapter extends ArrayAdapter<Contact> {

    public ContactsAdapter(Context context, int resource, List<Contact> objects) {
        super(context, resource, objects);
        }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        ContactViewHolder holder;
        Contact temp = getItem(position);

        if(convertView == null){
            holder = new ContactViewHolder();

            convertView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.contact_autocomplete, parent, false);
            holder.email = (TextView) convertView.findViewById(R.id.email);
            holder.phone = (TextView) convertView.findViewById(R.id.phone);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.picture = (ImageView) convertView.findViewById(R.id.picture);
            convertView.setTag(holder);
        }
        else{
            holder = (ContactViewHolder) convertView.getTag();
        }

        //holder.email.setText(temp.getEmail());
        //holder.phone.setText(temp.getPhone());
        holder.name.setText(temp.getName());
        return convertView;
    }
}