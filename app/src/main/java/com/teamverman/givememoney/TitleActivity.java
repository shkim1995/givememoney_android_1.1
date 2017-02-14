package com.teamverman.givememoney;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class TitleActivity extends Activity {

    InterstitialAd interstitialAd;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_title);

       final Intent intent = new Intent(this, ModeActivity.class);


        RelativeLayout rel = (RelativeLayout)findViewById(R.id.title_relative);
        rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
                finish();
            }
        });


    }



}