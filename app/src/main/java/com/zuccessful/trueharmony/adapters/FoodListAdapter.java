package com.zuccessful.trueharmony.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.application.SakshamApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.MyViewHolder> {
    private List<String> foodList;
    private List<String> selectedFood;
    private HashMap<String,Integer> foodImageMap;
    SakshamApp app = SakshamApp.getInstance();
    Context context =  app.getBaseContext();
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.food_content, parent, false);
        foodImageMap = new HashMap<>();
        foodImageMap.put(context.getResources().getString(R.string.cereals), R.drawable.cereals);
        foodImageMap.put(context.getResources().getString(R.string.egg),R.drawable.eggs);
        foodImageMap.put(context.getResources().getString(R.string.fruits),R.drawable.fruits);
        foodImageMap.put(context.getResources().getString(R.string.dryfruits),R.drawable.almonds);
        foodImageMap.put(context.getResources().getString(R.string.milk),R.drawable.milk);
        foodImageMap.put(context.getResources().getString(R.string.beverage),R.drawable.juice);
        foodImageMap.put(context.getResources().getString(R.string.daal),R.drawable.vegetables);
        foodImageMap.put(context.getResources().getString(R.string.others),R.drawable.others_revised);
        foodImageMap.put(context.getResources().getString(R.string.salad),R.drawable.vegetables);
        foodImageMap.put(context.getResources().getString(R.string.fish_meat),R.drawable.meat);
        return new MyViewHolder(itemView);
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView foodName;
        public ImageView foodImage;
        public CardView cardView;
        public MyViewHolder(View view) {
            super(view);
            foodName = (TextView) view.findViewById(R.id.foodName);
            foodImage = (ImageView) view.findViewById(R.id.foodImage);
            cardView = (CardView) view.findViewById(R.id.rootCardView);
        }
    }

    public FoodListAdapter(List<String> foodList) {
        this.foodList = foodList;
        selectedFood = new ArrayList<>();
    }

    @Override
    public void onBindViewHolder(@NonNull final FoodListAdapter.MyViewHolder holder, int i) {
        final String name = foodList.get(i);
        holder.foodName.setText(name);
        if (foodImageMap.get(name)!=null) {
            holder.foodImage.setImageResource(foodImageMap.get(name));
        }
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            boolean clicked = false;
            @Override
            public void onClick(View v) {
                clicked = !clicked;
                if (clicked)
                {
                    holder.cardView.setAlpha(0.5f);
                    selectedFood.add(name);
                }
                else
                {
                    holder.cardView.setAlpha(1f);
                    selectedFood.remove(name);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public List<String> getSelectedFood()
    {
        return selectedFood;
    }
}
