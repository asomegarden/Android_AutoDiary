package com.garden.todoplusdiary;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class BaseActivity extends AppCompatActivity { //공통으로 들어가는 메소드들 모음
    myDBHelper myHelper;
    SQLiteDatabase sqlDB;
    final String PREFNAME = "Preferences";

    protected long appPauseTime = System.currentTimeMillis();

    @Override
    protected void onPause() {
        super.onPause();
        appPauseTime = System.currentTimeMillis(); //액티비티가 정지되면 시간 기록
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (System.currentTimeMillis() >= appPauseTime + 30000) { //재개될 때 30초 이상이 지났으면

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
            cursor.close();
        }
    }

    //현재 포커스 되지 않은 다른 뷰를 클릭하면 자동으로 키보드 내려가게
    //https://ohdbjj.tistory.com/7`
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) ev.getX(), y = (int) ev.getY();
            if (!rect.contains(x, y)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private Toast toast;
    private long backKeyPressedTime = 0;

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
    public static class myDBHelper extends SQLiteOpenHelper { //투두 테이블, 패스워드 테이블 생성
        public myDBHelper(Context context) {
            super(context, "todoDB", null, 1);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE groupTBL (gdbId String PRIMARY KEY, gDate String , gContent String, gCheck BOOLEAN);");
            db.execSQL("CREATE TABLE pwTBL (gId Integer PRIMARY KEY, gPw String , gEnable BOOLEAN);");
            db.execSQL("CREATE TABLE DATE (ID Integer PRIMARY KEY, Date String);");
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS groupTBL");
            db.execSQL("DROP TABLE IF EXISTS pwTBL");
            db.execSQL("DROP TABLE IF EXISTS DATE");
            onCreate(db);
        }
    }
}

