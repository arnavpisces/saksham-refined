package com.zuccessful.trueharmony.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.activities.SummaryActivity;
import com.zuccessful.trueharmony.adapters.MealsAdapter;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.Activity_Usage;
import com.zuccessful.trueharmony.utilities.Constants;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DietModuleFragment extends Fragment {
    private SakshamApp app = SakshamApp.getInstance();
    private FirebaseFirestore db = app.getFirebaseDatabaseInstance();
    private MealsAdapter adapter;
    private Context iContext;
    private List<String> mealList = new ArrayList<>();
    private Button summaryButton;
    long startTime;
    long endTime;
    private final String TAG = "DietFrag";
    public DietModuleFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startTime = System.currentTimeMillis();
        iContext = getContext();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_diet_module, container, false);
        SharedPreferences sharedPreferences = iContext.getSharedPreferences("MyPrefs",iContext.MODE_PRIVATE);
        int numMeals = (sharedPreferences.getInt("num_meals", 3));
        Log.d("DietModuleFragment", "onCreateView: MealCount"+ numMeals);
        summaryButton = (Button) view.findViewById(R.id.summaryButton);
        summaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewSummary();
            }
        });
        //recycler view
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_meals);
        adapter = new MealsAdapter(mealList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        getMeals(numMeals);
        return  view;
    }

    private void viewSummary() {
        Intent intent = new Intent(getActivity(), SummaryActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void getMeals(int numMeals)
    {
       mealList.add(getResources().getString(R.string.breakfast));
       mealList.add(getResources().getString(R.string.lunch));
       mealList.add(getResources().getString(R.string.dinner));
       if (numMeals>3)
       {
           //fetch the list of meals from SharedPreferences
           ArrayList<String> moreMeals = Utilities.getListFromSharedPref(Constants.KEY_MEALS_LIST);
           for (String meal: moreMeals){
               mealList.add(meal);
           }
       }
       adapter.notifyDataSetChanged();
    }

    private interface  FirestroreCallBack{
        void onCallBack(Long time);
    }

    protected void fetchObject(final FirestroreCallBack firestroreCallBack){

        SimpleDateFormat sdf = Utilities.getSimpleDateFormat();

        if(Utilities.isInternetOn(iContext)) {
            final DocumentReference documentReference;
            documentReference = db.collection("time_spent/")
                    .document(app.getAppUser(null).getId())
                    .collection(sdf.format(new Date())).document("Diet Fragment");

            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        Activity_Usage activity_usage = documentSnapshot.toObject(Activity_Usage.class);
                        if (activity_usage == null) {
                            long time = 0;
                            firestroreCallBack.onCallBack(time);
                            return;
                        }
                        Log.d(TAG, "oldtime on database : " + Long.toString(activity_usage.getTime()));
                        firestroreCallBack.onCallBack(activity_usage.getTime());

                    } else {
                        Log.d(TAG, "Error: Can't get Activity_Usage", task.getException());
                    }

                }

            });
        }
    }
    @Override
    public void onStop()
    {
        endTime = System.currentTimeMillis();
        final long timeSpend = endTime - startTime;
        //update time
        fetchObject(new FirestroreCallBack() {
            @Override
            public void onCallBack(Long totaltime) {
                totaltime += timeSpend;
                //change format of time
                String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(totaltime),
                        TimeUnit.MILLISECONDS.toMinutes(totaltime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(totaltime)),
                        TimeUnit.MILLISECONDS.toSeconds(totaltime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totaltime)));


                //Log.d(TAG, "OLDTIME " + Long.toString(oldtimespend));

                //create object
                Activity_Usage au = new Activity_Usage(hms,totaltime,"Diet Fragment");
                SimpleDateFormat sdf = Utilities.getSimpleDateFormat();

                //add to database
                try {db.collection("time_spent/")
                        .document(app.getAppUser(null)
                                .getId())
                        .collection(sdf.format(new Date()))
                        .document("Diet Fragment")
                        .set(au);
                    Map<String, Object> today_date = new HashMap<>();
                    SimpleDateFormat sdf2 = Utilities.getSimpleDateFormat();
                    today_date.put(sdf2.format(new Date()), sdf2.format(new Date()));
                    db.collection("time_dates/"+app.getPatientID()+"/dates").document("dates").set(today_date, SetOptions.merge());
                    Map<String, Object> module_name = new HashMap<>();
                    module_name.put(au.getActivity_name(), au.getActivity_name());
                    db.collection("time_dates/"+app.getPatientID()+"/dates").document("module").set(module_name, SetOptions.merge());
                } catch (Exception e) { Log.d(TAG ,"ERROR : can't get object", e); }

            }
        });





        super.onStop();
    }



}
