//package com.zuccessful.trueharmony.fragments;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v7.widget.DividerItemDecoration;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ProgressBar;
//
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QuerySnapshot;
//import com.zuccessful.trueharmony.R;
//import com.zuccessful.trueharmony.application.SakshamApp;
//import com.zuccessful.trueharmony.pojo.DailyRoutine;
//
//import java.util.ArrayList;
//
//public class DailyRoutineManageNewFrag extends Fragment {
//
//    private SakshamApp app;
//    private FirebaseFirestore db;
//    private Context mContext;
//
//
//    private ArrayList<DailyRoutine> dailyRoutines = new ArrayList<>();
//    private RecyclerView mListView;
//    private ProgressBar progressBar;
//
//    public DailyRoutineManageNewFrag() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mContext = getContext();
//        app = SakshamApp.getInstance();
//        db = app.getFirebaseDatabaseInstance();
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_daily_routine_manage_new, container, false);
//
//        mListView = view.findViewById(R.id.daily_rout_record_list);
//        progressBar = view.findViewById(R.id.progressbar);
//        adapter = new DailyRoutineTaskAdapter(getContext(), dailyRoutines);
//
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
//        mListView.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));
//        mListView.setLayoutManager(layoutManager);
//        mListView.setAdapter(adapter);
//
//        return view;
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        updateTaskRecords();
//    }
//
//    private void updateTaskRecords() {
//        progressBar.setVisibility(View.VISIBLE);
//        mListView.setVisibility(View.GONE);
//
//        dailyRoutines = new ArrayList<>();
//
//        dailyRoutines.add(new DailyRoutine(getResources().getString(R.string.brush), DailyRoutine.DailyRoutineConstants.brush));
//        dailyRoutines.add(new DailyRoutine(getResources().getString(R.string.walk), DailyRoutine.DailyRoutineConstants.walk));
//        dailyRoutines.add(new DailyRoutine(getResources().getString(R.string.wake), DailyRoutine.DailyRoutineConstants.wake));
//        dailyRoutines.add(new DailyRoutine(getResources().getString(R.string.bath), DailyRoutine.DailyRoutineConstants.bath));
//        dailyRoutines.add(new DailyRoutine(getResources().getString(R.string.sleep), DailyRoutine.DailyRoutineConstants.sleep));
//        dailyRoutines.add(new DailyRoutine(getResources().getString(R.string.meal), DailyRoutine.DailyRoutineConstants.meal));
//
//        adapter.replaceRouts(dailyRoutines);
//
//        db.collection("alarms/" + app.getAppUser(null).getId() + "/daily_routine/")
//                .get()
//                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        progressBar.setVisibility(View.GONE);
//                        mListView.setVisibility(View.VISIBLE);
//                        Log.i("DailyRoutine", "in on success get daily tasks");
//                        if (!queryDocumentSnapshots.isEmpty()) {
////                            List<Medication> m = queryDocumentSnapshots.toObjects(Medication.class);
//                            for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
//                                DailyRoutine m = ds.toObject(DailyRoutine.class);
//                                updateTaskAddView(m);
//                            }
//                            Log.i("DailyRoutine", "DAILY SZE = " + dailyRoutines.size());
//
//                            adapter.replaceRouts(dailyRoutines);
//                        }
//                    }
//                });
//    }
//
//    private void updateTaskAddView(DailyRoutine routine) {
//        try {
//            if (getResources().getString(R.string.brush).equals(routine.getName())
//            || routine.getId().equals(String.valueOf(DailyRoutine.DailyRoutineConstants.brush))) {
//                dailyRoutines.remove(0);
//                dailyRoutines.add(0, routine);
//            }
//            if (getResources().getString(R.string.walk).equals(routine.getName()) ||
//                    routine.getId().equals(String.valueOf(DailyRoutine.DailyRoutineConstants.walk))) {
//                dailyRoutines.remove(1);
//                dailyRoutines.add(1, routine);
//            }
//            if (getResources().getString(R.string.wake).equals(routine.getName())
//            || routine.getId().equals(String.valueOf(DailyRoutine.DailyRoutineConstants.wake))) {
//                dailyRoutines.remove(2);
//                dailyRoutines.add(2, routine);
//            }
//            if (getResources().getString(R.string.bath).equals(routine.getName())
//            || routine.getId().equals(String.valueOf(DailyRoutine.DailyRoutineConstants.bath))) {
//                dailyRoutines.remove(3);
//                dailyRoutines.add(3, routine);
//            }
//            if (getResources().getString(R.string.sleep).equals(routine.getName()) ||
//                    routine.getId().equals(String.valueOf(DailyRoutine.DailyRoutineConstants.sleep))) {
//                dailyRoutines.remove(4);
//                dailyRoutines.add(4, routine);
//            }
//            if (getResources().getString(R.string.meal).equals(routine.getName())
//            || routine.getId().equals(String.valueOf(DailyRoutine.DailyRoutineConstants.meal))) {
//                dailyRoutines.remove(5);
//                dailyRoutines.add(5, routine);
//            }
//        }catch (Exception e){
//
//        }
//    }
//
//}
