package com.zuccessful.trueharmony.fragments;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.Activity_Usage;
import com.zuccessful.trueharmony.pojo.Patient;
import com.zuccessful.trueharmony.utilities.AxisDateValueFormatter;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class StatsFragment extends Fragment {

    private static final String TAG = StatsFragment.class.getSimpleName();
    private LineChart mHeightChart, mWeightChart;
    private ArrayList<Double> heights, weights;
    private ArrayList<String> hdates, wdates;
    private SakshamApp app;
    private FirebaseFirestore db;
    private Patient patient;
    long startTime;
    long endTime;
    public StatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        mHeightChart = view.findViewById(R.id.height_chart);
        mWeightChart = view.findViewById(R.id.weight_chart);
        startTime = System.currentTimeMillis();
        heights = new ArrayList<>();
        hdates = new ArrayList<>();
        weights = new ArrayList<>();
        wdates = new ArrayList<>();

        app = SakshamApp.getInstance();
        db = app.getFirebaseDatabaseInstance();
        patient = app.getAppUser(null);

        try {
            createChart("height", mHeightChart);
            createChart("weight", mWeightChart);
        } catch (Exception e) {
            Log.i("ChartException", "Error in creaintg chart : " + e);
        }
        return view;
    }

    private void createChart(final String testName, final LineChart chart) {
        final ArrayList<String> dates = new ArrayList<>();
        final ArrayList<Double> values = new ArrayList<>();

        db.collection("records/" + patient.getId() + "/patient")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                if (snapshot.contains(testName)) {
                                    dates.add(snapshot.getId());
                                    values.add((Double) snapshot.get(testName));
                                }
                            }
                            setChartData(chart, values, dates, String.valueOf(testName.charAt(0)).toUpperCase() + testName.substring(1));
                        }
                    }
                }

                )
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void setChartData(LineChart chart, ArrayList<Double> dataDouble, ArrayList<String> dates, String label) {
        if (dataDouble == null || dataDouble.size() == 0) {
            return;
        }

        chart.setDrawGridBackground(false);

        // no description text
        chart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        // mHeightChart.setScaleXEnabled(true);
        // mHeightChart.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(true);

        // set an alternative background color
        // mHeightChart.setBackgroundColor(Color.GRAY);

        XAxis xAxis = chart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        chart.getAxisRight().setEnabled(false);
        chart.animateX(2500);

        xAxis.setValueFormatter(new AxisDateValueFormatter(dates));
        xAxis.setGranularity(1.0f);
        ArrayList<Entry> data = new ArrayList<>();
        for (int i = 0; i < dataDouble.size(); i++) {
            Log.d(TAG, String.valueOf(i + " : " + dates.get(i)));
            data.add(new Entry(i, dataDouble.get(i).floatValue()));
        }
        LineDataSet set1;
        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(data);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            set1 = new LineDataSet(data, label);
            set1.setDrawIcons(false);
            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);
            set1.setFormLineWidth(1f);
            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
            set1.setFormSize(15.f);
            set1.setFillColor(Color.BLACK);
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1); // add the datasets
            // create a data object with the datasets
            LineData datas = new LineData(dataSets);
            // set data
            chart.setData(datas);
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
                .collection(sdf.format(new Date())).document("Physical Health Monitor Progress");

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
                Activity_Usage au = new Activity_Usage(hms,totaltime,"Physical Health Montior Progress");
                SimpleDateFormat sdf = Utilities.getSimpleDateFormat();

                //add to database
                try {db.collection("time_spent/")
                        .document(app.getAppUser(null)
                                .getId())
                        .collection(sdf.format(new Date()))
                        .document(au.getActivity_name())
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
