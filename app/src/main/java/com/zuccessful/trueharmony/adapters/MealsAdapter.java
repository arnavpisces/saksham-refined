package com.zuccessful.trueharmony.adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.activities.FoodListActivity;

import java.util.List;

public class MealsAdapter extends RecyclerView.Adapter<MealsAdapter.MyViewHolder> {
    private List<String> mealList;
    @NonNull
    @Override

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.meal_content, parent, false);

        return new MyViewHolder(itemView);
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mealName;

        public MyViewHolder(View view) {
            super(view);
            mealName = (TextView) view.findViewById(R.id.mealText);
        }
    }

    public MealsAdapter(List<String> mealList) {
        this.mealList = mealList;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int i) {
        final String name = mealList.get(i);
        holder.mealName.setText(name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(v.getContext(), name, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(v.getContext(), FoodListActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Meal",name);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }
}
