package com.garden.todoplusdiary;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PwSetting extends AppCompatActivity {
    myDBHelper myHelper;
    SQLiteDatabase sqlDB;
    TextView pw1, pw2, pw3, pw4, guide;
    Button btnDel;
    int cnt = 1, set = 1;
    String inputpw = "", temp = "";
    Button[] numButton = new Button[10];
    Integer[] numBtnIDs = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3,
            R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pwsetting);

        pw1 = (TextView) findViewById(R.id.pw1);
        pw2 = (TextView) findViewById(R.id.pw2);
        pw3 = (TextView) findViewById(R.id.pw3);
        pw4 = (TextView) findViewById(R.id.pw4);
        guide = (TextView) findViewById(R.id.guide);

        pw1.setInputType(0);
        pw2.setInputType(0);
        pw3.setInputType(0);
        pw4.setInputType(0);

        btnDel = (Button) findViewById(R.id.btnDel);
        myHelper = new myDBHelper(this);

        for(int i=0; i<numBtnIDs.length; i++){
            numButton[i] = (Button) findViewById(numBtnIDs[i]);
        }
        for(int i=0; i<numBtnIDs.length; i++)
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
                        if(set == 1)
                        {
                            if(checkpw(inputpw)){
                                pw1.setText("");
                                pw2.setText("");
                                pw3.setText("");
                                pw4.setText("");
                                cnt = 1;
                                guide.setTextColor(Color.rgb(0, 0, 0));
                                guide.setText("새로운 비밀번호를 입력하세요");
                                inputpw = "";
                                set = 2;
                            }
                            else
                            {
                                pw1.setText("");
                                pw2.setText("");
                                pw3.setText("");
                                pw4.setText("");
                                cnt = 1;
                                guide.setText("비밀번호가 일치하지 않습니다");
                                inputpw = "";
                                guide.setTextColor(Color.rgb(157, 1, 1));
                            }
                        }
                        else if(set == 2)
                        {
                            temp = "1" + inputpw;
                            pw1.setText("");
                            pw2.setText("");
                            pw3.setText("");
                            pw4.setText("");
                            inputpw = "";
                            cnt = 1;
                            set = 3;
                            guide.setText("다시 한 번 입력하세요");
                        }
                        else if(set == 3)
                        {
                            pw1.setText("");
                            pw2.setText("");
                            pw3.setText("");
                            pw4.setText("");
                            cnt = 1;
                            if(temp.substring(1, 5).equals(inputpw))
                            {
                                guide.setTextColor(Color.rgb(0, 0, 0));
                                guide.setText("비밀번호 설정이 완료되었습니다.");
                                setpw("1" + inputpw);
                                set = 4;
                                Handler mHandler = new Handler();
                                mHandler.postDelayed(new Runnable()  {
                                    public void run() {
                                        Intent intent = new Intent(getApplicationContext(), Options.class);
                                        startActivity(intent);
                                        onStop();
                                    }
                                }, 1000); // 0.5초후

                            }
                            else
                            {
                                pw1.setText("");
                                pw2.setText("");
                                pw3.setText("");
                                pw4.setText("");
                                cnt = 1;
                                guide.setText("비밀번호가 일치하지 않습니다");
                                inputpw = "";
                                guide.setTextColor(Color.rgb(157, 1, 1));
                            }
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
                    inputpw = "";
                }
                if(cnt==3)
                {
                    cnt--;
                    pw2.setText("");
                    inputpw = inputpw.substring(0, 1);
                }
                if(cnt==4)
                {
                    cnt--;
                    pw3.setText("");
                    inputpw = inputpw.substring(0, 2);
                }
                if(cnt==5)
                {
                    cnt--;
                    pw4.setText("");
                    inputpw = inputpw.substring(0, 3);
                }
            }
        });
    }
    public Boolean checkpw(String inputpw){
        String pw = "";
        sqlDB = myHelper.getReadableDatabase();

        Cursor cursor;
        cursor = sqlDB.rawQuery("SELECT * FROM pwTBL", null);

        cursor.moveToFirst();

        pw = cursor.getString(1);

        sqlDB.close();

        if(inputpw.equals(pw.substring(1, 5))) return true;
        else return false;
    }
    public Boolean setpw(String inputpw){
        sqlDB = myHelper.getWritableDatabase();

        int Id = 1;
        Boolean enable = true;
        sqlDB.execSQL("INSERT OR REPLACE INTO pwTBL VALUES('"+Id+"', '"+inputpw+"','"+enable+"');");
        sqlDB.close();

        return true;
    }
    public class myDBHelper extends SQLiteOpenHelper {
        public myDBHelper(Context context) {
            super(context, "todoDB", null, 1);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE pwTBL (gId Integer PRIMARY KEY, gPw String , gEnable BOOLEAN);");
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS pwTBL");
            onCreate(db);
        }
    }
}
