package com.zuccessful.trueharmony.activities;

import android.app.ActionBar;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransitionImpl;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.fragments.InjStatsFrag;
import com.zuccessful.trueharmony.pojo.Activity_Usage;
import com.zuccessful.trueharmony.pojo.FileDownloader;
import com.zuccessful.trueharmony.pojo.InjectionRecord;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.Nullable;

public class LibraryActivity extends AppCompatActivity {


    private TextView title;
    private TextView description;
    long startTime;
    long endTime;
    String TAG = "LibraryModule";

   // private long oldtimespend;
    private SakshamApp app = SakshamApp.getInstance();
    private FirebaseFirestore db = app.getFirebaseDatabaseInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startTime = System.currentTimeMillis();
        setContentView(R.layout.resource_list);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        changeActionBarFont();


        ImageView view_image = (ImageView) findViewById(R.id.view);
        title = (TextView) findViewById(R.id.pdf_item_name);
        description = (TextView) findViewById(R.id.pdf_des);
        title.setText(getResources().getString(R.string.psycho_education));
        description.setText(getResources().getString(R.string.psycho_education));


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        view_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    download(v);
                 //   view(v);

            }
        });


    }

    public void download(View v)
    {
        String url="https://drive.google.com/open?id=1FZ33bMNy0OYB-qT-d70enJsPjj7SeL7Z";

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void view(View v)
    {
        TextView titleView = (TextView) findViewById(R.id.pdf_item_name);
        String title = titleView.getText().toString()+".pdf";

        File pdfFile = new File(Environment.getExternalStorageDirectory().toString() + "/Saksham/" + title);  // -> filename = maven.pdf
        Log.d("LIB-DIR", "view: "+pdfFile.toString());

        Uri path = Uri.fromFile(pdfFile);
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Intent intent = Intent.createChooser(pdfIntent, "Open File");

        try{
            startActivity(intent);
        }catch(ActivityNotFoundException e){
            Toast.makeText(getApplicationContext(), "No Application available to view PDF", Toast.LENGTH_SHORT).show();
        }
    }
    public void changeActionBarFont(){ //This function is used to customize the action bar
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        // Create a TextView programmatically.
        TextView tv = new TextView(getApplicationContext());

        // Create a LayoutParams for TextView
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, // Width of TextView
                RelativeLayout.LayoutParams.WRAP_CONTENT); // Height of TextView

        // Apply the layout parameters to TextView widget
        tv.setLayoutParams(lp);

        // Set text to display in TextView
        tv.setText(ab.getTitle()); // ActionBar title text

        // Set the text color of TextView to black
        tv.setTextColor(Color.WHITE);

        // Set the monospace font for TextView text
        // This will change ActionBar title text font
        Typeface customTypeface = ResourcesCompat.getFont(getApplicationContext(),R.font.semibold);

        tv.setTypeface(customTypeface);
        tv.setTextSize(20);

        // Set the ActionBar display option
        ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        // Finally, set the newly created TextView as ActionBar custom view
        ab.setCustomView(tv);
//        customToolbar=findViewById(R.id.toolbar);
//        setSupportActionBar(customToolbar);
        //Changing the appbar title font here
    }
    private class DownloadFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];   // -> http://maven.apache.org/maven-1.x/maven.pdf
            String fileName = strings[1];  // -> maven.pdf

            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "Saksham");
            if(folder.mkdir())
               Log.d(TAG,"LIB-DIR CREATED");
            else
                Log.d(TAG, "LIB-DIR NOT CREATED");


            File pdfFile = new File(folder, fileName);

            try{
                pdfFile.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            FileDownloader.downloadFile(fileUrl, pdfFile);
            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
                .collection(sdf.format(new Date())).document("Library");

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
                Activity_Usage au = new Activity_Usage(hms,totaltime,"Library");
                SimpleDateFormat sdf = Utilities.getSimpleDateFormat();

                //add to database
                try {db.collection("time_spent/")
                        .document(app.getAppUser(null)
                                .getId())
                        .collection(sdf.format(new Date()))
                        .document("Library")
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
