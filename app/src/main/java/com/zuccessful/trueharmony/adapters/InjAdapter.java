package com.zuccessful.trueharmony.adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.activities.AddInjActivity;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.Injection;
import com.zuccessful.trueharmony.receivers.ReminderReceiver;

import java.util.ArrayList;

public class InjAdapter extends RecyclerView.Adapter<InjAdapter.InjViewHolder> {

    private ArrayList<Injection> iInjs;
    private final String TAG = "INJADAPTER_LOG";
    public Context context;
    public SakshamApp app;
    public InjAdapter() {
        iInjs = new ArrayList<>();
    }

    public InjAdapter(ArrayList<Injection> iInjs) {
        this.iInjs = iInjs;
        app = SakshamApp.getInstance();
        context = app.getApplicationContext();
    }

    @NonNull
    @Override
    public InjAdapter.InjViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.inj_reminder_list_item, parent, false);
        return new InjAdapter.InjViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InjAdapter.InjViewHolder holder, int position) {
        Injection inj = iInjs.get(position);
        holder.iItem = inj;
        String name = inj.getName();
        String times = inj.getRepeated();
        String lastDate = inj.getDay()+"/"+inj.getMonth()+"/"+inj.getYear();
        holder.iNameView.setText(name);
        holder.iTimesView.setText(context.getResources().getString(R.string.repeat)+" "+times+"\n"+ context.getResources().getString(R.string.last_date)+":"+lastDate);
        //holder.iTimesView.setText(getRemindersFormatted(inj.getReminders()));
    }

    private String getRemindersFormatted(ArrayList<String> reminders) {
        StringBuilder a = new StringBuilder();
        for (String time : reminders) {
            a.append(time).append(", ");
        }
        return a.toString().substring(0, a.length() - 2);
    }

    @Override
    public int getItemCount() {
        return iInjs.size();
    }

    public void updateInjs(ArrayList<Injection> injList) {
        iInjs.addAll(injList);
        notifyDataSetChanged();
    }

    public void replaceInjs(ArrayList<Injection> injList) {
        iInjs = injList;
        notifyDataSetChanged();
    }

    public void removeInjs(Injection inj) {
        iInjs.remove(inj);
        notifyDataSetChanged();
    }

    public ArrayList<Injection> getInjs() {
        return iInjs;
    }


    class InjViewHolder extends RecyclerView.ViewHolder {

        TextView iNameView;
        TextView iTimesView;
        Injection iItem;
        ImageView iDeleteIcon;
        ImageView leftIvEdit;

        InjViewHolder(final View itemView) {
            super(itemView);
            iNameView = itemView.findViewById(R.id.inj_item_name);
            iTimesView = itemView.findViewById(R.id.inj_item_times);
            iDeleteIcon = itemView.findViewById(R.id.deleteIv);
            leftIvEdit = itemView.findViewById(R.id.leftIv);

            leftIvEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = itemView.getContext();
                    Intent intent = new Intent(context, AddInjActivity.class);
                    intent.putExtra("editName",iItem.getName());
                    context.startActivity(intent);

                }
            });

            iDeleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*AlarmManager alarmManager = (AlarmManager)
                            v.getContext().getSystemService(Context.ALARM_SERVICE);
                    Intent myIntent = new Intent(v.getContext(),
                            AlarmReceiver.class);

                    final ArrayList<Integer> alarmIds = iItem.getAlarmIds();

                    for(int id : alarmIds) {
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                v.getContext(), id, myIntent, 0);
                        alarmManager.cancel(pendingIntent);
                    }
*/


                    // delete the notifiocation
                    Context context = itemView.getContext();
                    Injection inj = new Injection(context);
                    Intent intent = new Intent(context, ReminderReceiver.class);
                    PendingIntent sender = PendingIntent.getBroadcast(context, Integer.parseInt(iItem.getReqCode()), intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    alarmManager.cancel(sender);

                    SakshamApp.getInstance().getFirebaseDatabaseInstance().collection("alarms/" +
                            SakshamApp.getInstance().getAppUser(null).getId() +
                            "/injection").document(iItem.getName()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(SakshamApp.getInstance().getApplicationContext(),
                                    "Deleted "
                                            +iItem.getName()+" successfully.",Toast.LENGTH_SHORT).show();
                            removeInjs(iItem);
                        }

                    });
                }
            });

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(v.getContext(),"medClicked : " +iItem.getName()
//                    ,Toast.LENGTH_SHORT).show();
//                    Intent editMedIntent = new Intent(v.getContext(), AddMedRecActivity.class);
//                    editMedIntent.putExtra(Constants.MED_OBJ,iItem);
//                    editMedIntent.putExtra(Constants.CALLED_FROM,Constants.MED_ADAPTER);
//
//                    v.getContext().startActivity(editMedIntent);
//
//                }
//            });
        }
    }

}
