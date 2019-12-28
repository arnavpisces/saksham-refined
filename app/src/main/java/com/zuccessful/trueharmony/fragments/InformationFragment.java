package com.zuccessful.trueharmony.fragments;

import android.content.Context;
import android.content.res.TypedArray;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.Toaster;
import com.zuccessful.trueharmony.activities.IADLActivity;
import com.zuccessful.trueharmony.adapters.VerticlePagerAdapter;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.Activity_Usage;
import com.zuccessful.trueharmony.pojo.Information;
import com.zuccessful.trueharmony.pojo.Injection;
import com.zuccessful.trueharmony.pojo.QuestionUsage;
import com.zuccessful.trueharmony.utilities.Utilities;
import com.zuccessful.trueharmony.utilities.VerticalViewPager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.support.constraint.Constraints.TAG;

public class InformationFragment extends Fragment {

    ImageButton leftNav, rightNav;
    View view;
    String[] ques;
    String[] ans;
    private VerticalViewPager verticalViewPager;
    private Context mContext;
    private TypedArray imagesArray;
    String module;
    long startTime;
    long endTime;
    String TAG = "InfoModule";
    long start_time_info, end_time_info;
    int curr_ques=0,prev_ques=-1;
    HashMap<Integer,Long> Info_time_array ;

    // private long oldtimespend;
    private SakshamApp app = SakshamApp.getInstance();
    private FirebaseFirestore db = app.getFirebaseDatabaseInstance();

