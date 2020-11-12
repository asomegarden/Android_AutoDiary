package com.garden.todoplusdiary;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.logging.Logger;

public class BaseActivity extends AppCompatActivity {
    private long Apppausetime = System.currentTimeMillis();
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
        onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Apppausetime = System.currentTimeMillis();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (System.currentTimeMillis() >= Apppausetime + 30000) {
            Intent intent = new Intent(getApplicationContext(), AppLocker.class);
            startActivity(intent);
        }
    }
}

