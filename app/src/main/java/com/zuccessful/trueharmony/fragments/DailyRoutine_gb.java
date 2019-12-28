package com.zuccessful.trueharmony.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.activities.AddDailyRoutActivity;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.DailyRoutine;
import com.zuccessful.trueharmony.pojo.RoutineActivity;
import com.zuccessful.trueharmony.receivers.AlarmReceiver;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.util.Arrays;
import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;


public class DailyRoutine_gb extends Fragment {
    private SakshamApp app;
    private static AlarmManager alarmManager;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    private Context mContext;
    private RecyclerView activityList;
    private ProgressBar progressBar;


    public DailyRoutine_gb() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        app = SakshamApp.getInstance();
        db = app.getFirebaseDatabaseInstance();
        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_routine_gb, container, false);
        activityList = view.findViewById(R.id.rvContacts);
        activityList.setLayoutManager(linearLayoutManager);
        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        fetchActivities();
        return view;
    }

    private void fetchActivities(){
//        if(Utilities.isInternetOn(mContext)) {
            Query query = db.collection("alarms/" + app.getAppUser(null).getId() + "/daily_routine/");

            FirestoreRecyclerOptions<RoutineActivity> response = new FirestoreRecyclerOptions.Builder<RoutineActivity>()
                    .setQuery(query, RoutineActivity.class)
                    .build();

            adapter = new FirestoreRecyclerAdapter<RoutineActivity, ActivityHolder>(response) {

                @Override
                public void onBindViewHolder(final ActivityHolder holder, int position, final RoutineActivity model) {
                    progressBar.setVisibility(View.GONE);
                    String name = model.getName();
                    String id = model.getId();
                    holder.textName.setText(name);
                    holder.taskId.setText(model.getId());
                    int alarms = model.getReminders().size();
                    boolean alarmStatus = model.getAlarmStatus();
                    if (name.equalsIgnoreCase(getResources().getStringArray(R.array.leisure_activity_pref_arrays)[2])) {
                        holder.imageView.setImageDrawable(getResources().getDrawable(R.drawable.eating));
                    } else if (name.equalsIgnoreCase(getResources().getStringArray(R.array.leisure_activity_pref_arrays)[1])) {
                        holder.imageView.setImageDrawable(getResources().getDrawable(R.drawable.sleeping));
                    } else if (Arrays.asList(getResources().getStringArray(R.array.leisure_activity_pref_arrays)).contains(name)) {
                        holder.imageView.setImageDrawable(getResources().getDrawable(R.drawable.leisureactivities));
                    } else if (Arrays.asList(getResources().getStringArray(R.array.phy_act_pref_arrays)).contains(name)) {
                        holder.imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_physical_health));
                    } else {
                        holder.imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_avatar));
                    }

                    holder.alarmStatus.setChecked(alarmStatus);
                    if (alarms == 1) {
                        holder.alarm_time3.setVisibility(View.GONE);
                        holder.alarm_time2.setVisibility(View.GONE);
                        holder.alarm_time1.setText(model.getReminders().get(0));
                    }
                    if (alarms == 2) {
                        holder.alarm_time2.setText(model.getReminders().get(1));
                        holder.alarm_time3.setVisibility(View.GONE);
                    }

                    if (alarms == 3) {
                        holder.alarm_time2.setText(model.getReminders().get(1));
                        holder.alarm_time3.setText(model.getReminders().get(2));
                    }
                    final DailyRoutine dailyRoutineObj = new DailyRoutine(name, model.getReminders(), id);


                    if (alarms == 3)
                        holder.alarm_time3.setText(model.getReminders().get(2));

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent dailyRoutIntent = new Intent(v.getContext().getApplicationContext(), AddDailyRoutActivity.class);
                            dailyRoutIntent.putExtra("dailyRoutTaskName", String.valueOf(holder.textName.getText()));
                            dailyRoutIntent.putExtra("dailyRoutTaskid", String.valueOf(holder.taskId.getText()));
                            startActivity(dailyRoutIntent);
                        }
                    });

                    holder.alarmStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (!Utilities.isInternetOn(mContext)) {
                                Toast.makeText(buttonView.getContext().getApplicationContext(), getResources().getString(R.string.internet_connectivity), Toast.LENGTH_SHORT).show();
                            }
                            if (isChecked) {
                                //add the alarmid to the list of active alarms
                                Log.d("Switch", "alarm turned on for " + holder.textName.getText());
                                for (int slot = 0; slot < model.getReminders().size(); slot++) {
                                    Intent mIntent = new Intent(mContext, AlarmReceiver.class);
                                    mIntent.putExtra("alarm_id", model.getAlarmIds().get(slot));
                                    mIntent.putExtra("dailyRoutSlot", slot);
                                    Utilities.setExtraForIntent(mIntent, "dailyRoutRaw", dailyRoutineObj);
                                    int h = Integer.parseInt(model.getReminders().get(slot).split(":")[0]);
                                    int m = Integer.parseInt(model.getReminders().get(slot).split(":")[1]);
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.set(Calendar.HOUR_OF_DAY, h);
                                    calendar.set(Calendar.MINUTE, m);
                                    calendar.set(Calendar.SECOND, 0);
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, model.getAlarmIds().get(slot), mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                                    Log.d("AlarmService", "Setting alarm, Alarm Id: " + model.getAlarmIds().get(slot) + " Slot: " + slot);

                                    //edit the alarmStatus in Firestore collection
                                    db.collection("alarms/" + app.getAppUser(null).getId() + "/daily_routine/").document(model.getName()).update("alarmStatus", true);
                                }
                            } else {
                                //remove all alarms associated with these ids
                                alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
                                for (int slot = 0; slot < model.getAlarmIds().size(); slot++) {
                                    Intent mIntent = new Intent(mContext, AlarmReceiver.class);
                                    mIntent.putExtra("alarm_id", model.getAlarmIds().get(slot));
                                    mIntent.putExtra("dailyRoutSlot", slot);
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, model.getAlarmIds().get(slot), mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                    alarmManager.cancel(pendingIntent);
                                    Utilities.removeFromTimersList(model.getAlarmIds().get(slot));
                                }
                                Log.d("Switch", "alarm turned off for " + holder.textName.getText());
                                db.collection("alarms/" + app.getAppUser(null).getId() + "/daily_routine/").document(model.getName()).update("alarmStatus", false);
                            }
                        }
                    });
                }

