package com.garden.todoplusdiary;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class AppLocker extends AppCompatActivity {
    myDBHelper myHelper;
    SQLiteDatabase sqlDB;
    final String PREFNAME = "Preferences";
    private long backKeyPressedTime = 0;
    private Toast toast;
    TextView pw1, pw2, pw3, pw4;
    Button btnDel;
    int cnt = 1;
    String inputpw = "";
    final Button[] numButton = new Button[10];
    final Integer[] numBtnIDs = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3,
            R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applocker);
        pw1 = (TextView) findViewById(R.id.pw1);
        pw2 = (TextView) findViewById(R.id.pw2);
        pw3 = (TextView) findViewById(R.id.pw3);
        pw4 = (TextView) findViewById(R.id.pw4);

        pw1.setInputType(0);
        pw2.setInputType(0);
        pw3.setInputType(0);
        pw4.setInputType(0);

        btnDel = (Button) findViewById(R.id.btnDel);
        myHelper = new myDBHelper(this);

        for(int i=0; i<numBtnIDs.length; i++){
            numButton[i] = (Button) findViewById(numBtnIDs[i]);
        }
        for(int i=0; i<numBtnIDs.length; i++) // 버튼 입력 시
        {
            final int index;
            index = i;
            numButton[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(cnt == 1)
                    {
                        pw1.setText("●");
                        inputpw += numButton[index].getText().toString();
                        cnt++;
                    }
                    else if(cnt == 2)
                    {
                        pw2.setText("●");
                        inputpw += numButton[index].getText().toString();
                        cnt++;
                    }
                    else if(cnt == 3)
                    {
                        pw3.setText("●");
                        inputpw += numButton[index].getText().toString();
                        cnt++;
                    }
                    else if(cnt == 4)
                    {
                        pw4.setText("●");
                        inputpw += numButton[index].getText().toString();
                        if(checkpw(inputpw)) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            onStop();
                        }
                        else{
                            cnt=1;
                            pw1.setText("");
                            pw2.setText("");
                            pw3.setText("");
                            pw4.setText("");
                            inputpw = "";
                        }
                    }
                }
            });
        }
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cnt==2)
                {
                    cnt--;
                    pw1.setText("");
                }
                if(cnt==3)
                {
                    cnt--;
                    pw2.setText("");
                }
                if(cnt==4)
                {
                    cnt--;
                    pw3.setText("");
                }
                if(cnt==5)
                {
                    cnt--;
                    pw4.setText("");
                }
            }
        });
    }
    public Boolean checkpw(String inputpw){
        String pw;
        sqlDB = myHelper.getReadableDatabase();

        Cursor cursor;
        cursor = sqlDB.rawQuery("SELECT * FROM pwTBL", null);

        cursor.moveToFirst();

        pw = cursor.getString(1);

        sqlDB.close();

        return inputpw.equals(pw.substring(1, 5));
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

            editor.putBoolean("isFirstTime", true);
            editor.apply();
            ActivityCompat.finishAffinity(this);
            toast.cancel();
        }
    }
    public static class myDBHelper extends SQLiteOpenHelper {
        public myDBHelper(Context context) {
            super(context, "todoDB", null, 1);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE pwTBL (gId Integer PRIMARY KEY, gPw String , gEnable BOOLEAN);"); //pw 테이블 생성
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS pwTBL");
            onCreate(db);
        }
    }
}
