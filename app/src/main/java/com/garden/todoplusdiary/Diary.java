package com.garden.todoplusdiary;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;

public class Diary extends BaseActivity {

    EditText edtDiary;
    Button btnSave, btnDatePicker, btnDel;
    public static final String TAG = "Test_Alert_Dialog";
    String fileName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        btnDel = (Button) findViewById(R.id.btnDel);
        edtDiary = (EditText) findViewById(R.id.edtDiary);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDatePicker = (Button) findViewById(R.id.btnDatePicker);
        LinearLayout btnMenu = (LinearLayout) findViewById(R.id.btnMenu);

        Calendar c = Calendar.getInstance();
        checkedDay(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));

        btnDatePicker.setText(c.get(Calendar.YEAR) +"년 "+ (c.get(Calendar.MONTH)+1) +"월 "+ c.get(Calendar.DATE) + "일");

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
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDiary(fileName);
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
                        btnSave.setText("저장");
                        deleteFile(fileName);
                    }
                });
                dlg.setNegativeButton("취소", null);
                dlg.show();
            }
        });

    }

    void showDate() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                checkedDay(year, month, dayOfMonth);
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));

        datePickerDialog.show();
    }

    // 일기 파일 읽기
    @SuppressLint("DefaultLocale")
    private void checkedDay(int year, int monthOfYear, int dayOfMonth) {

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
            btnSave.setText("수정");
        } catch (Exception e) {     //일기 유무 검사
            edtDiary.setText("");
            btnSave.setText("저장");
            e.printStackTrace();
        }

    }

    //일기 저장
    private void saveDiary(String readDay) {
        FileOutputStream outFS;
        try {
            outFS = openFileOutput(readDay, Context.MODE_PRIVATE); //MODE_WORLD_WRITEABLE
            String str = edtDiary.getText().toString();

            if(str.equals("")) {
                outFS.close();
                deleteFile(readDay);
                Toast.makeText(getApplicationContext(), "내용을 입력하세요", Toast.LENGTH_SHORT).show();
            }
            else {
                outFS.write(str.getBytes());
                Toast.makeText(getApplicationContext(), "저장됨", Toast.LENGTH_SHORT).show();
                btnSave.setText("수정");
                outFS.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "오류", Toast.LENGTH_SHORT).show();
        }
    }
}