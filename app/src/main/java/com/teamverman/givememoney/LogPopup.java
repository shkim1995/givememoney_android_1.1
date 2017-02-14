package com.teamverman.givememoney;

/**
 * Created by ickhyun on 2017-02-12.
 */
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;

/**
 * Created by 김세훈 on 2017-02-11.
 */
public class LogPopup extends Activity {
    ArrayList<String> playerName;
    LinearLayout window;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.log_popup);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int w = dm.widthPixels;
        int h = dm.heightPixels;

        getWindow().setLayout((int) (w * 0.88), (int) (h * 0.70));


        Intent intent = getIntent();
        playerName = intent.getStringArrayListExtra("NAMES");

        window = (LinearLayout)findViewById(R.id.log_popup);

        for(int i=0; i<MainActivity.log.size(); i++)
            makeText(i);
    }

    void makeText(int index){
        String host = playerName.get(MainActivity.log.get(index).host);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/TmonMonsori.ttf");

        ArrayList<Integer> temp = MainActivity.log.get(index).baggers;
        ArrayList<String> baggers = new ArrayList<String>();
        if(temp.size()==playerName.size())
            baggers.add("모두");
        else {
            for (int i = 0; i < temp.size(); i++) {
                baggers.add(playerName.get(temp.get(i)));
            }
        }
        int payment = MainActivity.log.get(index).pay;
        String reason = MainActivity.log.get(index).reason;


        TextView tv1 =  (TextView) View.inflate(this, R.layout.log_text, null );
        TextView tv2 =  (TextView) View.inflate(this, R.layout.log_text, null );
        TextView tv3 =  (TextView) View.inflate(this, R.layout.log_text, null );

        String text = +(index+1)+". 돈낸 사람 : "+host;
        if(!reason.equals(""))
            text = text+" ("+reason+")";
        tv1.setText(text);
        tv1.setTypeface(typeFace);
        tv1.setTextSize(20);
        tv1.setTextColor(Color.BLACK);
        window.addView(tv1);

        text = "빚진 사람들 : ";
        for(int i=0; i<baggers.size(); i++)
            text = text+baggers.get(i)+" ";
        tv2.setText(text);
        tv2.setTypeface(typeFace);
        tv2.setTextSize(15);
        tv2.setTextColor(Color.GRAY);
        window.addView(tv2);

        text = "빚진 총 금액 : "+payment+"원\n";
        if(index!=playerName.size()-1){
            text = text+"\n";
        }
        tv3.setText(text);
        tv3.setTypeface(typeFace);
        tv3.setTextSize(15);
        tv3.setTextColor(0xFFB22222);
        window.addView(tv3);



    }

}