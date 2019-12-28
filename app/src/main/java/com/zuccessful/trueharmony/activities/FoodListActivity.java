package com.zuccessful.trueharmony.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.adapters.FoodListAdapter;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.utilities.Constants;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodListActivity extends AppCompatActivity {

    private SakshamApp app = SakshamApp.getInstance();
    private FirebaseFirestore db = app.getFirebaseDatabaseInstance();
    private RecyclerView recyclerView;
    private Context iContext;
    private List<String> foodList = new ArrayList<>();
    private  FoodListAdapter adapter;
    private Button submitButton;
    private String meal;
    // food adapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = findViewById(R.id.recyclerView);
        submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFood();
            }
        });
        adapter = new FoodListAdapter(foodList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getBaseContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        Intent intent = getIntent();
        iContext = this;
        meal = intent.getStringExtra("Meal");
        getSupportActionBar().setTitle(getResources().getString(R.string.what_eat)+" " +meal+"?");

        if (meal.equals(getResources().getString(R.string.breakfast)))
        {
            String [] foods = {getResources().getString(R.string.cereals),getResources().getString(R.string.egg),getResources().getString(R.string.fruits),getResources().getString(R.string.dryfruits),getResources().getString(R.string.milk),getResources().getString(R.string.beverage), getResources().getString(R.string.daal),getResources().getString(R.string.others)};
            List <String> temp = new ArrayList<>(Arrays.asList(foods));
            foodList.addAll(temp);
        }
        else if (meal.equals(getResources().getString(R.string.lunch)))
        {
            String [] foods = {getResources().getString(R.string.cereals),getResources().getString(R.string.daal),getResources().getString(R.string.salad),getResources().getString(R.string.fish_meat),getResources().getString(R.string.egg),getResources().getString(R.string.milk),getResources().getString(R.string.others)};
            List <String> temp = new ArrayList<>(Arrays.asList(foods));
            foodList.addAll(temp);
        }
        else if (meal.equals(getResources().getString(R.string.dinner)))
        {
            String [] foods = {getResources().getString(R.string.cereals),getResources().getString(R.string.salad),getResources().getString(R.string.daal),getResources().getString(R.string.fish_meat),getResources().getString(R.string.egg),getResources().getString(R.string.milk),getResources().getString(R.string.others)};
            List <String> temp = new ArrayList<>(Arrays.asList(foods));
            foodList.addAll(temp);
        }
        else{
            String [] foods = {getResources().getString(R.string.fruits),getResources().getString(R.string.dryfruits),getResources().getString(R.string.beverage),getResources().getString(R.string.cereals),getResources().getString(R.string.salad),getResources().getString(R.string.daal),getResources().getString(R.string.fish_meat),getResources().getString(R.string.egg),getResources().getString(R.string.milk),getResources().getString(R.string.others)};
            List <String> temp = new ArrayList<>(Arrays.asList(foods));
            foodList.addAll(temp);
        }
        adapter.notifyDataSetChanged();
    }

    private void submitFood() {
        List<String> selectedFoods;
        selectedFoods  = adapter.getSelectedFood();
        Utilities.saveListToSharedPref((ArrayList<String>)selectedFoods, meal.toLowerCase());
        Log.d("FoodListActivity", "submitFood: key: "+meal.toLowerCase());

//        Toast.makeText(getBaseContext(), "Submitted", Toast.LENGTH_SHORT).show();

        //update date in firebase
        SimpleDateFormat sdf2 = Utilities.getSimpleDateFormat();
        Map<String, Object> today_date = new HashMap<>();
        today_date.put(sdf2.format(new Date()), sdf2.format(new Date()));
        db.collection("patient_dietr_dates/"+app.getPatientID()+"/dates").document("dates").set(today_date, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("written", "DocumentSnapshot successfully written!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("written", "Error writing document", e);
                    }
                });
        Map<String, Object> meal_name = new HashMap<>();
        meal_name.put(meal, meal);
        db.collection("patient_dietr_dates/"+app.getPatientID()+"/dates").document("meals").set(meal_name,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("written", "DocumentSnapshot successfully written!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("written", "Error writing document", e);
                    }
                });
        Map<String,List> foodItems = new HashMap<>();
        foodItems.put("Items",selectedFoods);
        //update record in firebase
        SimpleDateFormat sdf = Utilities.getSimpleDateFormat();
        try {
            db.collection("patient_diet_logs/")
                .document(app.getAppUser(null)
                        .getId())
                .collection(sdf.format(new Date()))
                .document(meal)
                .set(foodItems).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("written", "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("written", "Error writing document", e);
                    }
                });
//            Toast.makeText(getBaseContext(), "Submitted", Toast.LENGTH_SHORT).show();
        } catch (Exception e) { e.printStackTrace(); }

        Intent intent = new Intent(this, DietActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
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
