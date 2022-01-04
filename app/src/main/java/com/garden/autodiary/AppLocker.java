package com.garden.autodiary;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AppLocker extends BaseActivity {
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
                            finish();
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
        String pw;
        sqlDB = myHelper.getReadableDatabase();

        Cursor cursor;
        cursor = sqlDB.rawQuery("SELECT * FROM pwTBL", null);

        cursor.moveToFirst();

        pw = cursor.getString(1);

        sqlDB.close();
        cursor.close();

        return inputpw.equals(pw.substring(1, 5));
    }
}
