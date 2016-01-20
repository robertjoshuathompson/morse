package com.example.edbest.samuelmorse;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;

public class MorseImageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_morse_image);
    }
    public boolean onTouchEvent(MotionEvent aMotionEvent) {
        finish();
        return true;
    }
}
