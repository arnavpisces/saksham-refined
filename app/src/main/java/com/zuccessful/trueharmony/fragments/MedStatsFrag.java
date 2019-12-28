package com.zuccessful.trueharmony.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.zuccessful.trueharmony.R;
//import com.zuccessful.trueharmony.activities./s;
import com.zuccessful.trueharmony.activities.ViewMeds;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.Activity_Usage;
import com.zuccessful.trueharmony.pojo.Medication;
import com.zuccessful.trueharmony.pojo.MedicineRecord;
import com.zuccessful.trueharmony.pojo.Patient;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MedStatsFrag extends Fragment  {
    long startTime;
    long endTime;

    private SakshamApp app = SakshamApp.getInstance();
    private FirebaseFirestore db = app.getFirebaseDatabaseInstance();

    private Patient patient;
    /* private TableLayout tableLayout;
     private TableRow tableRow1;
     private TableRow tableRowX;*/
    private ArrayList<Medication> medicationArrayList;
    private final String TAG = "MEDTAG";
    final HashMap<String,List> dateMedRecMap = new HashMap<>();
    private BarChart barChart;
    public MedStatsFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        startTime = System.currentTimeMillis();
        View view = inflater.inflate(R.layout.fragment_med_stats, container, false);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        patient = app.getAppUser(null);
        barChart = (BarChart) view.findViewById(R.id.barchart);
       /* tableLayout = (TableLayout) view.findViewById(R.id.table);
        tableRow1 = new TableRow(getContext());
        TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        tableLayout.setLayoutParams(layoutParams);*/
        Log.d("MedStatsFrag", "onCreateView: "+"Fetching medicine list");
        fetchMedicines();
        fetchMedicineRecords();
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading Graph");
        progressDialog.setMessage("Please wait");
        progressDialog.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //createTable();
                createChart();
                progressDialog.dismiss();
            }
        },5000);
        return view;
    }

    private void createChart()
    {
        ArrayList<BarEntry> barGroupTaken = new ArrayList<>();
        ArrayList<BarEntry> barGroupMissed= new ArrayList<>();
        String[] dates = new String[100];
        int dateVal = 0;
        for (String date : dateMedRecMap.keySet())
        {
            int takenVal = 0;
            int missedVal = 0;
            //datesLabel.add(date);
            dates[dateVal]=date;
            List<MedicineRecord> medicineRecordList = dateMedRecMap.get(date);
            for (MedicineRecord medicineRecord : medicineRecordList)
            {
                Boolean taken = medicineRecord.isTaken();
                if (taken==true)
                {
                    takenVal++;
                }
                else
                {
                    missedVal++;
                }
            }
            barGroupTaken.add(new BarEntry(dateVal,takenVal));
            barGroupMissed.add(new BarEntry(dateVal,missedVal));
            dateVal++;
        }
        BarDataSet barDataSet1 = new BarDataSet(barGroupTaken,"Taken");
        barDataSet1.setColor(Color.GREEN);
        BarDataSet barDataSet2 = new BarDataSet(barGroupMissed,"Missed");
        barDataSet2.setColor(Color.RED);

        /*ArrayList<BarDataSet> dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        dataSets.add(barDataSet2);*/

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
        barChart.invalidate();

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                String date = barChart.getXAxis().getValueFormatter().getFormattedValue((float) Math.floor(e.getX()),barChart.getXAxis());
                Log.d(TAG, "onValueSelected: " + date);
                Intent intent = new Intent(getContext(), ViewMeds.class);
                intent.putExtra("MEDMAP",dateMedRecMap);
                intent.putExtra("DATE",date);
                startActivity(intent);
            }

            @Override
            public void onNothingSelected() {

            }
        });

    }

    private void fetchMedicines() {
        medicationArrayList = new ArrayList<>();
        db.collection("alarms/" + app.getAppUser(null).getId() + "/medication")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                               if (task.isSuccessful()) {
                                                   for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                       Medication medication = snapshot.toObject(Medication.class);
                                                       //Log.d(TAG, "onComplete: "+medication.toString());
                                                       medicationArrayList.add(medication);
                                                   }
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


    private void fetchMedicineRecords()
    {

        try {
            //medicationArrayList;
            final List<MedicineRecord> medicineRecordList = new ArrayList<>();
            Log.d("MedStatsFrag", "fetchMedicineRecords: Fetching medicineRecords");
            SimpleDateFormat sdf = Utilities.getSimpleDateFormat();
            final String today = sdf.format(new Date());
            DocumentReference documentReference = db.collection("patient_med_logs/").document(app.getAppUser(null).getId() + "/" + today);

            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
//                                       for (final String date : documentSnapshot.getData().keySet())
//                    {
                        String printme = "patient_med_logs/" + app.getAppUser(null).getId() + "/" + today;
                        Log.d("MedStatsFrag", "onComplete: " + printme);
                        db.collection("patient_med_logs/" + app.getAppUser(null).getId() + "/" + today).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                        MedicineRecord medicineRecord = snapshot.toObject(MedicineRecord.class);
                                        medicineRecordList.add(medicineRecord);
                                    }

                                    dateMedRecMap.put(today, (List) ((ArrayList<MedicineRecord>) medicineRecordList).clone());
                                    Log.d(TAG, "onComplete1: " + dateMedRecMap.toString());
                                    medicineRecordList.clear();
                                }

                            }
                        });

//                    }

                    }

                }
            });
        }catch (Exception e){
            Log.d("MedStatsFrag", "fetchMedicineRecords: Requires even number of segments");
        }
    }



    /*private void createTable()
    {
        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = "medication_record.csv";
        String filePath = baseDir + File.separator + fileName;
        File f = new File(filePath);
        CSVWriter writer = null;

        if (f.exists() && !f.isDirectory())
        {
            try {
                FileWriter mFileWriter = new FileWriter(filePath , false);
                writer = new CSVWriter(mFileWriter);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else
        {
            try {
                writer = new CSVWriter(new FileWriter(filePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        List<String[]> data = new ArrayList<>();
        String[] row = new String[20];
        Log.d(TAG, "works");
        for (String date : dateMedRecMap.keySet())
        {
            int i =0;
            row[i]=date;
            i++;
            //Log.d(TAG, "createTable: "+date);
            List<MedicineRecord> medicineRecordList = dateMedRecMap.get(date);
            for (MedicineRecord medicineRecord : medicineRecordList)
            {
                String name = medicineRecord.getName();
                boolean taken = medicineRecord.isTaken();

                row[i] = name+": "+ Boolean.toString(taken);
                //Log.d(TAG, "createTable: "+name);
                i++;
            }
            Log.d(TAG, "Row0-"+row[0]);
            Log.d(TAG, "Row1-"+row[1]);
            data.add(row);
        }
        try
        {
            writer.writeAll(data);
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        *//*Intent i = new Intent();
        i.setAction(android.content.Intent.ACTION_VIEW);
        i.setDataAndType(Uri.fromFile(f), "text/csv");
        startActivity(i);*//*

    }*/
    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
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
                .collection(sdf.format(new Date())).document("Medical Adherence Progress");

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
                Activity_Usage au = new Activity_Usage(hms,totaltime,"Medical Adherence Progress");
                SimpleDateFormat sdf = Utilities.getSimpleDateFormat();

                //add to database
                try {db.collection("time_spent/")
                        .document(app.getAppUser(null)
                                .getId())
                        .collection(sdf.format(new Date()))
                        .document("Medical Adherence Progress")
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
