package com.zuccessful.trueharmony.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.activities.AdvisoryActivity;
import com.zuccessful.trueharmony.activities.PSychoeducation;
import com.zuccessful.trueharmony.utilities.FirebaseAnalyticsHelper;

import java.util.HashMap;
import java.util.Map;

public class PsychoAdapter extends RecyclerView.Adapter<PsychoAdapter.ViewHolder> {
    private String[] mDataset;
    public static Context mContext;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mQuestionTv;


        public ViewHolder(final View callView, final View parent) {
            super(callView);

            mQuestionTv = callView.findViewById(R.id.question_text);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PsychoAdapter(String[] myDataset, PSychoeducation pSychoeducation) {
        mDataset = myDataset;
        mContext = pSychoeducation;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PsychoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view

        View callView;

        callView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.question_cardview,
                parent, false);
                return new ViewHolder(callView,parent);

    }



    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mQuestionTv.setText(mDataset[position]);

        holder.mQuestionTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent advActIntent = new Intent(v.getContext(),AdvisoryActivity.class);
                advActIntent.putExtra("question_index", position);
                v.getContext().startActivity(advActIntent);
                ((Activity) v.getContext()).overridePendingTransition
                        (R.anim.anim_slide_in_left, R.anim.anim_slide_out_left);

                Map<String,String> analyticsMap = new HashMap();
                analyticsMap.put(FirebaseAnalyticsHelper.FirebaseAnalyConst.QUESTION_NUMBER,
                        String.valueOf(position));
                analyticsMap.put(FirebaseAnalyticsHelper.FirebaseAnalyConst.ACTIVITY_NAME,
                        v.getContext().getString(R.string.psycho_education));
                FirebaseAnalyticsHelper.sendMapAnalytics(analyticsMap,
                        FirebaseAnalyticsHelper.FirebaseAnalyConst.QUESTION_CLICKED);



            }
        });


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}
