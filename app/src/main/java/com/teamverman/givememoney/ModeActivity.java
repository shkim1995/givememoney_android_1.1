package com.teamverman.givememoney;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;


/**
 * Created by ickhyun on 2017-02-14.
 */

public class ModeActivity extends Activity {
    Intent intent;
    Intent intent2;
    int clicked = 0;

    Button btn_renew;
    Button btn_reset;

    InterstitialAd interstitialAd;
    InterstitialAd interstitialAd2;

    BackPressCloseHandler backPressCloseHandler;

    public void displayInterstitial() {
        if (interstitialAd.isLoaded()) {

            interstitialAd.show();
        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest2 = new AdRequest.Builder()
                .addTestDevice("1A66417BC5450C8887755FEB37D48889")
                .build();
        interstitialAd.loadAd(adRequest2);
    }

    public void displayInterstitial2() {
        if (interstitialAd2.isLoaded()) {

            interstitialAd2.show();
        }
    }

    private void requestNewInterstitial2() {
        AdRequest adRequest3 = new AdRequest.Builder()
                .addTestDevice("1A66417BC5450C8887755FEB37D48889")
                .build();
        interstitialAd2.loadAd(adRequest3);
    }


    public boolean randomEvent(double rand){
        if(rand>1)
            return true;
        if(rand<0)
            return false;
        double temp = Math.random();
        Log.v("AAAAA", "" + temp);

        if(temp>1-0.5*rand) {
            return true;
        }
        if(temp<0.5*rand)
            return true;
        return false;
    }


    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_modes);

        btn_renew = (Button)findViewById(R.id.mode_renew);
        btn_reset = (Button)findViewById(R.id.mode_reset);

        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/TmonMonsori.ttf");
        btn_renew.setTypeface(typeFace);
        btn_reset.setTypeface(typeFace);

        intent = new Intent(this, NameActivity.class);
        intent2 = new Intent(this, ResetActivity.class);

        backPressCloseHandler = new BackPressCloseHandler(this);

        //광고
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.ad_unit_id));
        AdRequest adRequest2 = new AdRequest.Builder().addTestDevice("1A66417BC5450C8887755FEB37D48889").build();
        interstitialAd.loadAd(adRequest2);
        // start Ads
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                startActivity(intent);
                finish();
            }
        });

        interstitialAd2 = new InterstitialAd(this);
        interstitialAd2.setAdUnitId(getResources().getString(R.string.ad_unit_id));
        AdRequest adRequest3 = new AdRequest.Builder().addTestDevice("1A66417BC5450C8887755FEB37D48889").build();
        interstitialAd2.loadAd(adRequest3);
        // start Ads
        interstitialAd2.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                startActivity(intent2);
                finish();
            }
        });

        requestNewInterstitial();
        requestNewInterstitial2();

        btn_renew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(randomEvent(0.5)) {
                    displayInterstitial();
                }
                else{
                    startActivity(intent);
                    finish();
                }
            }
        });

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(randomEvent(0.5)) {
                    displayInterstitial2();
                }
                else{
                    startActivity(intent2);
                    finish();
                }
            }
        });

    }

    /////////////뒤로가기 2번 CLASS/////////////////

    public class BackPressCloseHandler {
        private long backKeyPressedTime = 0;
        private Toast toast;

        private Activity activity;

        public BackPressCloseHandler(Activity context) {
            this.activity = context;
        }

        public void onBackPressed() {
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                showGuide();
                return;
            }
            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                toast.cancel();

                Intent t = new Intent(activity, MainActivity.class);
                activity.startActivity(t);

                activity.moveTaskToBack(true);
                activity.finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        }

        public void showGuide() {
            toast = Toast.makeText(activity, "한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
