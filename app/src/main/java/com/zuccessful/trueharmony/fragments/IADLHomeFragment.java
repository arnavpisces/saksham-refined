package com.zuccessful.trueharmony.fragments;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.adapters.IADLAdapter;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.Activity_Usage;
import com.zuccessful.trueharmony.pojo.Video;
import com.zuccessful.trueharmony.utilities.Constants;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


public class IADLHomeFragment extends Fragment {
    private RecyclerView aListView;
    private ArrayList<Video> videos;
    long startTime;
    long endTime;
    String TAG = "IADLHomeFrag";
    private SakshamApp app = SakshamApp.getInstance();
    private FirebaseFirestore db = app.getFirebaseDatabaseInstance();

    public IADLHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startTime = System.currentTimeMillis();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.fragment_iadl_videos, container, false);
        aListView = (RecyclerView) view.findViewById(R.id.vid_list);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        aListView.setLayoutManager(llm);


        initializeData();
        initializeAdapter();



        return view;
    }
    private void initializeData(){
        this.videos = new ArrayList<>();
        String langPrefType  = Utilities.getDataFromSharedpref(getActivity().getApplicationContext(), Constants.KEY_LANGUAGE_PREF);

        if(langPrefType!=null) {
            int lang = Integer.parseInt(langPrefType);
            if(lang==1) {
                //language is hindi
                this.videos.add(new Video(getString(R.string.video1), R.drawable.ri_thumbnail, "raghuhindi"));
            }else{
                //language is english
                this.videos.add(new Video(getString(R.string.video1), R.drawable.ri_thumbnail, "raghuintro"));
            }

        }else{
//            language is hindi by default
            this.videos.add(new Video(getString(R.string.video1), R.drawable.ri_thumbnail, "raghuhindi"));
        }


    }

    private void initializeAdapter(){

        IADLAdapter adapter = new IADLAdapter(videos);
        aListView.setAdapter(adapter);

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
                .collection(sdf.format(new Date())).document("Self Reliance Fragment");

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
                Activity_Usage au = new Activity_Usage(hms,totaltime,"Self Reliance Fragment");
                SimpleDateFormat sdf = Utilities.getSimpleDateFormat();

                //add to database
                try {db.collection("time_spent/")
                        .document(app.getAppUser(null)
                                .getId())
                        .collection(sdf.format(new Date()))
                        .document("Self Reliance Fragment")
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
