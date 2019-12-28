package com.zuccessful.trueharmony.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.Patient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ActivityStatsFrag extends Fragment {
    private SakshamApp app;
    private FirebaseFirestore db;
    private Patient patient;
    private final String TAG = "ACTIVITYTAG";
    private BarChart barChart;
    private HashMap<String, HashMap> dietHmap = new HashMap<>();

    public ActivityStatsFrag() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_diet_stats, container, false);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        barChart = (BarChart) view.findViewById(R.id.stacked_bar);
        app = SakshamApp.getInstance();
        db = app.getFirebaseDatabaseInstance();
        patient = app.getAppUser(null);

        fetchDates(new FireStoreCallBack() {
            @Override
            public void onCallBack(ArrayList<String> dietDatesList) {
                Log.d(TAG, "onCallBack: " + dietDatesList.toString());
                fetchDietRecords(dietDatesList, new FireStoreCallBack2() {
                    @Override
                    public void onCallBack(HashMap<String, HashMap> dietHmap) {
                        makeChart(dietHmap);
                    }
                });
            }
        });
        return view;
    }

    private void makeChart(HashMap<String, HashMap> dietHmap) {
        barChart.clear();
        ArrayList<BarEntry> barGroupBalanced = new ArrayList<>();
        ArrayList<BarEntry> barGroupNotBalanced= new ArrayList<>();
        String[] dates = new String[100];
        int dateVal = 0;
        //Log.d(TAG, "makeChart: " + dietHmap.toString());
        for (Map.Entry element : dietHmap.entrySet()) {
            int balanced = 0;
            int notBalanced = 0;
            String date = (String) element.getKey();
            Log.d("ActivityStatsFrag", "makeChart: Keys fetched " + date);
            dates[dateVal]=date;
            HashMap<String, Object> activities = (HashMap<String, Object>) element.getValue();
            for (Map.Entry element2 : activities.entrySet()) {
                String meal = (String) element2.getKey();
                Object foodListObj = element2.getValue();
                //Log.d(TAG, "CHECK " + meal + ":" + foodListObj);
                // Have the date, meal type and food list: create chart
                List list = ((List) foodListObj);
                boolean isBalanced = true;
                for (Object item : list) {
                    Log.d(TAG, "makeChart00: " + item.toString());
                    if (item.toString().equals("Others")) {
                        isBalanced = false;
                        break;
                    }
                }
                if (isBalanced)
                    balanced++;
                else
                   notBalanced++;
            }
            Log.d(TAG, "makeChart: "+date+" "+balanced+" "+notBalanced);
            barGroupBalanced.add(new BarEntry(dateVal,balanced));
            barGroupNotBalanced.add(new BarEntry(dateVal,notBalanced));
            dateVal++;
        }
        BarDataSet barDataSet1 = new BarDataSet(barGroupBalanced,"Done");
        barDataSet1.setColor(Color.GREEN);
        BarDataSet barDataSet2 = new BarDataSet(barGroupNotBalanced,"Missed");
        barDataSet2.setColor(Color.RED);

        BarData data = new BarData(barDataSet1,barDataSet2);
        barChart.setData(data);

        XAxis xAxis =  barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);

        barChart.setDragEnabled(true);
        barChart.setVisibleXRangeMaximum(7);

        float barSpace = 0.1f;
        float groupSpace = 0.5f;
        data.setBarWidth(0.15f);
        barChart.animateY(2500);
        barChart.getXAxis().setAxisMinimum(0);
        barChart.groupBars(0,groupSpace,barSpace);
        barChart.notifyDataSetChanged();

    }


    /*private ArrayList<BarEntry> dataVals1 () {
        ArrayList<BarEntry> dataVals = new ArrayList<>();
        dataVals.add(new BarEntry(0,new float[]{2,5.5f,4}));
        dataVals.add(new BarEntry(1,new float[]{2,5.5f,4}));
        dataVals.add(new BarEntry(2,new float[]{2,5.5f,4}));
        return dataVals;
    }*/
    private void fetchDates(final FireStoreCallBack fireStoreCallBack) {
        final ArrayList<String> dietDatesList = new ArrayList<>();
        db.collection("patient_dr_dates/").document(app.getPatientID()).
                collection("dates").document("dates").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot;
                if (task.isSuccessful())
                {
                    documentSnapshot = task.getResult();
                    try
                    {
                        dietDatesList.addAll(documentSnapshot.getData().keySet());
                        fireStoreCallBack.onCallBack(dietDatesList);
                    }
                    catch (Exception e){
                        Log.d(TAG, "onComplete: "+e.getMessage());
                    }
                }
            }
        });
    }
    private interface FireStoreCallBack
    {
        void onCallBack(ArrayList<String> dietDatesList);
    }
    private interface FireStoreCallBack2
    {
        void onCallBack(HashMap<String, HashMap> dietHmap);
    }

    private void fetchDietRecords(ArrayList<String> dietDatesList, final FireStoreCallBack2 fireStoreCallBack2)
    {

        for (final String date : dietDatesList)
        {
            CollectionReference collectionReference = db.collection("patient_daily_rout_logs").document(app.getPatientID()).collection(date);
            collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful())
                    {
                        HashMap<String,Object> tempMap  = new HashMap<>();
                        for (DocumentSnapshot documentSnapshot : task.getResult())
                        {
                            Log.d(TAG, "onComplete dietDatesList: "+documentSnapshot.getId());
                            tempMap.put(documentSnapshot.getId(),documentSnapshot.get("details"));
                            Log.d(TAG, "MAP0:"+tempMap.toString());
                        }
                        dietHmap.put(date,tempMap);
                        Log.d(TAG, "MAP1:"+dietHmap.toString());
                        fireStoreCallBack2.onCallBack(dietHmap);
                    }
                }
            });
        }
    }


}
