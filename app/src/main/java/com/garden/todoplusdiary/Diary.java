package com.garden.todoplusdiary;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Calendar;

public class Diary extends BaseActivity {

    EditText edtDiary;
    Button btnSave, btnDatePicker, goHome, goTodo, btnDel;

    String fileName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        btnDel = (Button) findViewById(R.id.btnDel);
        edtDiary = (EditText) findViewById(R.id.edtDiary);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnDatePicker = (Button) findViewById(R.id.btnDatePicker);

        Calendar c = Calendar.getInstance();
        checkedDay(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));

        btnDatePicker.setText(c.get(Calendar.YEAR) +"-"+ (c.get(Calendar.MONTH)+1) +"-"+ c.get(Calendar.DATE));

        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDate();
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
                edtDiary.setText("");
                saveDiary(fileName);
                btnSave.setText("저장");
                deleteFile(fileName);
            }
        });


        //엑티비티 이동
        goHome = (Button) findViewById(R.id.goHome);
        goTodo = (Button) findViewById(R.id.goTodo);

        goHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
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
    private void checkedDay(int year, int monthOfYear, int dayOfMonth) {

        btnDatePicker.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

        fileName = year + "" + String.format("%02d", monthOfYear + 1) + "" + String.format("%02d", dayOfMonth) + ".txt";

        FileInputStream inFS = null;
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
        FileOutputStream outFS = null;
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