//            private String getIdFromName(String name){
//             return db.collection("alarms/" + app.getAppUser(null).getId() + "/daily_routine/").document(name).getId();
//            }

                @Override
                public ActivityHolder onCreateViewHolder(ViewGroup group, int i) {
                    View view = LayoutInflater.from(group.getContext())
                            .inflate(R.layout.item_contact, group, false);

                    return new ActivityHolder(view);
                }

                @Override
                public void onError(FirebaseFirestoreException e) {
                    Log.e("error", e.getMessage());
                }

                @Override
                public int getItemCount() {
                    return super.getItemCount();
                }
            };

        adapter.notifyDataSetChanged();
        activityList.setAdapter(adapter);
    }

    public static class ActivityHolder extends RecyclerView.ViewHolder {
        TextView textName, taskId;
        TextView alarm_time1, alarm_time2, alarm_time3;
        ImageView imageView;
        Switch alarmStatus;

        public ActivityHolder(View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.activity_name);
            taskId = itemView.findViewById(R.id.taskId);
            alarm_time1 = itemView.findViewById(R.id.alarm_time1);
            alarm_time2 = itemView.findViewById(R.id.alarm_time2);
            alarm_time3 = itemView.findViewById(R.id.alarm_time3);
            imageView = itemView.findViewById(R.id.activityImage);
            alarmStatus = itemView.findViewById(R.id.alarm_switch);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}