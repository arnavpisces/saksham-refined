package com.zuccessful.trueharmony.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.adapters.FoodListAdapter2;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.Activity_Usage;
import com.zuccessful.trueharmony.utilities.Constants;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SummaryActivity extends AppCompatActivity {

    private SakshamApp app = SakshamApp.getInstance();
    private FirebaseFirestore db = app.getFirebaseDatabaseInstance();
    private RecyclerView recyclerViewBreakfast;
    private RecyclerView recyclerViewLunch;
    private Context context;
    private RecyclerView recyclerDinner;
    private RecyclerView recyclerSnack1;
    private RecyclerView recyclerSnack2;
    private ArrayList<String> breakFastList = new ArrayList<>();
    private ArrayList<String> lunchList = new ArrayList<>();
    private ArrayList<String> dinnerList = new ArrayList<>();
    private ArrayList<String> snack1List = new ArrayList<>();
    private ArrayList<String> snack2List = new ArrayList<>();
    private FoodListAdapter2 breakFastAdapter;
    private FoodListAdapter2 lunchAdapter;
    private FoodListAdapter2 dinnerAdapter;
    private FoodListAdapter2 snack1Adapter;
    private FoodListAdapter2 snack2Adapter;
    private final String TAG = "SUMMARYTAG";
    private TextView textView5, textView6;
    long startTime;
    long endTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        getSupportActionBar().setTitle(R.string.summary); // for set actionbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        startTime = System.currentTimeMillis();
        setContentView(R.layout.activity_summary);
        recyclerViewBreakfast = findViewById(R.id.recycler_view_breakfast);
        recyclerViewLunch = findViewById(R.id.recycler_view_lunch);
        recyclerDinner = findViewById(R.id.recycler_view_dinner);
        recyclerSnack1 = findViewById(R.id.recycler_view_snack1);
        recyclerSnack2 = findViewById(R.id.recycler_view_snack2);
        breakFastAdapter = new FoodListAdapter2(breakFastList);
        textView5 = findViewById(R.id.textView5);
        textView6 = findViewById(R.id.textView6);
        textView5.setText(getResources().getStringArray(R.array.meals_pref_arrays)[0]);
        textView6.setText(getResources().getStringArray(R.array.meals_pref_arrays)[1]);
        lunchAdapter = new FoodListAdapter2(lunchList);
        dinnerAdapter = new FoodListAdapter2(dinnerList);
        snack1Adapter = new FoodListAdapter2(snack1List);
        snack2Adapter = new FoodListAdapter2(snack2List);
        //
        LinearLayoutManager horizontalLayout = new LinearLayoutManager(SummaryActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewBreakfast.setLayoutManager(horizontalLayout);
        recyclerViewBreakfast.setItemAnimator(new DefaultItemAnimator());
        recyclerViewBreakfast.setAdapter(breakFastAdapter);
        //
        LinearLayoutManager horizontalLayout2 = new LinearLayoutManager(SummaryActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewLunch.setLayoutManager(horizontalLayout2);
        recyclerViewLunch.setItemAnimator(new DefaultItemAnimator());
        recyclerViewLunch.setAdapter(lunchAdapter);
        //
        LinearLayoutManager horizontalLayout3 = new LinearLayoutManager(SummaryActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerDinner.setLayoutManager(horizontalLayout3);
        recyclerDinner.setItemAnimator(new DefaultItemAnimator());
        recyclerDinner.setAdapter(dinnerAdapter);

        LinearLayoutManager horizontalLayout4 = new LinearLayoutManager(SummaryActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerSnack1.setLayoutManager(horizontalLayout4);
        recyclerSnack1.setItemAnimator(new DefaultItemAnimator());
        recyclerSnack1.setAdapter(snack1Adapter);

        LinearLayoutManager horizontalLayout5 = new LinearLayoutManager(SummaryActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerSnack2.setLayoutManager(horizontalLayout5);
        recyclerSnack2.setItemAnimator(new DefaultItemAnimator());
        recyclerSnack2.setAdapter(snack2Adapter);
        getFoodList();
    }




    private interface FireStoreCallBack
    {
        void onCallBack(ArrayList<String> callinjectionDatesList);
    }

    private void getFoodList() {
        SimpleDateFormat sdf2 = Utilities.getSimpleDateFormat();
        String date = sdf2.format(new Date());

        if(Utilities.isInternetOn(context)) {
            DocumentReference documentReference = db.collection("patient_diet_logs").document(app.getPatientID()).collection(date).document(this.getResources().getString(R.string.breakfast));

            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        List<Object> temp = new ArrayList<>(documentSnapshot.getData().values());
                        ArrayList<Object> itemList = new ArrayList((Collection) temp.get(0));

                        for (Object item : itemList) {
                            breakFastList.add((String) item);
                        }

                        Log.d(TAG, "onComplete: " + documentSnapshot.getData().values().toString());
                        breakFastAdapter.notifyDataSetChanged();
                    }
                }
            });


            DocumentReference documentReference2 = db.collection("patient_diet_logs").document(app.getPatientID()).collection(date).document(this.getResources().getString(R.string.lunch));

            documentReference2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        List<Object> temp = new ArrayList<>(documentSnapshot.getData().values());
                        ArrayList<Object> itemList = new ArrayList((Collection) temp.get(0));
                        for (Object item : itemList) {
                            lunchList.add((String) item);
                        }

                        Log.d(TAG, "onComplete: " + documentSnapshot.getData().values().toString());
                        lunchAdapter.notifyDataSetChanged();
                    }

                }
            });


            DocumentReference documentReference3 = db.collection("patient_diet_logs").document(app.getPatientID()).collection(date).document(this.getResources().getString(R.string.dinner));

            documentReference3.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        List<Object> temp = new ArrayList<>(documentSnapshot.getData().values());
                        ArrayList<Object> itemList = new ArrayList((Collection) temp.get(0));
                        for (Object item : itemList) {
                            dinnerList.add((String) item);
                        }

                        Log.d(TAG, "onComplete: " + documentSnapshot.getData().values().toString());
                        dinnerAdapter.notifyDataSetChanged();
                    }

                }
            });

            DocumentReference documentReference4 = db.collection("patient_diet_logs").document(app.getPatientID()).collection(date).document(this.getResources().getStringArray(R.array.meals_pref_arrays)[0]);

            documentReference4.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        List<Object> temp = new ArrayList<>(documentSnapshot.getData().values());
                        ArrayList<Object> itemList = new ArrayList((Collection) temp.get(0));
                        for (Object item : itemList) {
                            snack1List.add((String) item);
                        }

                        Log.d(TAG, "onComplete: " + documentSnapshot.getData().values().toString());
                        snack1Adapter.notifyDataSetChanged();
                    }

                }
            });

            DocumentReference documentReference5 = db.collection("patient_diet_logs").document(app.getPatientID()).collection(date).document(context.getResources().getStringArray(R.array.meals_pref_arrays)[1]);

            documentReference5.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        List<Object> temp = new ArrayList<>(documentSnapshot.getData().values());
                        ArrayList<Object> itemList = new ArrayList((Collection) temp.get(0));
                        for (Object item : itemList) {
                            snack2List.add((String) item);
                        }

                        Log.d(TAG, "onComplete: " + documentSnapshot.getData().values().toString());
                        snack2Adapter.notifyDataSetChanged();
                    }

                }
            });
        }else {
            ArrayList<String> breakfast = Utilities.getListFromSharedPref(this.getResources().getString(R.string.breakfast));
            for (String item : breakfast) {
                breakFastList.add(item);
            }
            breakFastAdapter.notifyDataSetChanged();
            ArrayList<String> lunch = Utilities.getListFromSharedPref(this.getResources().getString(R.string.lunch));
            for (String item : lunch) {
                lunchList.add(item);
            }
            lunchAdapter.notifyDataSetChanged();
            ArrayList<String> dinner = Utilities.getListFromSharedPref(this.getResources().getString(R.string.dinner));
            for (String item : dinner) {
                dinnerList.add(item);
            }
            dinnerAdapter.notifyDataSetChanged();
            SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs",context.MODE_PRIVATE);
            int numMeals = (sharedPreferences.getInt("num_meals", 3));
            if(numMeals>3) {
                ArrayList<String> snacks1 = Utilities.getListFromSharedPref(this.getResources().getStringArray(R.array.meals_pref_arrays)[0]);
                for (String item : snacks1) {
                    snack1List.add(item);
                }
                snack1Adapter.notifyDataSetChanged();
                ArrayList<String> snacks2 = Utilities.getListFromSharedPref(this.getResources().getStringArray(R.array.meals_pref_arrays)[1]);
                for (String item : snacks2) {
                    snack2List.add(item);
                }
                snack2Adapter.notifyDataSetChanged();
            }
        }
    }

    private interface  FirestroreCallBack{
        void onCallBack(Long time);
    }

    protected void fetchObject(final FirestroreCallBack firestroreCallBack){



        SimpleDateFormat sdf = Utilities.getSimpleDateFormat();

        final DocumentReference documentReference;
        documentReference = db.collection("time_spent/")
                .document(app.getAppUser(null)
                        .getId())
                .collection(sdf.format(new Date())).document("Diet Summary");

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot=task.getResult();
                    Activity_Usage activity_usage = documentSnapshot.toObject(Activity_Usage.class);
                    if(activity_usage==null){
                        long time = 0;
                        firestroreCallBack.onCallBack(time);
                        return;
                    }
                    Log.d(TAG, "oldtime on database : " + Long.toString(activity_usage.getTime()));
                    firestroreCallBack.onCallBack(activity_usage.getTime());

                }else{
                    Log.d(TAG,"Error: Can't get Activity_Usage",task.getException());
                }

            }

        });

    }
    @Override
    protected void onStop()
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
                Activity_Usage au = new Activity_Usage(hms,totaltime,"Diet Summary");
                SimpleDateFormat sdf = Utilities.getSimpleDateFormat();

                //add to database
                try {db.collection("time_spent/")
                        .document(app.getAppUser(null)
                                .getId())
                        .collection(sdf.format(new Date()))
                        .document("Diet Summary")
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }}
}
//package com.zuccessful.trueharmony.activities;
//
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.DefaultItemAnimator;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.MenuItem;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.firestore.DocumentReference;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.SetOptions;
//import com.zuccessful.trueharmony.R;
//import com.zuccessful.trueharmony.adapters.FoodListAdapter2;
//import com.zuccessful.trueharmony.application.SakshamApp;
//import com.zuccessful.trueharmony.pojo.Activity_Usage;
//import com.zuccessful.trueharmony.utilities.Utilities;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.TimeUnit;
//
//public class SummaryActivity extends AppCompatActivity {
//
//    private SakshamApp app = SakshamApp.getInstance();
//    private FirebaseFirestore db = app.getFirebaseDatabaseInstance();
//    private RecyclerView recyclerViewBreakfast;
//    private RecyclerView recyclerViewLunch;
//    private RecyclerView recyclerDinner;
//    private RecyclerView recyclerSnack1;
//    private RecyclerView recyclerSnack2;
//    private ArrayList<String> breakFastList = new ArrayList<>();
//    private ArrayList<String> lunchList = new ArrayList<>();
//    private ArrayList<String> dinnerList = new ArrayList<>();
//    private ArrayList<String> snack1List = new ArrayList<>();
//    private ArrayList<String> snack2List = new ArrayList<>();
//    private FoodListAdapter2 breakFastAdapter;
//    private FoodListAdapter2 lunchAdapter;
//    private FoodListAdapter2 dinnerAdapter;
//    private FoodListAdapter2 snack1Adapter;
//    private FoodListAdapter2 snack2Adapter;
//    private final String TAG = "SUMMARYTAG";
//    long startTime;
//    long endTime;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        getSupportActionBar().setTitle("Diet Summary"); // for set actionbar title
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        startTime = System.currentTimeMillis();
//        setContentView(R.layout.activity_summary);
//        recyclerViewBreakfast = findViewById(R.id.recycler_view_breakfast);
//        recyclerViewLunch = findViewById(R.id.recycler_view_lunch);
//        recyclerDinner = findViewById(R.id.recycler_view_dinner);
//        recyclerSnack1 = findViewById(R.id.recycler_view_snack1);
//        recyclerSnack2 = findViewById(R.id.recycler_view_snack2);
//        breakFastAdapter = new FoodListAdapter2(breakFastList);
//        lunchAdapter = new FoodListAdapter2(lunchList);
//        dinnerAdapter = new FoodListAdapter2(dinnerList);
//        snack1Adapter = new FoodListAdapter2(snack1List);
//        snack2Adapter = new FoodListAdapter2(snack2List);
//        //
//        LinearLayoutManager horizontalLayout = new LinearLayoutManager(SummaryActivity.this, LinearLayoutManager.HORIZONTAL, false);
//        recyclerViewBreakfast.setLayoutManager(horizontalLayout);
//        recyclerViewBreakfast.setItemAnimator(new DefaultItemAnimator());
//        recyclerViewBreakfast.setAdapter(breakFastAdapter);
//        //
//        LinearLayoutManager horizontalLayout2 = new LinearLayoutManager(SummaryActivity.this, LinearLayoutManager.HORIZONTAL, false);
//        recyclerViewLunch.setLayoutManager(horizontalLayout2);
//        recyclerViewLunch.setItemAnimator(new DefaultItemAnimator());
//        recyclerViewLunch.setAdapter(lunchAdapter);
//        //
//        LinearLayoutManager horizontalLayout3 = new LinearLayoutManager(SummaryActivity.this, LinearLayoutManager.HORIZONTAL, false);
//        recyclerDinner.setLayoutManager(horizontalLayout3);
//        recyclerDinner.setItemAnimator(new DefaultItemAnimator());
//        recyclerDinner.setAdapter(dinnerAdapter);
//
//        LinearLayoutManager horizontalLayout4 = new LinearLayoutManager(SummaryActivity.this, LinearLayoutManager.HORIZONTAL, false);
//        recyclerSnack1.setLayoutManager(horizontalLayout4);
//        recyclerSnack1.setItemAnimator(new DefaultItemAnimator());
//        recyclerSnack1.setAdapter(snack1Adapter);
//
//        LinearLayoutManager horizontalLayout5 = new LinearLayoutManager(SummaryActivity.this, LinearLayoutManager.HORIZONTAL, false);
//        recyclerSnack2.setLayoutManager(horizontalLayout5);
//        recyclerSnack2.setItemAnimator(new DefaultItemAnimator());
//        recyclerSnack2.setAdapter(snack2Adapter);
//        getFoodList();
//    }
//
//
//
//
//    private interface FireStoreCallBack
//    {
//        void onCallBack(ArrayList<String> callinjectionDatesList);
//    }
//
//    private void getFoodList() {
//        SimpleDateFormat sdf2 = Utilities.getSimpleDateFormat();
//        String date = sdf2.format(new Date());
//
//
//        DocumentReference documentReference = db.collection("patient_diet_logs").document(app.getPatientID()).collection(date).document("Breakfast");
//
//            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    DocumentSnapshot documentSnapshot = task.getResult();
//                    if (documentSnapshot.exists())
//                    {
//                        List<Object> temp = new ArrayList<>(documentSnapshot.getData().values());
//                        ArrayList<Object> itemList = new ArrayList((Collection) temp.get(0));
//
//                        for (Object item : itemList)
//                        {
//                            breakFastList.add((String) item);
//                        }
//
//                        Log.d(TAG, "onComplete: "+documentSnapshot.getData().values().toString());
//                        breakFastAdapter.notifyDataSetChanged();
//                    }
//                }
//            });
//
//
//        DocumentReference documentReference2 = db.collection("patient_diet_logs").document(app.getPatientID()).collection(date).document("Lunch");
//
//            documentReference2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    DocumentSnapshot documentSnapshot = task.getResult();
//                    if (documentSnapshot.exists())
//                    {
//                        List<Object> temp = new ArrayList<>(documentSnapshot.getData().values());
//                        ArrayList<Object> itemList = new ArrayList((Collection) temp.get(0));
//                        for (Object item : itemList)
//                        {
//                            lunchList.add((String) item);
//                        }
//
//                        Log.d(TAG, "onComplete: "+documentSnapshot.getData().values().toString());
//                        lunchAdapter.notifyDataSetChanged();
//                    }
//
//                }
//            });
//
//
//
//        DocumentReference documentReference3 = db.collection("patient_diet_logs").document(app.getPatientID()).collection(date).document("Dinner");
//
//            documentReference3.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    DocumentSnapshot documentSnapshot = task.getResult();
//                    if (documentSnapshot.exists())
//                    {
//                        List<Object> temp = new ArrayList<>(documentSnapshot.getData().values());
//                        ArrayList<Object> itemList = new ArrayList((Collection) temp.get(0));
//                        for (Object item : itemList)
//                        {
//                            dinnerList.add((String) item);
//                        }
//
//                        Log.d(TAG, "onComplete: "+documentSnapshot.getData().values().toString());
//                        dinnerAdapter.notifyDataSetChanged();
//                    }
//
//                }
//            });
//
//        DocumentReference documentReference4 = db.collection("patient_diet_logs").document(app.getPatientID()).collection(date).document("Snack1");
//
//        documentReference4.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                DocumentSnapshot documentSnapshot = task.getResult();
//                if (documentSnapshot.exists())
//                {
//                    List<Object> temp = new ArrayList<>(documentSnapshot.getData().values());
//                    ArrayList<Object> itemList = new ArrayList((Collection) temp.get(0));
//                    for (Object item : itemList)
//                    {
//                        snack1List.add((String) item);
//                    }
//
//                    Log.d(TAG, "onComplete: "+documentSnapshot.getData().values().toString());
//                    snack1Adapter.notifyDataSetChanged();
//                }
//
//            }
//        });
//
//        DocumentReference documentReference5 = db.collection("patient_diet_logs").document(app.getPatientID()).collection(date).document("Snack2");
//
//        documentReference5.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                DocumentSnapshot documentSnapshot = task.getResult();
//                if (documentSnapshot.exists())
//                {
//                    List<Object> temp = new ArrayList<>(documentSnapshot.getData().values());
//                    ArrayList<Object> itemList = new ArrayList((Collection) temp.get(0));
//                    for (Object item : itemList)
//                    {
//                        snack2List.add((String) item);
//                    }
//
//                    Log.d(TAG, "onComplete: "+documentSnapshot.getData().values().toString());
//                    snack2Adapter.notifyDataSetChanged();
//                }
//
//            }
//        });
//
//
//    }
//
//    private interface  FirestroreCallBack{
//        void onCallBack(Long time);
//    }
//
//    protected void fetchObject(final FirestroreCallBack firestroreCallBack){
//
//
//
//        SimpleDateFormat sdf = Utilities.getSimpleDateFormat();
//
//        final DocumentReference documentReference;
//        documentReference = db.collection("time_spent/")
//                .document(app.getAppUser(null)
//                        .getId())
//                .collection(sdf.format(new Date())).document("Diet Summary");
//
//        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()){
//                    DocumentSnapshot documentSnapshot=task.getResult();
//                    Activity_Usage activity_usage = documentSnapshot.toObject(Activity_Usage.class);
//                    if(activity_usage==null){
//                        long time = 0;
//                        firestroreCallBack.onCallBack(time);
//                        return;
//                    }
//                    Log.d(TAG, "oldtime on database : " + Long.toString(activity_usage.getTime()));
//                    firestroreCallBack.onCallBack(activity_usage.getTime());
//
//                }else{
//                    Log.d(TAG,"Error: Can't get Activity_Usage",task.getException());
//                }
//
//            }
//
//        });
//
//    }
//    @Override
//    protected void onStop()
//    {
//        endTime = System.currentTimeMillis();
//        final long timeSpend = endTime - startTime;
//        //update time
//        fetchObject(new FirestroreCallBack() {
//            @Override
//            public void onCallBack(Long totaltime) {
//                totaltime += timeSpend;
//                //change format of time
//                String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(totaltime),
//                        TimeUnit.MILLISECONDS.toMinutes(totaltime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(totaltime)),
//                        TimeUnit.MILLISECONDS.toSeconds(totaltime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totaltime)));
//
//
//                //Log.d(TAG, "OLDTIME " + Long.toString(oldtimespend));
//
//                //create object
//                Activity_Usage au = new Activity_Usage(hms,totaltime,"Diet Summary");
//                SimpleDateFormat sdf = Utilities.getSimpleDateFormat();
//
//                //add to database
//                try {db.collection("time_spent/")
//                        .document(app.getAppUser(null)
//                                .getId())
//                        .collection(sdf.format(new Date()))
//                        .document("Diet Summary")
//                        .set(au);
//                    Map<String, Object> today_date = new HashMap<>();
//                    SimpleDateFormat sdf2 = Utilities.getSimpleDateFormat();
//                    today_date.put(sdf2.format(new Date()), sdf2.format(new Date()));
//                    db.collection("time_dates/"+app.getPatientID()+"/dates").document("dates").set(today_date, SetOptions.merge());
//                    Map<String, Object> module_name = new HashMap<>();
//                    module_name.put(au.getActivity_name(), au.getActivity_name());
//                    db.collection("time_dates/"+app.getPatientID()+"/dates").document("module").set(module_name, SetOptions.merge());
//                } catch (Exception e) { Log.d(TAG ,"ERROR : can't get object", e); }
//
//            }
//        });
//
//
//
//
//
//        super.onStop();
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                onBackPressed();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }}
//}
