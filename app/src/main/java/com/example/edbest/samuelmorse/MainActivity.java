package com.example.edbest.samuelmorse;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView aTextView1;
    private TextView aTextView2;
    private TextView aTextView3;
    private TextView aTextView4;
    private TextView aTextView5;
    private TextView aCorrectionTextView1;
    private TextView aCorrectionTextView2;
    private TextView aCorrectionTextView3;
    private TextView aCorrectionTextView4;
    private TextView aCorrectionTextView5;
    private TextView theLetterTextView;
    private int aBlipCounter = 0;
    private int aTimer = 0;
    private long downTime;
    private long upTime;
    private long theTimeDifference;
    private static long LONG_CLICK_LENGTH = 200;
    private static String THE_KEY_NAME_IN_STATE ="MORSEKEY";
    private static int SHORT_WIDTH = 20;
    private static int LONG_WIDTH = 40;
    private static int SHORT_SOUND_LENGTH = 100;
    private static int LONG_SOUND_LENGTH = 300;
    private HashMap aHashmapOfMorse = new HashMap();
    private String aCurrentMorseKey;
    private ToneGenerator aToneGenerator;
    private String shortBlip = ".";
    private String longBlip = "-";
    private long TIMER_SECONDS = 5000;
    private CountDownTimer aCountDownTimer;
    private boolean readyForInput;
    private boolean timerIsRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setMorseHashmap();
        aTextView1 = (TextView) findViewById(R.id.blip1);
        aTextView2 = (TextView) findViewById(R.id.blip2);
        aTextView3 = (TextView) findViewById(R.id.blip3);
        aTextView4 = (TextView) findViewById(R.id.blip4);
        aTextView5 = (TextView) findViewById(R.id.blip5);
        aCorrectionTextView1 = (TextView) findViewById(R.id.correctionblip1);
        aCorrectionTextView2 = (TextView) findViewById(R.id.correctionblip2);
        aCorrectionTextView3 = (TextView) findViewById(R.id.correctionblip3);
        aCorrectionTextView4 = (TextView) findViewById(R.id.correctionblip4);
        aCorrectionTextView5 = (TextView) findViewById(R.id.correctionblip5);
        //aTableRow = (TableRow)findViewById(R.id.linespacer);
        initTheWholeThing();

        aToneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, 70);
        ImageView anImageView = (ImageView)findViewById(R.id.telegraphid);
        anImageView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                openImage();
            }
        });
        TextView aTextView = (TextView)findViewById(R.id.theletter);
        aTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                executeValidation(0);
            }
        });
        goGetAMorseKey(savedInstanceState);
    }


    private void openImage(){
        Intent myIntent = new Intent(this, MorseImageActivity.class);
        startActivity(myIntent);
    }

    public boolean onTouchEvent(MotionEvent aMotionEvent) {
        if (readyForInput) {
            int anActionCode = aMotionEvent.getAction();
            if (anActionCode == MotionEvent.ACTION_DOWN) {
                downTime = System.currentTimeMillis();
                ImageView anImageView = (ImageView) findViewById(R.id.telegraphid);
                anImageView.setBackgroundColor(Color.parseColor("#FFFF00"));
            } else if (anActionCode == MotionEvent.ACTION_UP) {
                upTime = System.currentTimeMillis();
                ImageView anImageView = (ImageView) findViewById(R.id.telegraphid);
                anImageView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                if (upTime - downTime > LONG_CLICK_LENGTH) {
                    setCurrentBlip(false);
                } else {
                    setCurrentBlip(true);
                }
            }
        }
        return true;
    }

    private void setCurrentBlip(boolean isShort) {
        int aWidth;
        if (isShort) {
            aWidth = SHORT_WIDTH;
            aToneGenerator.startTone(ToneGenerator.TONE_CDMA_ONE_MIN_BEEP, SHORT_SOUND_LENGTH);
        } else {
            aWidth = LONG_WIDTH;
            aToneGenerator.startTone(ToneGenerator.TONE_CDMA_ONE_MIN_BEEP, LONG_SOUND_LENGTH);
        }

        switch (aBlipCounter) {
            case 0:
                aTextView1.setWidth(aWidth);
                if (readyForInput) {
                    aCountDownTimer = null;
                    startTimer();
                }
                break;
            case 1:
                aTextView2.setWidth(aWidth);
                break;
            case 2:
                aTextView3.setWidth(aWidth);
                break;
            case 3:
                aTextView4.setWidth(aWidth);
                break;
            default:
                aTextView5.setWidth(aWidth);
                break;
        }
        if (aBlipCounter < 4) {
            aBlipCounter++;
        } else {
            aBlipCounter = 0;
            executeValidation(aWidth);
        }
    }

    private void executeValidation(int aWidth) {
        readyForInput = false;
        if (aCountDownTimer != null) {
            aCountDownTimer.cancel();
        }
        if (isValidEntries(aWidth)) {
            //aTableRow.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), "It matches!  Good job.", Toast.LENGTH_SHORT).show();
            initTheWholeThing();
            goGetAMorseKey(null);
        } else {
            readyForInput = false;
            Toast.makeText(getApplicationContext(), "You suck eggs!  Why don't you quit trying?", Toast.LENGTH_SHORT).show();
            new CountDownTimer(5000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    timerIsRunning = true;
                }

                @Override
                public void onFinish() {
                    initTheWholeThing();
                    aBlipCounter = 0;
                    readyForInput = true;
                }

            }.start();
        }
    }

    private void startTimer() {
        /*
        if (aCountDownTimer == null) {
            aCountDownTimer = new CountDownTimer(TIMER_SECONDS, 1) {

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    executeValidation(0);
                }

            }.start();
        }
        */
    }

    private String convertZeroWidthsToEmpties(int anInt){
        String aReturnValue = "";
        if (anInt != 0){ aReturnValue = String.valueOf(anInt); }
        return aReturnValue;
    }

    private boolean isValidEntries(int theLastWidth) {
        String theValue = (String)aHashmapOfMorse.get(aCurrentMorseKey);
        String theHashCompareValue = "";
        String theTestValue = convertZeroWidthsToEmpties(aTextView1.getWidth())  +
                convertZeroWidthsToEmpties(aTextView2.getWidth()) +
                convertZeroWidthsToEmpties(aTextView3.getWidth()) +
                convertZeroWidthsToEmpties(aTextView4.getWidth()) +
                convertZeroWidthsToEmpties(theLastWidth);
        int theCompareWidth;
        initTheCorrections();
        for (int aCounter = 0; aCounter < theValue.length(); aCounter++){
            char aChar = theValue.charAt(aCounter);
            if (String.valueOf(aChar).equals(shortBlip)) {
                theCompareWidth = SHORT_WIDTH;
            }
            else{
                theCompareWidth = LONG_WIDTH;
            }
            switch (aCounter) {
                case 0:
                    aCorrectionTextView1.setWidth(theCompareWidth);
                    break;
                case 1:
                    aCorrectionTextView2.setWidth(theCompareWidth);
                    break;
                case 2:
                    aCorrectionTextView3.setWidth(theCompareWidth);
                    break;
                case 3:
                    aCorrectionTextView4.setWidth(theCompareWidth);
                    break;
                default:
                    aCorrectionTextView5.setWidth(theCompareWidth);
                    break;
            }
            theHashCompareValue += theCompareWidth;
        }
        if (theHashCompareValue.equals(theTestValue)) {
            //initTheWholeThing();
            //goGetAMorseKey();
            return true;
        }
        else{
            return false;
        }
    }
    private void initTheWholeThing(){
        readyForInput = true;
        timerIsRunning = false;
        initMyAnswers();
        initTheCorrections();
        downTime = 0;
        upTime = 0;

    }
    private void initMyAnswers(){
        aTextView1.setWidth(0);
        aTextView2.setWidth(0);
        aTextView3.setWidth(0);
        aTextView4.setWidth(0);
        aTextView5.setWidth(0);
        aBlipCounter = 0;
        initTheCorrections();
    }
    private void initTheCorrections(){
        aCorrectionTextView1.setWidth(0);
        aCorrectionTextView2.setWidth(0);
        aCorrectionTextView3.setWidth(0);
        aCorrectionTextView4.setWidth(0);
        aCorrectionTextView5.setWidth(0);
    }
    private void goGetAMorseKey(Bundle savedInstanceState){
        theLetterTextView = (TextView) findViewById(R.id.theletter);
        if (savedInstanceState != null) {
            String aMorseKey = savedInstanceState.getString(THE_KEY_NAME_IN_STATE, "");
            theLetterTextView.setText(aMorseKey);
            aCurrentMorseKey = aMorseKey;
        }
        else {
            List<Object> valuesList = new ArrayList<Object>(aHashmapOfMorse.keySet());
            Collections.shuffle(valuesList);

            for (Object obj : valuesList) {
                aCurrentMorseKey = obj.toString();
            }
            theLetterTextView.setText(aCurrentMorseKey);
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Make sure to call the super method so that the states of our views are saved
        super.onSaveInstanceState(outState);
        theLetterTextView = (TextView) findViewById(R.id.theletter);
        outState.putString(THE_KEY_NAME_IN_STATE, theLetterTextView.getText().toString());
    }

    private void setMorseHashmap(){
        aHashmapOfMorse.put("A",".-");
        aHashmapOfMorse.put("B","-...");
        aHashmapOfMorse.put("C","-.-.");
        aHashmapOfMorse.put("D","-..");
        aHashmapOfMorse.put("E",".");
        aHashmapOfMorse.put("F","..-.");
        aHashmapOfMorse.put("G","--.");
        aHashmapOfMorse.put("H","....");
        aHashmapOfMorse.put("I","..");
        aHashmapOfMorse.put("J",".---");
        aHashmapOfMorse.put("K","-.-");
        aHashmapOfMorse.put("L",".-..");
        aHashmapOfMorse.put("M","--");
        aHashmapOfMorse.put("N","-.");
        aHashmapOfMorse.put("O","---");
        aHashmapOfMorse.put("P",".--.");
        aHashmapOfMorse.put("Q","--.-");
        aHashmapOfMorse.put("R",".-.");
        aHashmapOfMorse.put("S","...");
        aHashmapOfMorse.put("T","-");
        aHashmapOfMorse.put("U","..-");
        aHashmapOfMorse.put("V","...-");
        aHashmapOfMorse.put("W",".--");
        aHashmapOfMorse.put("X","-..-");
        aHashmapOfMorse.put("Y","-.--");
        aHashmapOfMorse.put("Z","--..");
        aHashmapOfMorse.put("1",".----");
        aHashmapOfMorse.put("2","..---");
        aHashmapOfMorse.put("3","...--");
        aHashmapOfMorse.put("4","....-");
        aHashmapOfMorse.put("5",".....");
        aHashmapOfMorse.put("6","-....");
        aHashmapOfMorse.put("7","--...");
        aHashmapOfMorse.put("8","---..");
        aHashmapOfMorse.put("9","----.");
        aHashmapOfMorse.put("0","-----");
    }

}

