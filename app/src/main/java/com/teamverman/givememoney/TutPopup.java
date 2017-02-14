package com.teamverman.givememoney;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by ickhyun on 2017-02-13.
 */

public class TutPopup extends Activity {

    int pageNum;

    ViewPager viewPager;
    TextView tv;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.tutorial_popup);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int w = dm.widthPixels;
        int h = dm.heightPixels;

        getWindow().setLayout((int) (w * 0.91), (int) (h * 0.70));



        viewPager = (ViewPager) findViewById(R.id.vp);
        ImageAdapter adapter = new ImageAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                                              @Override
                                              public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                                                  int x = viewPager.getCurrentItem();
                                                  tv.setText((x+1)+" / 9");
                                              }

                                              @Override
                                              public void onPageSelected(int position) {

                                              }

                                              @Override
                                              public void onPageScrollStateChanged(int state) {
                                                  int x = viewPager.getCurrentItem();
                                                  tv.setText((x+1)+" / 9");
                                              }
                                          }

        );
        tv = (TextView)findViewById(R.id.tut_page);

        pageNum=1;

    }

    class ImageAdapter extends PagerAdapter {

        Context context;
        Bitmap galImage;
        BitmapFactory.Options options;
        private final int[] galImages = new int[] {

                R.drawable.tut1,
                R.drawable.tut2,
                R.drawable.tut3,
                R.drawable.tut4,
                R.drawable.tut5,
                R.drawable.tut6,
                R.drawable.tut7,
                R.drawable.tut8,
                R.drawable.tut9
        };

        ImageAdapter(Context context) {
            this.context = context;
            options = new BitmapFactory.Options();
        }

        @Override
        public int getCount() {
            return galImages.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((ImageView) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(context);

            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            options.inSampleSize = 1;
            galImage = BitmapFactory.decodeResource(context.getResources(), galImages[position], options);

            imageView.setImageBitmap(galImage);
            ((ViewPager) container).addView(imageView, 0);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((ImageView) object);
        }


        public void onPageScrollStateChanged(int state) {
        }
    }


}
