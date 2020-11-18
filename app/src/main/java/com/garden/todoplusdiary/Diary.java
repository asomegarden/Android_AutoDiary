package com.garden.todoplusdiary;

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
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;

public class Diary extends BaseActivity {

    EditText edtDiary;
    Button btnSave, btnDatePicker, goHome, goTodo, btnDel;
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

        Calendar c = Calendar.getInstance();
        checkedDay(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));

        btnDatePicker.setText(c.get(Calendar.YEAR) +"년 "+ (c.get(Calendar.MONTH)+1) +"월 "+ c.get(Calendar.DATE) + "일");

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

        btnSave.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder ad = new AlertDialog.Builder(Diary.this, R.style.MyDialogTheme);

                ad.setTitle("확인");       // 제목 설정
                ad.setMessage("캡쳐 하시겠습니까?");   // 내용 설정

                // 확인 버튼 설정
                ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v(TAG,"Yes Btn Click");
                        dialog.dismiss();     //닫기
                        captureView(edtDiary);
                        Toast.makeText(getApplicationContext(), "캡쳐됨", Toast.LENGTH_SHORT).show();
                        // Event
                    }
                });

                // 취소 버튼 설정
                ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v(TAG,"No Btn Click");
                        dialog.dismiss();     //닫기
                        // Event
                    }
                });
                // 창 띄우기
                ad.show();
                return false;
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
    public void captureView(View View) {
        View.buildDrawingCache();
        Bitmap captureView = View.getDrawingCache();
        FileOutputStream fos;

        String strFolderPath = getExternalFilesDir(null).getAbsolutePath() + "/Capture"; //내장에 만든다

        String strFilePath = strFolderPath + "/" + System.currentTimeMillis() + ".png";
        File fileCacheItem = new File(strFilePath);

        try {
            fos = new FileOutputStream(fileCacheItem);
            captureView.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}