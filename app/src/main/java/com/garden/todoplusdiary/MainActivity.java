package com.garden.todoplusdiary;

import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends BaseActivity{

    final int PERMISSIONS_REQUEST_CODE = 1;
    final String PREFNAME = "Preferences";
    final String PREFNAME0 = "Pref";
    private long backKeyPressedTime = 0;
    private Toast toast;

    final ArrayList<String> items = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myHelper = new myDBHelper(this);

        startService(new Intent(this, TerminatedApp.class));

        isFirstTime();
        Appexec();

        requestPermission();

        Button goDiary, goTodo, goDiaryList, btnset;
        TextView text1;

        setTitle("TD");

        Calendar c = Calendar.getInstance(); // 오늘 날짜를 받음

        goDiary = (Button) findViewById(R.id.goDiary);
        goTodo = (Button) findViewById(R.id.goTodo);
        goDiaryList = (Button) findViewById(R.id.goDiarylist);
        btnset = (Button) findViewById(R.id.btnset);
        text1 = (TextView) findViewById(R.id.text1);

        text1.setText(c.get(Calendar.MONTH)+1 + "월 " + c.get(Calendar.DATE) + "일");

        goDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Diary.class);
                startActivity(intent);
                onStop();
            }
        });

        goTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Todo.class);
                startActivity(intent);
                onStop();
            }
        });

        goDiaryList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DiaryList.class);
                startActivity(intent);
                onStop();
            }
        });

        btnset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Options.class);
                startActivity(intent);
                onStop();
            }
        });

        text1.setOnClickListener(new View.OnClickListener() {
            final Calendar c = Calendar.getInstance(); // 오늘 날짜를 받음
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), c.get(Calendar.YEAR) +"-"+ (c.get(Calendar.MONTH)+1) +"-"+ c.get(Calendar.DATE), Toast.LENGTH_SHORT).show();
            }
        });

        //오늘 할 일 출력
        final ArrayAdapter adapter = new ArrayAdapter(this, R.layout.listview_item_simple, items);

        final ListView listview = (ListView) findViewById(R.id.todoView);
        listview.setAdapter(adapter);

        String sqlSelect = "SELECT * FROM groupTBL WHERE gDate=" + "'" + c.get(Calendar.YEAR) + "" + (c.get(Calendar.MONTH) + 1) + "" + c.get(Calendar.DATE) + "'";
        int index = 0;
        sqlDB = myHelper.getReadableDatabase();
        Cursor cursor;
        cursor = sqlDB.rawQuery(sqlSelect, null);
        while (cursor.moveToNext()) {
            items.add(cursor.getString(2));
            index++;
        }
        adapter.notifyDataSetChanged();
        if(index == 0) items.add("오늘 할 일을 추가해보세요");
    }

    @Override
    public void onBackPressed() {
        
        // 기존 뒤로가기 버튼의 기능을 막기위해 주석처리 또는 삭제
        // super.onBackPressed();

        // 마지막으로 뒤로가기 버튼을 눌렀던 시간에 2초를 더해 현재시간과 비교 후
        // 마지막으로 뒤로가기 버튼을 눌렀던 시간이 2초가 지났으면 Toast Show
        // 2000 milliseconds = 2 seconds
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "뒤로 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        // 마지막으로 뒤로가기 버튼을 눌렀던 시간에 2초를 더해 현재시간과 비교 후
        // 마지막으로 뒤로가기 버튼을 눌렀던 시간이 2초가 지나지 않았으면 종료
        // 현재 표시된 Toast 취소
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            SharedPreferences settings = getSharedPreferences(PREFNAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();

            editor.putBoolean("Appexec", true);
            editor.apply();
            ActivityCompat.finishAffinity(this);
            toast.cancel();
        }
    }

    public void Appexec() {
        SharedPreferences settings = getSharedPreferences(PREFNAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        if (settings.getBoolean("Appexec", true)) {
            editor.putBoolean("Appexec", false);
            editor.apply();
            sqlDB = myHelper.getReadableDatabase();

            Cursor cursor;
            cursor = sqlDB.rawQuery("SELECT * FROM pwTBL", null);

            cursor.moveToFirst();
            if(Boolean.parseBoolean(cursor.getString(2))) {
                sqlDB.close();
                Intent intent = new Intent(getApplicationContext(), AppLocker.class);
                startActivity(intent);
            }

        }
    }

    public void isFirstTime() {
        SharedPreferences settings = getSharedPreferences(PREFNAME0, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        if (settings.getBoolean("isFirstTime", true)) {
            editor.putBoolean("isFirstTime", false);
            editor.apply();

            int Id = 1;
            String Pw = "1" + "0000";

            Boolean Enable = false;

            sqlDB = myHelper.getWritableDatabase();
            sqlDB.execSQL("INSERT INTO pwTBL VALUES('"+Id+"', '"+Pw+"','"+Enable+"');");
            sqlDB.close();
        }
    }

    private void requestPermission() {
        boolean shouldProviceRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE);//사용자가 이전에 거절한적이 있어도 true 반환

        if (shouldProviceRationale) {
            //앱에 필요한 권한이 없어서 권한 요청
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            //권한있을때.
            //오레오부터 꼭 권한체크내에서 파일 만들어줘야함
            makeDir();
        }
    }
    public void makeDir() {
        String root = getExternalFilesDir(null).getAbsolutePath(); //내장에 만든다
        String directoryName = "Capture";
        final File myDir = new File(root + "/" + directoryName);
        if (!myDir.exists()) {
            boolean wasSuccessful = myDir.mkdir();
            if (!wasSuccessful) {
                System.out.println("file: was not successful.");
            } else {
                System.out.println("file: 최초로 폴더 만듦." + root + "/" + directoryName);
            }
        } else {
            System.out.println("file: " + root + "/" + directoryName +"already exists");
        }
    }
}
