package com.zuccessful.trueharmony.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.activities.AddDailyRoutActivity;
import com.zuccessful.trueharmony.pojo.RoutineActivity;

import java.util.List;

public class ContactsAdapter extends
        RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView nameTextView;
        public Switch alarm_switch;
        public TextView alarmTime1, alarmTime2, alarmTime3;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            nameTextView = (TextView) itemView.findViewById(R.id.activity_name);
            alarm_switch = (Switch) itemView.findViewById(R.id.alarm_switch);
            alarmTime1 = (TextView) itemView.findViewById(R.id.alarm_time1);
            alarmTime2 = (TextView) itemView.findViewById(R.id.alarm_time2);
            alarmTime3 = (TextView) itemView.findViewById(R.id.alarm_time3);

        }
    }

    private List<RoutineActivity> mContacts;

    // Pass in the contact array into the constructor
    public ContactsAdapter(List<RoutineActivity> routineActivities) {
        mContacts = routineActivities;
    }

    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_contact, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final ContactsAdapter.ViewHolder viewHolder, int position) {
        // Get the data model based on position
        RoutineActivity routineActivity = mContacts.get(position);
        final int view_position = position;
        // Set item views based on your views and data model
        TextView textView = viewHolder.nameTextView;
        textView.setText(routineActivity.getName());
        viewHolder.nameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"activity : " + viewHolder.nameTextView,Toast.LENGTH_SHORT).show();
                Intent dailyRoutIntent = new Intent(v.getContext().getApplicationContext(), AddDailyRoutActivity.class);
                dailyRoutIntent.putExtra("dailyRoutTaskName", viewHolder.nameTextView.getText());
                dailyRoutIntent.putExtra("dailyRoutTaskPos", String.valueOf(view_position));
                v.getContext().getApplicationContext().startActivity(dailyRoutIntent);
            }
        });
//        Button button = viewHolder.messageButton;
//        button.setText(routineActivity.isOnline() ? "Message" : "Offline");
//        button.setEnabled(routineActivity.isOnline());
    }

//     Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mContacts.size();
    }
}