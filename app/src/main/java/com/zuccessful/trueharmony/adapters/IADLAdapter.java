package com.zuccessful.trueharmony.adapters;


import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.activities.PlayVideoActivity;
import com.zuccessful.trueharmony.pojo.Video;
import com.zuccessful.trueharmony.utilities.Constants;

import java.util.ArrayList;


public class IADLAdapter extends RecyclerView.Adapter<IADLAdapter.VidsViewHolder> {
    private ArrayList<Video> videos;

    public static class VidsViewHolder extends RecyclerView.ViewHolder {

        CardView cv;
        TextView vName;
        ImageView vthumbnail;
        String video_path;

        VidsViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            vName = (TextView)itemView.findViewById(R.id.video_name);
            vthumbnail = (ImageView)itemView.findViewById(R.id.video_photo);




            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(v.getContext(),"item Clicked : "
//                            ,Toast.LENGTH_SHORT).show();

                    Intent editMedIntent = new Intent(v.getContext(), PlayVideoActivity.class);
                    editMedIntent.putExtra("video_path",video_path);
                    v.getContext().startActivity(editMedIntent);

                }
            });
        }



    }



    public IADLAdapter(ArrayList<Video> videos){
        this.videos = videos;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public VidsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.video_item, viewGroup, false);
        VidsViewHolder pvh = new VidsViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(VidsViewHolder videoViewHolder, int i) {
        videoViewHolder.video_path = videos.get(i).getvPathName();
        videoViewHolder.vName.setText(videos.get(i).getvName());
        videoViewHolder.vthumbnail.setImageResource(videos.get(i).getvID());
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }


}
