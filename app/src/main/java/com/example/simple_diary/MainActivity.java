package com.example.simple_diary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity{

    private long backKeyPressedTime = 0;
    private Toast toast;
    myDBHelper myHelper;
    SQLiteDatabase sqlDB;
    ArrayList<String> items = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button goDiary, goTodo;
        TextView text1;

        setTitle("TD");

        Calendar c = Calendar.getInstance(); // 오늘 날짜를 받음

        goDiary = (Button) findViewById(R.id.goDiary);
        goTodo = (Button) findViewById(R.id.goTodo);
        text1 = (TextView) findViewById(R.id.text1);

        text1.setText(c.get(Calendar.MONTH)+1 + "월" + c.get(Calendar.DATE) + "일");

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

        text1.setOnClickListener(new View.OnClickListener() {
            Calendar c = Calendar.getInstance(); // 오늘 날짜를 받음
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), c.get(Calendar.YEAR) +"-"+ (c.get(Calendar.MONTH)+1) +"-"+ c.get(Calendar.DATE), Toast.LENGTH_SHORT).show();
            }
        });

        //오늘 할 일 출력
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, items);
        myHelper = new myDBHelper(this);

        final ListView listview = (ListView) findViewById(R.id.todoView);
        listview.setAdapter(adapter);

        String sqlSelect = "SELECT * FROM groupTBL WHERE gDate=" + "'" + c.get(Calendar.YEAR) + "" + c.get(Calendar.MONTH) + "" + c.get(Calendar.DATE) + "'";
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
            ActivityCompat.finishAffinity(this);
            toast.cancel();
        }
    }

    public class myDBHelper extends SQLiteOpenHelper {
        public myDBHelper(Context context) {
            super(context, "todoDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE groupTBL (gdbId String PRIMARY KEY, gDate String , gContent String, gCheck BOOLEAN);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS groupTBL");
            onCreate(db);
        }
    }
}
