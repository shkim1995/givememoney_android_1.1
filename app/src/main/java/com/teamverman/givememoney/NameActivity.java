package com.teamverman.givememoney;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.graphics.Typeface;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.plus.model.people.Person;

/**
 * Created by ickhyun on 2017-02-12.
 */

public class NameActivity  extends Activity {

    final int MIN_NUM = 2;
    final int MAX_NUM = 8;

    final double RANDOM_NUM = 0.10;

    InputMethodManager inputMethodManager;

    LinearLayout mainLayout;
    Button insertBtn;
    Button nextBtn;
    EditText editText;
    ListView listView;

    int playerNum = 0;
    ArrayList<String> playerName = new ArrayList<String>();
    MyListAdapter myAdapter;

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

    public void onBackPressed() {
        final Intent intent = new Intent(this, ModeActivity.class);
        startActivity(intent);
        finish();
    }

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_names);

        /////////전면 광고//////////

        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.ad_unit_id));
        AdRequest adRequest2 = new AdRequest.Builder().addTestDevice("1A66417BC5450C8887755FEB37D48889").build();
        interstitialAd.loadAd(adRequest2);
        // start Ads
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });


        requestNewInterstitial();


        /////////////////////////////

        inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        mainLayout = (LinearLayout)findViewById(R.id.names_main_layout);
        insertBtn = (Button)findViewById(R.id.names_insert_btn);
        nextBtn = (Button)findViewById(R.id.name_next_btn);
        editText = (EditText)findViewById(R.id.names_edittext);
        listView = (ListView)findViewById(R.id.name_listview);

        nextBtn.setText("다음 단계로!! (참여자 : "+playerNum+"명)");
        nextBtn.setEnabled(false);

        myAdapter = new MyListAdapter(this, R.layout.name_list, playerName);
        listView.setAdapter(myAdapter);

        final Intent intent = new Intent(this, MainActivity.class);

        //intent from ResetActivity
        Intent intentGet = getIntent();
        String fileName = intentGet.getStringExtra("NAME");


        //Reset에서 불러진 경우!!!
        if(fileName!=null) {
            readNamesFromFile(fileName);
            playerNum = playerName.size();
            myAdapter.notifyDataSetChanged();
            editText.setText("");
            nextBtn.setText("다음 단계로!! (참여자 : "+playerNum+"명)");
            if(playerNum>=2)
                nextBtn.setEnabled(true);

            intent.putExtra("NAMES", playerName);
            intent.putExtra("RESET", true);
            intent.putExtra("FILE", fileName);
            fileName = null;
            startActivity(intent);

        }
        ///////////banner 광고///////////

        AdView mAdView_name = (AdView) findViewById(R.id.adView_name);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("1A66417BC5450C8887755FEB37D48889").build();
        mAdView_name.loadAd(adRequest);

        /////////////////////////////////


        //hide keyboard when touching the screen
        mainLayout.setOnTouchListener(new View.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent e){
                if(e.getAction()==MotionEvent.ACTION_DOWN){
                    inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        insertBtn.setOnClickListener(nameBtnTouch);

        nextBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(playerNum<MIN_NUM){
                    Toast.makeText(NameActivity.this, "혼자서는 돈정산할 수 없습니다!", Toast.LENGTH_SHORT).show();
                    return;
                }
                intent.putExtra("NAMES", playerName);
                intent.putExtra("RESET", false);
                startActivity(intent);
            }
        });
//        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/TmonMonsori.ttf");
//        insertBtn.setTypeface(typeFace);
    }



    Button.OnClickListener nameBtnTouch = new Button.OnClickListener(){
        public void onClick(View v){

            if(playerNum>MAX_NUM-1){
                Toast.makeText(NameActivity.this, "참여 인원이 8명을 넘을 수 없습니다 ㅠㅠ", Toast.LENGTH_SHORT).show();
                return;
            }
            if(editText.getText().toString().length()>4){
                Toast.makeText(NameActivity.this, "이름이 너무 깁니다! 4자 이내로 입력 바랍니다.", Toast.LENGTH_SHORT).show();
                return;
            }
            if(editText.getText().toString().length()==0){
                Toast.makeText(NameActivity.this, "이름을 입력해 주세요!", Toast.LENGTH_SHORT).show();
                return;
            }

            if(randomEvent(RANDOM_NUM))
                displayInterstitial();

            playerNum++;
            playerName.add(editText.getText().toString());
            myAdapter.notifyDataSetChanged();

            editText.setText("");
            nextBtn.setText("다음 단계로!! (참여자 : "+playerNum+"명)");
            if(playerNum>=2)
                nextBtn.setEnabled(true);

        }
    };


    void readNamesFromFile(String fileName){
        try {
            FileInputStream fis = openFileInput(fileName+".txt");
            BufferedReader buffer = new BufferedReader(new InputStreamReader(fis));
            String str = buffer.readLine();
            while (true) {
                if(str==null || str.equals("***") || str.equals(""))
                    break;
                playerName.add(str);
                str = buffer.readLine();
            }
            buffer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    /////////////LIST ADAPTER CLASS////////////////

    class  MyListAdapter extends BaseAdapter {
        Context context;
        LayoutInflater inflater;
        ArrayList<String> arrlist;
        int layout;

        public MyListAdapter(Context c, int l, ArrayList<String> arr){
            context = c;
            layout = l;
            arrlist = arr;
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public int getCount(){
            return arrlist.size();
        }

        public String getItem(int pos){
            return arrlist.get(pos)+"";
        }

        public long getItemId(int pos){
            return pos;
        }

        public View getView(int position, View convertView, ViewGroup parent){
            final int pos = position;
            if(convertView==null){
                convertView = inflater.inflate(layout, parent, false);
            }

            //set name text
            TextView txt = (TextView) convertView.findViewById(R.id.namelist_name);
            txt.setText(""+arrlist.get(pos));
            Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/TmonMonsori.ttf");
            txt.setTypeface(typeFace);
            if(pos%2==0)
                txt.setTextColor(Color.BLACK);
            else
                txt.setTextColor(Color.GRAY);

            //set button
            Button btn = (Button)convertView.findViewById(R.id.namelist_btn);
            btn.setOnClickListener(new Button.OnClickListener(){
                public void onClick(View v){
                    playerName.remove(pos);
                    myAdapter.notifyDataSetChanged();
                    playerNum--;
                    nextBtn.setText("다음 단계로!! (참여자 : "+playerNum+"명)");
                    if(playerNum<2)
                        nextBtn.setEnabled(false);
                }
            });

            //set backgroud - touchout
            convertView.findViewById(R.id.namelist_layout).setOnTouchListener(new View.OnTouchListener(){
                public boolean onTouch(View v, MotionEvent e){
                    if(e.getAction()==MotionEvent.ACTION_DOWN){
                        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                        return true;
                    }
                    return false;
                }
            });


            return convertView;
        }


    }


}