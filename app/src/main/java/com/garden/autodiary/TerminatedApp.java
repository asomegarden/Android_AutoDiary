package com.garden.autodiary;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class TerminatedApp extends Service {
    final String PREFNAME = "Preferences";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("Error","onTaskRemoved - 강제 종료 " + rootIntent);
        // 여기에 필요한 코드를 추가한다.,
        SharedPreferences settings = getSharedPreferences(PREFNAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putBoolean("Appexec", true);
        editor.apply();
        stopSelf();
    }
}
