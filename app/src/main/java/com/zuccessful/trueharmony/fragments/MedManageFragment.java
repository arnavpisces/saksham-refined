package com.zuccessful.trueharmony.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.activities.AddMedRecActivity;
import com.zuccessful.trueharmony.adapters.MedsAdapter;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.Activity_Usage;
import com.zuccessful.trueharmony.pojo.Medication;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class MedManageFragment extends Fragment {

    private SakshamApp app;
    private FirebaseFirestore db;
    private ArrayList<Medication> medications;
    private LinearLayout addMedView;
    private RecyclerView mMedListView;
    private TextView mBlankMedRecord;
    private ProgressBar progressBar;
    private MedsAdapter adapter;
    private Context mContext;
    long startTime;
    long endTime;
    String TAG = "MedManageFrag";

    public MedManageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startTime = System.currentTimeMillis();
        mContext = getContext();
        app = SakshamApp.getInstance();
        db = app.getFirebaseDatabaseInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_med_manage, container, false);

        addMedView = view.findViewById(R.id.add_med_btn);
        mMedListView = view.findViewById(R.id.med_record_list);
        mBlankMedRecord = view.findViewById(R.id.blank_med_record);
        progressBar = view.findViewById(R.id.progressbar);


        adapter = new MedsAdapter(medications,getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mMedListView.setLayoutManager(layoutManager);
        mMedListView.setAdapter(adapter);

//        progressBar.setVisibility(View.VISIBLE);

        addMedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewMedRecord(view);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateMedRecords();
    }

    public void updateMedRecords() {
        medications = new ArrayList<>();
        db.collection("alarms/" + app.getAppUser(null).getId() + "/medication/")
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        medications = Utilities.getListOfMedication();
                        if (medications!=null && medications.size() > 0) {
                            adapter.replaceMeds(medications);
                            mBlankMedRecord.setVisibility(View.GONE);
                            mMedListView.setVisibility(View.VISIBLE);
                        } else {
                            mBlankMedRecord.setVisibility(View.VISIBLE);
                            mMedListView.setVisibility(View.GONE);
                        }
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        progressBar.setVisibility(View.GONE);

                        if (!queryDocumentSnapshots.isEmpty()) {
//                            List<Medication> m = queryDocumentSnapshots.toObjects(Medication.class);
                            for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                                Medication m = ds.toObject(Medication.class);
                                medications.add(m);
                            }
                            if (medications.size() > 0) {
                                adapter.replaceMeds(medications);
                                mBlankMedRecord.setVisibility(View.GONE);
                                mMedListView.setVisibility(View.VISIBLE);
                            } else {
                                mBlankMedRecord.setVisibility(View.VISIBLE);
                                mMedListView.setVisibility(View.GONE);
                            }
//                            Toast.makeText(getContext(), "List: " + medications, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void addNewMedRecord(View view) {
        startActivity(new Intent(getContext(), AddMedRecActivity.class));
//        Toast.makeText(getContext(), "Add New Medicine Information", Toast.LENGTH_SHORT).show();
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
                .collection(sdf.format(new Date())).document("Medical Adherence Fragment");

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


                //create object
                Activity_Usage au = new Activity_Usage(hms,totaltime,"Medical Adherence Fragment");
                SimpleDateFormat sdf = Utilities.getSimpleDateFormat();

                //add to database
                try {db.collection("time_spent/")
                        .document(app.getAppUser(null)
                                .getId())
                        .collection(sdf.format(new Date()))
                        .document("Medical Adherence Fragment")
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
