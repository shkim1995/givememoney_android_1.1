package com.teamverman.givememoney;

/**
 * Created by ickhyun on 2017-02-12.
 */



/*---------------------------------------------------------
파일 저장 형식

세훈
수호
승우
덕현
병현
***
10 : last Index
1 : host
1 : baggers
2
3
*
100000
치
(반복)




---------------------------------------------------------*/

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.lang.Math;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.kakao.kakaolink.*;


import com.google.android.gms.ads.AdRequest;


/**
 * Created by 김세훈 on 2017-02-11.
 */
public class MainActivity extends Activity {

    InputMethodManager inputMethodManager;

    String fileName;

    //player정보
    ArrayList<String> playerName;
    int playerNum;
    int sum = 0; // sum of arrows


    //원 (graphic) 정보
    static Circles[] circles;

    //채무관계 정보 [i][j] : i가 j에게
    static int[][] payment;
    static int[][] payment_new;

    //mode 정보 (normal - selected)
    boolean isNormalMode = true;
    int host = -1;
    boolean[] isSelected;

    boolean isChanged = false;

    int small_rad;
    int big_rad;
    int center_x;
    int center_y;
    int gap;

    //Layouts
    RelativeLayout mainLayout;
    LinearLayout menuTopNormal;
    LinearLayout menuTopSelected;
    LinearLayout menuButNormal;
    LinearLayout menuButSelected;
    CenterView centerView;
    LinearLayout nameField;

    //Buttons
    Button backBtn;
    Button frontBtn;
    Button logBtn;
    Button shareBtn;
    Button changeBtn;
    Button selectAllBtn;
    Button deselectAllBtn;
    Button cancelBtn;
    Button insertButton;
    Button tutorialBtn;
    Button saveBtn;

    //Texts
    EditText editText;
    EditText editText2;
    TextView text1;
    TextView text2;
    TextView mode_text;


    //debugs
    Button btnDeb;
    Button btnDeb2;
    boolean debugMode;
    int num_recursion;

    static PaymentLog log;


    final double RANDOM_NUM = 0.15;

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

    //////////////////////내부 클래스/////////////////////////////////////
    class Circles {
        int x, y, r;
        Rect rec;
        String name;

        Circles() {
            x = y = r = 0;
            rec = new Rect(0, 0, 0, 0);
        }

        Circles(int x, int y, int r) {
            this.x = x;
            this.y = y;
            this.r = r;
            rec = new Rect(x - r, y - r, x + r, y + r);
        }
    }

    class Info{
        int host;
        ArrayList<Integer> baggers;
        int pay;
        String reason;
    }

    class PaymentLog{
        ArrayList<Info> log;
        int lastIndex;

        PaymentLog(){
            log = new ArrayList<Info>();
            lastIndex = -1;
        }

        Info get(int i){
            return log.get(i);
        }

        void push_back(Info info){
            //뒤에 값 제거
            for(int i=log.size()-1; i>lastIndex; i--){
                log.remove(i);
            }
            lastIndex++;
            log.add(info);
        }

        int size(){
            return lastIndex+1;
        }

        boolean frontable(){
            if (log.size()!=lastIndex+1)
                return true;
            return false;
        }

        boolean backable(){
            if(lastIndex!=-1)
                return true;
            return false;
        }
    }


    /////////////////////////////////////////////////////////////////////////



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        fileName="";

        inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);

        Intent intent = getIntent();
        playerName = intent.getStringArrayListExtra("NAMES");
        if(playerName!=null) {
            playerNum = playerName.size();
        }
        else{
            Intent temp_intent =  new Intent(this, TitleActivity.class);
            startActivity(temp_intent);
            finish();
        }
        payment = new int[playerNum][playerNum];
        for(int i=0; i<playerNum; i++)
            for(int j=0; j<playerNum; j++)
                payment[i][j] = 0;

        payment_new = new int[playerNum][playerNum];
        for(int i=0; i<playerNum; i++)
            for(int j=0; j<playerNum; j++)
                payment_new[i][j] = 0;

        isSelected = new boolean[playerNum];
        for(int i=0; i<playerNum; i++)
            isSelected[i] = false;


        sum = playerNum*playerNum;

        //DEBUG INITIALIZATION
        debugMode = false;
        num_recursion = 0;

        btnDeb = (Button)findViewById(R.id.menu_deb);
        btnDeb2 = (Button)findViewById(R.id.menu_deb2);

        btnDeb.setVisibility(View.INVISIBLE);
        btnDeb.setEnabled(false);


        btnDeb2.setVisibility(View.INVISIBLE);
        btnDeb2.setEnabled(false);


        //LAYOUTS INITIALIZATION
        mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        menuTopNormal = (LinearLayout) findViewById(R.id.menu_top_normal);
        menuTopSelected = (LinearLayout) findViewById(R.id.menu_top_selected);
        menuButNormal = (LinearLayout) findViewById(R.id.menu_bot_normal);
        menuButSelected = (LinearLayout) findViewById(R.id.menu_bot_selected);

        menuTopSelected.setVisibility(View.INVISIBLE);
        menuButSelected.setVisibility(View.INVISIBLE);

        nameField = (LinearLayout)findViewById(R.id.name_field) ;
        nameField.setVisibility(View.INVISIBLE);

        //BUTTONS INITIALIZATION
        backBtn = (Button)findViewById(R.id.menu_back_btn);
        frontBtn = (Button)findViewById(R.id.menu_front_btn);
        logBtn = (Button)findViewById(R.id.menu_log_btn);
        shareBtn = (Button)findViewById(R.id.menu_share_btn);
        changeBtn = (Button)findViewById(R.id.change_btn);
        selectAllBtn = (Button)findViewById(R.id.select_all_btn);
        deselectAllBtn = (Button)findViewById(R.id.deselect_all_btn);
        cancelBtn = (Button)findViewById(R.id.cancel_btn);
        insertButton = (Button)findViewById(R.id.menu_insert_btn);
        tutorialBtn = (Button)findViewById(R.id.menu_tutorial_btn);
        saveBtn = (Button)findViewById(R.id.menu_save_btn);
        initializeButtons();

        backBtn.setBackgroundResource(R.drawable.arrow_1_de);
        backBtn.setEnabled(false);
        frontBtn.setBackgroundResource(R.drawable.arrow_2_de);
        frontBtn.setEnabled(false);

        //Text Initialization
        editText = (EditText)findViewById(R.id.menu_edittext);
        editText2 = (EditText)findViewById(R.id.menu_edittext2);
        text1 = (TextView)findViewById(R.id.text1);
        text2 = (TextView)findViewById(R.id.text2);
        mode_text = (TextView)findViewById(R.id.main_mode_name);
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "fonts/TmonMonsori.ttf");
        text1.setTypeface(typeFace);
        text2.setTypeface(typeFace);
        text1.setTextColor(Color.GRAY);
        text2.setTextColor(Color.BLACK);
        mode_text.setTypeface(typeFace);
        mode_text.setTextColor(Color.GRAY);
        mode_text.setText("간략화 전");

        //DRAW CENTER GRAPHICS
        centerView = new CenterView(this);
        mainLayout.addView(centerView);

        log = new PaymentLog();

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

        //if reseted
        boolean reseted = intent.getBooleanExtra("RESET", false);
        if(reseted){
            String fileNameTemp = intent.getStringExtra("FILE");
            if(fileNameTemp!=null) {
                //Toast.makeText(MainActivity.this, fileNameTemp, Toast.LENGTH_SHORT).show();
                fileName = fileNameTemp;
                readDataFromFile(fileName);
                //Toast.makeText(MainActivity.this, fileName, Toast.LENGTH_SHORT).show();
                renew_payment();

                centerView.invalidate();

                //back, front btn activation
                frontBackBtnSwitch();

            }

        }


    }

    public boolean onKeyDown(int keyCode, KeyEvent event){

        final Intent intent = new Intent(this, NameActivity.class);

        if(keyCode==KeyEvent.KEYCODE_BACK) {
            if(isNormalMode) {
                String alertTitle = "뒤로 가시겠습니까?";
                String buttonMessage = "뒤로 갈 경우 현재 상태가 저장/복원되지 않습니다!!";
                String btnYes = "네";
                String btnNo = "아니오";

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(alertTitle)
                        .setMessage(buttonMessage)
                        .setPositiveButton(btnYes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton(btnNo, null)
                        .show();
                for (int i = 0; i < playerNum; i++) {
                    isSelected[i] = false;
                }
            }
            else {
                for (int i = 0; i < playerNum; i++) {
                    isSelected[i] = false;
                }
                host = -1;
                centerView.invalidate();
                switchMenus();
            }
            return true;
        }
        return false;
    }

    void switchMenus(){
        if(!isNormalMode) {
            menuTopSelected.setVisibility(View.INVISIBLE);
            menuButSelected.setVisibility(View.INVISIBLE);
            menuTopNormal.setVisibility(View.VISIBLE);
            menuButNormal.setVisibility(View.VISIBLE);

            nameField.setVisibility(View.INVISIBLE);
            isNormalMode = true;
            inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
            centerView.invalidate();
        }

        else{

            menuTopSelected.setVisibility(View.VISIBLE);
            menuButSelected.setVisibility(View.VISIBLE);
            menuTopNormal.setVisibility(View.INVISIBLE);
            menuButNormal.setVisibility(View.INVISIBLE);

            nameField.setVisibility(View.VISIBLE);
            text2.setText(playerName.get(host));
            isNormalMode = false;
            centerView.invalidate();
        }
    }

    void frontBackBtnSwitch(){

        if(log.backable()) {
            backBtn.setBackgroundResource(R.drawable.arrow_1);
            backBtn.setEnabled(true);
        }
        else {
            backBtn.setBackgroundResource(R.drawable.arrow_1_de);
            backBtn.setEnabled(false);
        }

        if(log.frontable()) {
            frontBtn.setBackgroundResource(R.drawable.arrow_2);
            frontBtn.setEnabled(true);
        }
        else {
            frontBtn.setBackgroundResource(R.drawable.arrow_2_de);
            frontBtn.setEnabled(false);
        }
    }

    void initializeButtons() {

        final Intent intentPop = new Intent(this, LogPopup.class);
        final Intent intentTutorial = new Intent(this, TutPopup.class);

        //insert Btn
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int payment;
                String reason;

                //채무자가 아무도 없을 때
                int numBaggers = 0;
                for (int i = 0; i < playerNum; i++)
                    if (isSelected[i])
                        numBaggers++;
                if (numBaggers == 0) {
                    Toast.makeText(MainActivity.this, "빚쟁이들을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                }


                //숫자 입력 오류
                try {
                    payment = Integer.parseInt(editText.getText().toString());
                    reason = editText2.getText().toString();
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "숫자를 입력해 주세요!", Toast.LENGTH_SHORT).show();
                    return;
                }

                editText.setText("");
                editText2.setText("");


                //log update

                Info newinfo = new Info();
                newinfo.pay = payment;
                newinfo.reason = reason;

                ArrayList<Integer> baggers = new ArrayList<Integer>();
                for (int i = 0; i < playerNum; i++) {
                    if (isSelected[i] == true) {
                        isSelected[i] = false;
                        baggers.add(i);
                    }
                }
                newinfo.baggers = baggers;
                newinfo.host = host;
                host = -1;

                log.push_back(newinfo);
                renew_payment();
                centerView.invalidate();

                // mode change
                switchMenus();

                //back, front btn activation
                frontBackBtnSwitch();

                if (randomEvent(RANDOM_NUM)) {
                    displayInterstitial();
                }
            }
        });

        //select All Btn
        selectAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < playerNum; i++) {
                    isSelected[i] = true;
                }
                centerView.invalidate();
            }
        });

        //deselect All Btn
        deselectAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < playerNum; i++) {
                    isSelected[i] = false;
                }
                centerView.invalidate();
            }
        });

        //cancel Btn
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < playerNum; i++) {
                    isSelected[i] = false;
                }
                host = -1;
                centerView.invalidate();
                switchMenus();
            }
        });

        //change Btn
        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isChanged) {
                    isChanged = true;
                    changeBtn.setText("되돌리기!");
                    frontBtn.setEnabled(false);
                    backBtn.setEnabled(false);
                    change_payment_3();
                    mode_text.setTextColor(Color.BLACK);
                    mode_text.setText("간략화 후");
                    centerView.invalidate();
                    return;
                } else {
                    isChanged = false;
                    changeBtn.setText("간략하게!!");
                    if (log.lastIndex != 0)
                        backBtn.setEnabled(true);
                    if (log.frontable())
                        frontBtn.setEnabled(true);
                    renew_payment();
                    mode_text.setTextColor(Color.GRAY);
                    mode_text.setText("간략화 전");
                    centerView.invalidate();
                }
            }
        });

        //frontBtn
        frontBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (log.frontable()) {
                    log.lastIndex++;
                    renew_payment();
                    centerView.invalidate();
                    frontBackBtnSwitch();
                }
            }
        });

        //back Btn
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("ASDSDSDSDA", "SDSADSAD");
                if (log.backable()) {
                    log.lastIndex--;
                    renew_payment();
                    centerView.invalidate();
                    frontBackBtnSwitch();
                }
            }
        });


        //log Btn
        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentPop.putExtra("NAMES", playerName);
                startActivity(intentPop);
            }
        });

        //share Btn
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                shareKakao();
            }
        });

        //tutorial Btn
        tutorialBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intentTutorial);
            }
        });

        //save Btn
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData(fileName);
            }
        });

        btnDeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    String data = "";
                    FileInputStream fis = openFileInput("name_list.txt");
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(fis));
                    String str = buffer.readLine();
                    while (str != null) {
                        data = data + ", " + str;
                        str = buffer.readLine();
                    }
                    Log.v("DEBUG_SAVE", data);
                    buffer.close();
                } catch (Exception e) {
                    //Log.v("DEBUG_SVE", "exception");
                    e.printStackTrace();
                }

            }
        });

        btnDeb2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                debugMode = !debugMode;
                Toast.makeText(MainActivity.this, debugMode ? "Debug Mode" : "Normal Mode", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /////////////////////////////////KAKAO FTN//////////////////////////////////////////////////////

    public void shareKakao()
    {
        try{
            final KakaoLink kakaoLink = KakaoLink.getKakaoLink(this);
            final KakaoTalkLinkMessageBuilder kakaoBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();

        /*메시지 추가*/
            String text = "----돈 정산 목록----\n";

            for(int i=0; i<playerNum; i++){
                boolean line = false;
                for(int j=0; j<playerNum; j++){
                    if(payment[i][j]>0){
                        line = true;
                    }
                }
                if(line)
                    text = text+"\n";
                for(int j=0; j<playerNum; j++){
                    if(payment[i][j]>0){
                        text = text+playerName.get(i)+" -> "+playerName.get(j)+" : "+payment[i][j]+"원\n";
                    }
                }
            }

            kakaoBuilder.addText(text);

        /*앱 실행버튼 추가*/
            kakaoBuilder.addAppButton("앱 실행 혹은 다운로드");

        /*메시지 발송*/
            kakaoLink.sendMessage(kakaoBuilder, this);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    /////////////////////////////SAVING FUNCTION////////////////////////////////

    void saveData(String str){

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("저장하시겠습니까?");
        alert.setMessage("파일 명을 입력하세요.");

        final EditText name = new EditText(this);
        name.setText(str);
        name.setHint("파일 명 입력 (10자 이내)");
        alert.setView(name);

        alert.setPositiveButton("저장", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //입력값이 없는 경우
                if(name.getText().toString().equals("")){
                    Toast.makeText(MainActivity.this, "파일 명을 입력해 주세요!", Toast.LENGTH_SHORT).show();
                    saveData("");
                }
                else if(name.getText().toString().length()>10){
                    Toast.makeText(MainActivity.this, "파일 명이 너무 깁니다!", Toast.LENGTH_SHORT).show();
                    saveData(name.getText().toString());
                }
                //입력값이 올바른 경우
                else{
                    String fileName = name.getText().toString();
                    if(isStringInFile(fileName)){
                        sameFileName(fileName);
                    }
                    else{
                        saveFile(fileName, true);
                    }
                }

            }
        });

        alert.setNegativeButton("취소",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alert.show();
    }

    void saveFile(String str, boolean dup){

        //이름 추가
        if(dup) {
            try {
                FileOutputStream fos = openFileOutput("name_list.txt", Context.MODE_APPEND);
                PrintWriter out = new PrintWriter(fos);


                out.println(str);
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //파일 추가
        try {
            deleteFile(str+".txt");

        } catch (Exception e) {
            e.printStackTrace();
        }

        try{
            FileOutputStream fos = openFileOutput(str+".txt", Context.MODE_APPEND);
            //Toast.makeText(MainActivity.this, "MAKE FILE with "+str, Toast.LENGTH_SHORT).show();
            PrintWriter out = new PrintWriter(fos);

            ///////////FTN/////////////

            //사용자 목록
            for(int i=0; i<playerNum; i++){
                out.println(playerName.get(i));
            }
            out.println("***");

            //last index
            out.println(log.lastIndex);

            //log
            for(int i=0; i<log.log.size(); i++){
                out.println(log.get(i).host);
                for(int j=0; j<log.get(i).baggers.size(); j++){
                    out.println(log.get(i).baggers.get(j));
                }
                out.println("*");
                out.println(log.get(i).pay);
                out.println(log.get(i).reason);
            }

            out.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    void readDataFromFile(String fileName){

        log = new PaymentLog();
        int index = -1;

        try {
            FileInputStream fis = openFileInput(fileName+".txt");
            BufferedReader buffer = new BufferedReader(new InputStreamReader(fis));
            String str = buffer.readLine();
            while (str!=null && !str.equals("***") && !str.equals("")) {
                str = buffer.readLine();
            }

            str = buffer.readLine();

            log.lastIndex = -1;
            //last Index
            try {
                index = Integer.parseInt(str);
            } catch (Exception e) {
                e.printStackTrace();
            }


            str = buffer.readLine();

            while(str!=null){
                Info tempInfo = new Info();

                //host
                try {
                    tempInfo.host = Integer.parseInt(str);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                str = buffer.readLine();

                tempInfo.baggers = new ArrayList<Integer>();
                while(!str.equals("*")){
                    try {
                        tempInfo.baggers.add(Integer.parseInt(str));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    str = buffer.readLine();
                }

                str = buffer.readLine();

                try {
                    tempInfo.pay = Integer.parseInt(str);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                tempInfo.reason = str;

                str = buffer.readLine();

                log.push_back(tempInfo);

                tempInfo.reason = str;

                str = buffer.readLine();

            }

            log.lastIndex = index;


            //Toast.makeText(MainActivity.this, "SIZE "+log.size()+" "+log.lastIndex+" "+log.log.size(), Toast.LENGTH_SHORT).show();
        }

        catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean isStringInFile(String testStr){
        try{
            StringBuffer data = new StringBuffer();
            FileInputStream fis = openFileInput("name_list.txt");
            BufferedReader buffer = new BufferedReader(new InputStreamReader(fis));
            String str = buffer.readLine();
            while(str!=null){
                if(str.equals(testStr))
                    return true;
                str = buffer.readLine();
            }
            buffer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }


    void sameFileName(final String fileName){

        String alertTitle="파일명 확인";
        String buttonMessage = "겹치는 파일명이 있습니다.\n 덮어쓰시겠습니까?";

        String btnYes = "네";
        String btnNo = "아니오";

        new AlertDialog.Builder(MainActivity.this)
                .setTitle(alertTitle)
                .setMessage(buttonMessage)
                .setPositiveButton(btnYes, new DialogInterface.OnClickListener() {
                    @Override
                    //덮어쓰지 않는 경우 - 돌아가기
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveFile(fileName, false);
                    }
                })
                .setNegativeButton(btnNo, new DialogInterface.OnClickListener() {
                    @Override
                    //덮어쓰는 경우 - 저장 함수 호출
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveData(fileName);
                    }
                })
                .show();
    }



    /////////////////////////PAYMENT PUNCTIONS/////////////////////////////////

    int round(int n){
        if(n%10<5)
            return (n/10)*10;
        return (n/10+1)*10;
    }

    void renew_payment() {

        for (int i = 0; i < playerNum; i++)
            for (int j = 0; j < playerNum; j++)
                payment[i][j] = 0;

        //일단 우겨넣자!
        for (int i = 0; i < log.size(); i++) {
            int host = log.get(i).host;

            int num_bagger = log.get(i).baggers.size();
            int money = round(log.get(i).pay / num_bagger);
            for (int j = 0; j < num_bagger; j++) {
                int bagger = log.get(i).baggers.get(j);
                if (host == bagger) continue;
                payment[bagger][host] += money;
            }
        }

        //뒷정리
        for (int i = 0; i < playerNum; i++) {
            for (int j = i + 1; j < playerNum; j++) {
                //음수 처리

                //대소 비교
                if (payment[i][j] >= payment[j][i]) {
                    payment[i][j] = payment[i][j] - payment[j][i];
                    payment[j][i] = 0;
                } else if (payment[i][j] <= payment[j][i]) {
                    payment[j][i] = payment[j][i] - payment[i][j];
                    payment[i][j] = 0;
                }
            }
        }
    }

    void change_payment(){
        while(true){
            boolean temp3 = false;

            for(int i=0; i<playerNum; i++){
                boolean temp2 = false;

                for(int j=0; j<playerNum; j++){
                    boolean temp = false;

                    if(payment[i][j]>0){
                        for(int k=0; k<playerNum; k++){
                            if(payment[j][k]>0){//삼각관계 존재
                                temp=true;
                                if(payment[i][j]<=payment[j][k]){
                                    payment[j][k] -= payment[i][j];
                                    payment[i][k] += payment[i][j];
                                    payment[i][j] = 0;
                                }
                                else{
                                    payment[i][k] += payment[j][k];
                                    payment[i][j] -= payment[j][k];
                                    payment[j][k] = 0;
                                }
                                break;//k문 탈출
                            }
                        }
                    }
                    if(temp){
                        temp2 = true;
                        break;//j문 탈출
                    }
                }
                if(temp2){
                    temp3 = true;
                    break; //i문 탈출
                }
            }
            if(temp3==false)
                break;

        }
    }

    int getSum(){
        int sum = 0;
        for(int i=0; i<playerNum; i++){
            for(int j=0; j<playerNum; j++){
                if(payment[i][j]>0)
                    sum++;
            }
        }
        return sum;
    }

    void copyPayment(int[][] x, int[][] y){ //x->y
        for(int i=0; i<playerNum; i++){
            for(int j=0; j<playerNum; j++){
                y[i][j] = x[i][j];
            }
        }
    }

    void change_payment_3(){
//        int[] before = new int[playerNum];
//        int[] after = new int[playerNum];
//
//        for(int i=0; i<playerNum; i++){
//            for(int j=0; j<playerNum; j++){
//                if(payment[i][j]>0) {
//                    before[j] += payment[i][j];
//                    before[i] -= payment[i][j];
//                }
//            }
//        }

        //////////////

        payment_new = new int[playerNum][playerNum];
        for(int i=0; i<playerNum; i++)
            for(int j=0; j<playerNum; j++)
                payment_new[i][j] = 0;

        int[] pay = new int[playerNum];
        for(int i=0; i<playerNum; i++){
            for(int j=0; j<playerNum; j++){
                if(payment[i][j]>0) {
                    pay[j] += payment[i][j];
                    pay[i] -= payment[i][j];
                }
            }
        }

        while(true){
            boolean temp = true;

            for(int i=0; i<playerNum; i++) {
                if (pay[i] > 0) {
                    temp = false;
                    break;
                }

            }
            if(temp)
                break;

            int min = 0;
            int max = 0;
            int minInd = -1;
            int maxInd = -1;
            for(int i=0; i<playerNum; i++) {
                if(pay[i]>max) {
                    max = pay[i];
                    maxInd = i;
                }
                if(pay[i]<min) {
                    min = pay[i];
                    minInd = i;
                }
            }

            int moneyFlow = max;
            if(-min>max)
                moneyFlow = -min;

            payment_new[minInd][maxInd] = moneyFlow;
            pay[minInd] += moneyFlow;
            pay[maxInd] -= moneyFlow;
        }

        copyPayment(payment_new, payment);

        ////////////

//        for(int i=0; i<playerNum; i++){
//            for(int j=0; j<playerNum; j++){
//                if(payment[i][j]>0) {
//                    after[j] += payment[i][j];
//
//                    after[i] -= payment[i][j];
//                }
//            }
//        }
//
//        boolean temp = true;
//        int x = -1;
//        for(int i=0; i<playerNum; i++) {
//            if (before[i] != after[i]) {
//                x = i;
//                temp = false;
//                break;
//            }
//        }
//        Toast.makeText(MainActivity.this, (temp ? "true" : "false")+" "+num_recursion, Toast.LENGTH_SHORT).show();
    }

    ///////////////////////////////////////////////////////////////////////

    ///////////////////////////MAIN GRAPHICS///////////////////////////////

    class CenterView extends View {
        Context con;

        public CenterView(Context c){
            super(c);
            con = c;
        }

        public void initCircles(){
            circles = new Circles[playerNum];
            double theta = 2*Math.PI/playerNum;
            for(int i=0; i<playerNum; i++){
                int x = (int)(center_x+big_rad*Math.sin(i*theta));
                int y = (int)(center_y-big_rad*Math.cos(i*theta));
                circles[i] = new Circles(x, y, small_rad);
                circles[i].name = playerName.get(i);
            }
        }

        public double length(double x1, double y1, double x2, double y2){
            return Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
        }

        public void drawArrow(Canvas canvas, int from, int to, boolean b, int money){

            int small_len = canvas.getWidth()/30;
            double theta_var = Math.PI/7;
            double x1 = circles[from].x;
            double x2 = circles[to].x;
            double y1 = circles[from].y;
            double y2 = circles[to].y;
            double x0 = 0.5*(x1+x2);
            double y0 = 0.5*(y1+y2);


            double total_len = length(x1, y1, x2, y2);
            double len = total_len-2*small_rad;

            double xx1 = ((total_len-len)*x0+len*x1)/total_len;
            double xx2 = ((total_len-len)*x0+len*x2)/total_len;
            double yy1 = ((total_len-len)*y0+len*y1)/total_len;
            double yy2 = ((total_len-len)*y0+len*y2)/total_len;
            double x3 = xx2+(xx1-xx2)*small_len/len;
            double y3 = yy2+(yy1-yy2)*small_len/len;

            double x4 = x3+(yy1-yy2)*0.5*small_len/len;
            double x5 = x3-(yy1-yy2)*0.5*small_len/len;
            double y4 = y3-(xx1-xx2)*0.5*small_len/len;
            double y5 = y3+(xx1-xx2)*0.5*small_len/len;

            Paint paint = new Paint();
            paint.setStrokeWidth(4);
            paint.setColor(Color.GRAY);
            if(b==false) {
                canvas.drawLine((int) xx1, (int) yy1, (int) xx2, (int) yy2, paint);
                canvas.drawLine((int) xx2, (int) yy2, (int) x4, (int) y4, paint);
                canvas.drawLine((int) xx2, (int) yy2, (int) x5, (int) y5, paint);
            }
            else{
                paint.setTextSize(50);
                Path path = new Path();
                path.reset();
                path.moveTo((int)xx2, (int)yy2);
                path.lineTo((int)xx1, (int)yy1);

                canvas.drawLine((int) xx1, (int) yy1, (int) xx2, (int) yy2, paint);
                canvas.drawLine((int) xx2, (int) yy2, (int) x4, (int) y4, paint);
                canvas.drawLine((int) xx2, (int) yy2, (int) x5, (int) y5, paint);

                paint.setColor(Color.BLACK);
                String temp = "     ";
                if(playerNum==8)
                    temp = "  ";
                if(playerNum==7)
                    temp = "   ";
                canvas.drawTextOnPath(temp+money, path, 0, 0, paint);
            }
        }

        //click 위치 반환
        public int findRects(int x, int y){
            //사용자 찾기 -> 0~num-1
            for(int i=0; i<playerNum; i++){
                if(circles[i].rec.contains(x, y))
                    return i;
            }
            //없으면 -1
            return -1;
        }

        public void onDraw(Canvas canvas) {
            center_x = canvas.getWidth()/2;
            center_y = canvas.getHeight()/2;
            if(playerNum<6)
                small_rad = (int)(canvas.getWidth()/11);
            else if(playerNum==6)
                small_rad = (int)(canvas.getWidth()/12.5);
            else if(playerNum==7)
                small_rad = (int)(canvas.getWidth()/13);
            else
                small_rad = (int)(canvas.getWidth()/14);
            gap = small_rad/3;
            big_rad = center_x-small_rad-gap;

            ///////////동그라미////////////////
            initCircles();

            for(int i=0; i<playerNum; i++) {
                Bitmap icon;
                if(!isSelected[i]) {
                    icon = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.player_nor);
                }
                else {
                    icon = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.player_sel);
                }

                canvas.drawBitmap(icon, null, circles[i].rec, null);
            }


            //////////////이름///////////////
            for(int i=0; i<playerNum; i++){
                //이름 출력
                Paint paint = new Paint();
                paint.setColor(Color.WHITE);
                int size = 0;
                do {
                    paint.setTextSize(++ size);
                } while(paint.measureText(circles[i].name) < small_rad*1.7 && size<60);
                Rect rt = new Rect();
                paint.getTextBounds(circles[i].name, 0, circles[i].name.length(), rt);
                paint.setColor(Color.WHITE);

                canvas.drawText(circles[i].name, circles[i].x-rt.width()/2, circles[i].y-rt.top/2, paint);

            }

            ////////////////화살표//////////////

            if(isNormalMode){
                for (int i = 0; i < playerNum; i++) {
                    for (int j = 0; j < playerNum; j++) {
                        if (payment[i][j] > 0)
                            drawArrow(canvas, i, j, true, payment[i][j]);
                    }
                }
            }
        }

        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                int x = (int) event.getX();
                int y = (int) event.getY();
                int index = findRects(x, y);

                if(index<0)
                    return  true;

                if(isChanged){
                    return true;
                }

                //if Normal Mode
                if(isNormalMode) {
                    host = index;
                    switchMenus();
                }

                //if Select Mode
                else if(!isNormalMode){
                    isSelected[index] = !isSelected[index];
                    invalidate();
                }
                return true;
            }
            return false;
        }
    }
}