    public InformationFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_information, container, false);
        mContext = getContext();
        startTime = System.currentTimeMillis();
        Info_time_array = new HashMap<Integer,Long>();
        start_time_info = startTime;

        //access data from the calling activity, i.e., question and answers
        Bundle bundle = this.getArguments();
        ArrayList<String> q = bundle.getStringArrayList("questions");
        ques = (q).toArray(new String[q.size()]);
        ArrayList<String> a = bundle.getStringArrayList("answers");
        ans = (a).toArray(new String[a.size()]);
        module = bundle.getString("Module");
        Log.d("MODNAME",module);

        if (module.equals("Psycho Education")){
            imagesArray = getResources().obtainTypedArray(R.array.psycho_edu_images);
        }else{
            imagesArray = getResources().obtainTypedArray(R.array.med_adh_images);
        }

        verticalViewPager = view.findViewById(R.id.verticalViewPager);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        verticalViewPager.setOffscreenPageLimit(1);
        verticalViewPager.setAdapter(new VerticlePagerAdapter(mContext, ques, ans,imagesArray));
        leftNav = view.findViewById(R.id.left_nav);
        rightNav = view.findViewById(R.id.right_nav);

        verticalViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


                if(position==0){
                    leftNav.setVisibility(View.INVISIBLE);
                }
                else {
                    leftNav.setVisibility(View.VISIBLE);

                }
                if(position==ques.length-1)
                    rightNav.setVisibility(View.INVISIBLE);
                else
                    rightNav.setVisibility(View.VISIBLE);


                curr_ques = position;

                if(curr_ques==1 && prev_ques==-1){
                    prev_ques = position-1;
                }

                if(prev_ques!=-1){
                    end_time_info = System.currentTimeMillis();
                    long time_spent_info = end_time_info-start_time_info;

                    if(Info_time_array.containsKey(prev_ques)){
                        Long time1 = Info_time_array.get(prev_ques);
                        time1 += time_spent_info;
                        Long x = Info_time_array.put(prev_ques,time1);
                    }
                    else
                    {
                        Long x = Info_time_array.put(prev_ques,time_spent_info);
                    }
                    prev_ques = curr_ques;
                    start_time_info = System.currentTimeMillis();
                }



            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

            
        });



        // Images left navigation
        leftNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verticalViewPager.arrowScroll(View.FOCUS_LEFT);
            }
        });

        // Images right navigatin
        rightNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    verticalViewPager.arrowScroll(View.FOCUS_RIGHT);
                }catch (Exception e){
                    Toaster.showShortMessage("Extra Page!");
                }
            }
        });
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
                .collection(sdf.format(new Date())).document(module+" Information");

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

    private interface  FirestroreCallBack2{
        void onCallBack(QuestionUsage qu);
    }
    protected void fetchObject2(final FirestroreCallBack2 firestroreCallBack){



        SimpleDateFormat sdf = Utilities.getSimpleDateFormat();

        final DocumentReference documentReference;
        documentReference = db.collection("time_spent_info/")
                .document(app.getAppUser(null)
                        .getId())
                .collection(sdf.format(new Date())).document(module+" Questions");

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot=task.getResult();
                    QuestionUsage activity_usage = documentSnapshot.toObject( QuestionUsage.class);
                    if(activity_usage==null){
                        QuestionUsage time = new QuestionUsage(module + " Questions", new HashMap<String, Long>());
                        firestroreCallBack.onCallBack(time);
                        return;
                    }
                    firestroreCallBack.onCallBack(activity_usage);

                }else{
                    Log.d(TAG,"Error: Can't get Activity_Usage",task.getException());
                }

            }

        });

    }


    @Override
    public void onStop()
    {

        if(curr_ques==0 && prev_ques==-1){
            prev_ques = curr_ques;
        }
        end_time_info = System.currentTimeMillis();
        final long time_spent_info = end_time_info-start_time_info;

        if(Info_time_array.containsKey(prev_ques)){
            Long time1 = Info_time_array.get(prev_ques);
            time1 += time_spent_info;
            Long x = Info_time_array.put(prev_ques,time1);
        }
        else
        {
            Long x = Info_time_array.put(prev_ques,time_spent_info);
        }

        Log.d("TimeSpentInfo", Info_time_array.toString());

//        for (  int ques : Info_time_array.keySet())
//        {
//            Long totaltime = Info_time_array.get(ques);
//            String hms1 = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(totaltime),
//                    TimeUnit.MILLISECONDS.toMinutes(totaltime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(totaltime)),
//                    TimeUnit.MILLISECONDS.toSeconds(totaltime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totaltime)));
//            Log.d("TimeSpentInfo "+ Integer.toString(ques),hms1 );}

            fetchObject2(new FirestroreCallBack2() {
                @Override
                public void onCallBack(QuestionUsage questionUsage) {

                    HashMap<String,Long> hm = questionUsage.getHm();

                    HashMap<String,Long> newhm = new HashMap<String, Long>();

                    for(int ques : Info_time_array.keySet()){
                        if(hm.containsKey(Integer.toString(ques))){
                            Long time = hm.get(Integer.toString(ques));
                            time += Info_time_array.get(ques);
                            Long x = newhm.put(Integer.toString(ques),time);
                        }else{
                            Long x = newhm.put(Integer.toString(ques),Info_time_array.get(ques));
                        }
                    }

                    for(String ques : hm.keySet()){
                        if(!newhm.containsKey(ques)){
                            newhm.put(ques,hm.get(ques));
                        }
                    }

                    String name = module+" Questions";
                    QuestionUsage qu = new QuestionUsage(name,newhm);
                    SimpleDateFormat sdf = Utilities.getSimpleDateFormat();
                    //add to database
                    try {


                        db.collection("time_spent_info/")
                            .document(app.getAppUser(null)
                                    .getId())
                            .collection(sdf.format(new Date()))
                            .document(module+" Questions").set(qu);

                        Map<String, Object> today_date = new HashMap<>();
                        SimpleDateFormat sdf2 = Utilities.getSimpleDateFormat();
                        today_date.put(sdf2.format(new Date()), sdf2.format(new Date()));
                        db.collection("time_dates_info/"+app.getPatientID()+"/dates").document("dates").set(today_date, SetOptions.merge());
                        Map<String, Object> module_name = new HashMap<>();
                        module_name.put(qu.getName(),qu.getName()) ;
                        db.collection("time_dates_info/"+app.getPatientID()+"/dates").document("questions").set(module_name, SetOptions.merge());
                    } catch (Exception e) { Log.d(TAG ,"ERROR : can't get object", e); }

                }
            });




        endTime = System.currentTimeMillis();
        final long timeSpend = endTime - startTime;

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
                Activity_Usage au = new Activity_Usage(hms,totaltime,module+" Information");
                SimpleDateFormat sdf = Utilities.getSimpleDateFormat();

                //add to database
                try {db.collection("time_spent/")
                        .document(app.getAppUser(null)
                                .getId())
                        .collection(sdf.format(new Date()))
                        .document(module+" Information")
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


