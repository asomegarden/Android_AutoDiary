package com.garden.todoplusdiary;

import android.content.Context;
import android.content.Intent;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity { //공통으로 들어가는 메소드들 모음
    myDBHelper myHelper;
    SQLiteDatabase sqlDB;
    private long Apppausetime = System.currentTimeMillis();
    @Override
    public void onBackPressed() { //뒤로 가기 누르면 홈화면으로
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
        onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Apppausetime = System.currentTimeMillis(); //액티비티가 정지되면 시간 기록
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (System.currentTimeMillis() >= Apppausetime + 30000) { //재개될 때 30초 이상이 지났으면

            myHelper = new myDBHelper(this);
            sqlDB = myHelper.getReadableDatabase();

            Cursor cursor;
            cursor = sqlDB.rawQuery("SELECT * FROM pwTBL", null);

            cursor.moveToFirst();
            if(Boolean.parseBoolean(cursor.getString(2))) { //활성화 여부 읽어오고
                sqlDB.close();
                Intent intent = new Intent(getApplicationContext(), AppLocker.class); //잠금화면 실행
                startActivity(intent);
            }
        }
    }
    public static class myDBHelper extends SQLiteOpenHelper { //투두 테이블, 패스워드 테이블 생성
        public myDBHelper(Context context) {
            super(context, "todoDB", null, 1);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE groupTBL (gdbId String PRIMARY KEY, gDate String , gContent String, gCheck BOOLEAN);");
            db.execSQL("CREATE TABLE pwTBL (gId Integer PRIMARY KEY, gPw String , gEnable BOOLEAN);");
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS groupTBL");
            db.execSQL("DROP TABLE IF EXISTS pwTBL");
            onCreate(db);
        }
    }
}

