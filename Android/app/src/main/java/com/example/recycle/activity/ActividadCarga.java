package com.example.recycle.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.annotation.Nullable;

import com.example.recycle.R;

import java.util.concurrent.TimeUnit;

import static java.lang.System.currentTimeMillis;

public class ActividadCarga extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {
                setContentView(R.layout.carga_main);
            }

            public void onFinish() {
                finish();
            }
        }.start();

    }
}
