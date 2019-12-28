package com.zuccessful.trueharmony.utilities;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class VerticalViewPager extends ViewPager {

    public VerticalViewPager(Context context) {
        super(context);
        init();
    }

    public VerticalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // The majority of the magic happens here
//        setPageTransformer(true, new VerticalPageTransformer());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // there are problems with this on 4.0.3, probably also on 4.1
            setPageTransformer(true, new VerticalPageTransformer());
        }
        // The easiest way to get rid of the overscroll drawing that happens on the left and right
        setOverScrollMode(OVER_SCROLL_ALWAYS);
    }

    private class VerticalPageTransformer implements ViewPager.PageTransformer {
//        private static final float MIN_SCALE = 0.75f;
//        @Override
//        public void transformPage(View view, float position) {
//
//            if (position < -1) { // [-Infinity,-1)
//                // This page is way off-screen to the left.
//                view.setAlpha(0);
//
//            }  else if (position <= 0) { // [-1,0]
//                // Use the default slide transition when moving to the left page
//                view.setAlpha(1);
//                // Counteract the default slide transition
//                view.setTranslationX(view.getWidth() * -position);
//
//                //set Y position to swipe in from top
//                float yPosition = position * view.getHeight();
//                view.setTranslationY(yPosition);
//                view.setScaleX(1);
//                view.setScaleY(1);
//
//            } else if (position <= 1) { // [0,1]
//                view.setAlpha(1);
//
//                // Counteract the default slide transition
//                view.setTranslationX(view.getWidth() * -position);
//
//
//                // Scale the page down (between MIN_SCALE and 1)
//                float scaleFactor = MIN_SCALE
//                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
//                view.setScaleX(scaleFactor);
//                view.setScaleY(scaleFactor);
//
//            } else { // (1,+Infinity]
//                // This page is way off-screen to the right.
//                view.setAlpha(0);
//            }
//        }
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    view.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    view.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                view.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }

    /**
     * Swaps the X and Y coordinates of your touch event.
     */
//    private MotionEvent swapXY(MotionEvent ev) {
//        float width = getWidth();
//        float height = getHeight();
//
//        float newX = (ev.getY() / height) * width;
//        float newY = (ev.getX() / width) * height;
//
//        ev.setLocation(newX, newY);
//
//        return ev;
//    }
//
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev){
//        boolean intercepted = super.onInterceptTouchEvent(swapXY(ev));
//        swapXY(ev); // return touch coordinates to original reference frame for any child views
//        return intercepted;
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        return super.onTouchEvent(swapXY(ev));
//    }
}
