package com.zuccessful.trueharmony.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zuccessful.trueharmony.R;

public class VerticlePagerAdapter extends PagerAdapter {

    //    String mResources[] = {"To start off lets understand what exactly Android CardView is? Its a new widget for Android, which can be used to display a card sort of a layout in android. As you may know Android material design is inspired from paper and ink concept. Mostly it displays views on top of each other, with shadows. In simple terms, Android CardView is such a view which has all material design properties, most importantly showing shadows according the elevation. The best part about this view is that it extends FrameLayout and it can be displayed on all the platforms of android since it’s available through the Support v7 library. Lets have a look at some of its properties:", "To start off lets understand what exactly Android CardView is? Its a new widget for Android, which can be used to display a card sort of a layout in android. As you may know Android material design is inspired from paper and ink concept. Mostly it displays views on top of each other, with shadows. In simple terms, Android CardView is such a view which has all material design properties, most importantly showing shadows according the elevation. The best part about this view is that it extends FrameLayout and it can be displayed on all the platforms of android since it’s available through the Support v7 library. Lets have a look at some of its properties:", "To start off lets understand what exactly Android CardView is? Its a new widget for Android, which can be used to display a card sort of a layout in android. As you may know Android material design is inspired from paper and ink concept. Mostly it displays views on top of each other, with shadows. In simple terms, Android CardView is such a view which has all material design properties, most importantly showing shadows according the elevation. The best part about this view is that it extends FrameLayout and it can be displayed on all the platforms of android since it’s available through the Support v7 library. Lets have a look at some of its properties:", "To start off lets understand what exactly Android CardView is? Its a new widget for Android, which can be used to display a card sort of a layout in android. As you may know Android material design is inspired from paper and ink concept. Mostly it displays views on top of each other, with shadows. In simple terms, Android CardView is such a view which has all material design properties, most importantly showing shadows according the elevation. The best part about this view is that it extends FrameLayout and it can be displayed on all the platforms of android since it’s available through the Support v7 library. Lets have a look at some of its properties:"};
    private String mResourcesQ[];
    private String mResourcesA[];
    private TypedArray mImages;

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public VerticlePagerAdapter(Context context, String[] que, String[] ans, TypedArray mImages) {
        mContext = context;
        mResourcesQ = que;
        mResourcesA = ans;
        this.mImages = mImages;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mResourcesA.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View itemView = mLayoutInflater.inflate(R.layout.view_pager_1, container, false);

        TextView ques = itemView.findViewById(R.id.textViewQues);
        TextView ans = itemView.findViewById(R.id.textViewAns);
        ImageView image = itemView.findViewById(R.id.imageView);

//        int pos = position%mResourcesA.length;

        ques.setText(mResourcesQ[position]);
        ans.setText(mResourcesA[position]);
        image.setImageResource(mImages.getResourceId(position, -1));
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((CardView) object);
    }

}


