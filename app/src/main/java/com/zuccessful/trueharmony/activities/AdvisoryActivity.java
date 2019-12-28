package com.zuccessful.trueharmony.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.utilities.FirebaseAnalyticsHelper;

public class AdvisoryActivity extends AppCompatActivity {

    private ImageView postImageIv;
    private TextView postTitleTv,postDescTv,postUserTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advisory);

        postImageIv = findViewById(R.id.post_image);
        postTitleTv = findViewById(R.id.post_title_txtview);
        postDescTv = findViewById(R.id.post_desc_txtview);
        postUserTv = findViewById(R.id.post_user);


        int advisoryItemPosition = (getIntent().getIntExtra("question_index",-1));
        String answer = getResources().getStringArray(R.array.psycho_edu_ans)[advisoryItemPosition];
        String question = getResources().getStringArray(R.array.psycho_edu_que)[advisoryItemPosition];

        Glide.with(this).load(R.drawable.gif11).into(postImageIv);

        postTitleTv.setText(question);
        postDescTv.setText(answer);

        FirebaseAnalyticsHelper.trackScreen(this,
                "PsychoEducation Q" +advisoryItemPosition);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition
                (R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
    }
}
