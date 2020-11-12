package com.garden.todoplusdiary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class AppLocker extends AppCompatActivity {
    final String PREFNAME = "Preferences";
    private long backKeyPressedTime = 0;
    private Toast toast;
    TextView pw1, pw2, pw3, pw4;
    Button btnDel;
    int cnt = 1;
    String inputpw = "";
    Button[] numButton = new Button[10];
    Integer[] numBtnIDs = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3,
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
    public Boolean checkpw(String pw){
        if(pw.equals("1111")) return true;
        else return false;
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
}
