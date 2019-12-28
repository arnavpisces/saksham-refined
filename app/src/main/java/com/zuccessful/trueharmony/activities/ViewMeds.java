package com.zuccessful.trueharmony.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.pojo.MedicineRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewMeds extends AppCompatActivity {

    private ListView listView;
    private TextView textView;
    private List<String> medsList;
    private final String TAG = "ViewMedsTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_meds);
        Intent intent = getIntent();
        HashMap<String, List> dateMedRecMap = (HashMap<String, List>) intent.getSerializableExtra("MEDMAP");
        String date = (String) intent.getSerializableExtra("DATE");
        Log.d(TAG, "onCreate: "+date);
        Log.d(TAG, "onCreate: "+dateMedRecMap.toString());
        listView = (ListView) findViewById(R.id.meds_list_view);
        textView = (TextView) findViewById(R.id.textView);
        medsList = new ArrayList<>();
        List<MedicineRecord> medicineRecordList = dateMedRecMap.get(date);
        for (MedicineRecord medicationRecord : medicineRecordList )
        {
            String name = medicationRecord.getName();
            boolean isTaken = medicationRecord.isTaken();
            String taken;
            if (isTaken==true)
                taken = "Taken";
            else
                taken = "Missed";
            medsList.add(name+"  :  "+taken);
        }
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(),
                R.layout.med_list_content, R.id.textView, medsList);
        listView.setAdapter(adapter);
    }
}
