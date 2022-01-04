package com.garden.autodiary;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Calendar;

public class Diary extends BaseActivity {

    EditText edtDiary;
    Button btnDatePicker, btnDel;
    String fileName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        btnDel = (Button) findViewById(R.id.btnDel);
        edtDiary = (EditText) findViewById(R.id.edtDiary);
        btnDatePicker = (Button) findViewById(R.id.btnDatePicker);
        LinearLayout btnMenu = (LinearLayout) findViewById(R.id.btnMenu);
        RelativeLayout layoutDiary = (RelativeLayout) findViewById(R.id.layout_diary);

        Calendar c = Calendar.getInstance();

        if(getIntent().hasExtra("date")){
            String date = getIntent().getStringExtra("date");
            loadDiary(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(4, 6))-1, Integer.parseInt(date.substring(6, 8)));
        }else{
            loadDiary(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
        }


        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDate();
            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Diary.this, MenuActivity.class);
                startActivity(intent);
            }
        });

        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dlg = new AlertDialog.Builder(Diary.this, R.style.MyDialogTheme);
                dlg.setTitle("주의");
                dlg.setMessage("일기가 삭제됩니다.");
                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        edtDiary.setText("");
                        saveDiary(fileName);
                        deleteFile(fileName);
                    }
                });
                dlg.setNegativeButton("취소", null);
                dlg.show();
            }
        });

    }

    void showDate() {
        saveDiary(fileName);

        Calendar cal = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                loadDiary(year, month, dayOfMonth);
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));

        datePickerDialog.show();
    }

    // 일기 파일 읽기
    @SuppressLint("DefaultLocale")
    private void loadDiary(int year, int monthOfYear, int dayOfMonth) {

        btnDatePicker.setText(year + "년 " + (monthOfYear + 1) + "월 " + dayOfMonth + "일");

        fileName = year + "" + String.format("%02d", monthOfYear + 1) + "" + String.format("%02d", dayOfMonth) + ".txt";

        FileInputStream inFS;
        try {
            inFS = openFileInput(fileName);

            byte[] fileData = new byte[inFS.available()];
            inFS.read(fileData);
            inFS.close();

            String str = new String(fileData, "UTF-8");

            edtDiary.setText(str);
        } catch (Exception e) {     //일기 유무 검사
            edtDiary.setText("");
            e.printStackTrace();
        }

    }

    //일기 저장
    private void saveDiary(String readDay) {
        FileOutputStream outFS;
        try {
            outFS = openFileOutput(readDay, MODE_PRIVATE); //MODE_WORLD_WRITEABLE
            String str = edtDiary.getText().toString();

            if(str.equals("")) {
                outFS.close();
                deleteFile(readDay);
            }
            else {
                outFS.write(str.getBytes());
                outFS.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        saveDiary(fileName);
        super.onPause();
        super.appPauseTime = System.currentTimeMillis(); //액티비티가 정지되면 시간 기록
    }
}