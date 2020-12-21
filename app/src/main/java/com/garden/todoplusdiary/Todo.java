package com.garden.todoplusdiary;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class Todo extends BaseActivity {
    final int PERMISSIONS_REQUEST_CODE = 1;
    final String PREFNAME = "Preferences";
    final String PREFNAME0 = "Pref";

    Button addButton, makeDiary, btnDatePicker;
    EditText edtNewItem;
    String fileName = "";
    String date = "", dbId = "";
    LinearLayout btnMenu, btnAdd;
    int y, m, d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        myHelper = new myDBHelper(this);

        startService(new Intent(this, TerminatedApp.class));

        isFirstTime();
        Appexec();


        edtNewItem = (EditText) findViewById(R.id.edtNewItem);
        btnDatePicker = (Button) findViewById(R.id.btnDatePicker);
        makeDiary = (Button) findViewById(R.id.btnMakeDiary);

        btnMenu = (LinearLayout) findViewById(R.id.btnMenu);
        btnAdd = (LinearLayout) findViewById(R.id.btnAdd);

        // 첫 시작 시에는 오늘 날짜 일기 읽어주기
        Calendar c = Calendar.getInstance(); // 오늘 날짜를 받음

        final CustomLinearLayoutAdapter adapter = new CustomLinearLayoutAdapter();

        // listview 생성 및 adapter 지정.
        final ListView listview = (ListView) findViewById(R.id.todoView);
        listview.setAdapter(adapter);

        btnDatePicker.setText(c.get(Calendar.YEAR) + "년 " + (c.get(Calendar.MONTH) + 1) + "월 " + c.get(Calendar.DATE) + "일");
        y = c.get(Calendar.YEAR); m = (c.get(Calendar.MONTH) + 1); d = c.get(Calendar.DATE);
        date = c.get(Calendar.YEAR) + "" + (c.get(Calendar.MONTH) + 1) + "" + c.get(Calendar.DATE);
        loaditem(listview, adapter, c.get(Calendar.YEAR), (c.get(Calendar.MONTH)+1), c.get(Calendar.DATE));

        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            final Calendar c = Calendar.getInstance();
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Todo.this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        btnDatePicker.setText(year + "년 " + (month + 1) + "월 " + dayOfMonth + "일");
                        adapter.clear();
                        adapter.notifyDataSetChanged();
                        date = year + "" + (month + 1) + "" + dayOfMonth;
                        y = year; m = month + 1; d = dayOfMonth;
                        loaditem(listview, adapter, year, month+1, dayOfMonth);
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
                datePickerDialog.show();
            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Todo.this, MenuActivity.class);
                startActivity(intent);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtNewItem.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "내용을 입력하세요", Toast.LENGTH_SHORT).show();
                }
                else if(edtNewItem.getText().toString().equals("새로운 할 일")) {
                    adapter.addItem("새로운 할 일");
                    adapter.notifyDataSetChanged();
                    edtNewItem.setText("");
                }
                else{
                    adapter.addItem(edtNewItem.getText().toString());
                    adapter.notifyDataSetChanged();

                    dbId = date + edtNewItem.getText().toString();

                    sqlDB = myHelper.getWritableDatabase();
                    sqlDB.execSQL("INSERT OR REPLACE INTO groupTBL VALUES('"+dbId+"', '"+date+"','"+edtNewItem.getText().toString()+"', '" +false+ "');");

                    sqlDB.close();
                    edtNewItem.setText("");
                }
            }
        });

        makeDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeDiary(y, m, d);
            }
        });

        makeDiary.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder ad = new AlertDialog.Builder(Todo.this, R.style.MyDialogTheme);

                ad.setTitle("주의");       // 제목 설정
                ad.setMessage(date + "의 Todo-List가 초기화됩니다.");   // 내용 설정

                // 확인 버튼 설정
                ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();     //닫기
                        sqlDB = myHelper.getWritableDatabase();

                        String deleteQuery = "DELETE FROM groupTBL WHERE gDate='" + date + "'";
                        sqlDB.execSQL(deleteQuery);
                        Toast.makeText(getApplicationContext(), date + "의 ToDo-List 초기화됨.", Toast.LENGTH_SHORT).show();
                        adapter.clear();
                        adapter.notifyDataSetChanged();
                        // Event
                    }
                });

                // 취소 버튼 설정
                ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                        // Event
                    }
                });

                // 창 띄우기
                ad.show();
                return false;
            }
        });

        AdapterView.OnItemClickListener OCL = new AdapterView.OnItemClickListener() {
            boolean checked = true;
            String str = "";
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView text1 = (TextView) view.findViewById(R.id.textView1);
                str = adapter.getItem(position);

                if(!str.equals("새로운 할 일")) {
                    sqlDB = myHelper.getWritableDatabase();

                    checked = listview.isItemChecked(position);
                    dbId = date + str;
                    sqlDB.execSQL("INSERT OR REPLACE INTO groupTBL VALUES('" + dbId + "', '" + date + "','" + str + "', '" + checked + "');");
                    sqlDB.close();
                }
                if(listview.isItemChecked(position)){
                    text1.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                }
                else{
                    text1.setPaintFlags(0);
                }
            }
        };

        AdapterView.OnItemLongClickListener OCL_L = new AdapterView.OnItemLongClickListener() {
            boolean checked = true;
            String str = "";
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                View vDialog = View.inflate(Todo.this, R.layout.dialog_edit, null);

                AlertDialog.Builder dlg = new AlertDialog.Builder(Todo.this);

                final AlertDialog ad = dlg.create();

                ad.setView(vDialog);

                Button btnDel = (Button) vDialog.findViewById(R.id.btnDel);
                Button btnSave = (Button) vDialog.findViewById(R.id.btnSave);

                final EditText edtItem = (EditText) vDialog.findViewById(R.id.edtItem);

                ad.show();

                edtItem.setText(adapter.getItem(position));
                str = adapter.getItem(position);

                btnDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sqlDB = myHelper.getWritableDatabase();

                        if(str.equals("새로운 할 일")) str = "";
                        else
                        {
                            dbId = date + str;

                            String deleteQuery = "DELETE FROM groupTBL WHERE gdbId='" + dbId + "'";
                            sqlDB.execSQL(deleteQuery);
                        }
                        adapter.removeItem(position);
                        adapter.notifyDataSetChanged();
                        ad.dismiss();
                    }
                });

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(edtItem.getText().toString().equals("")){
                            Toast.makeText(getApplicationContext(), "내용을 입력하세요", Toast.LENGTH_SHORT).show();
                        }
                        else if(edtItem.getText().toString().equals("새로운 할 일")) {
                        }
                        else{
                            checked = listview.isItemChecked(position);

                            sqlDB = myHelper.getWritableDatabase();

                            dbId = date + adapter.getItem(position);
                            String deleteQuery = "DELETE FROM groupTBL WHERE gdbId='" + dbId + "'";
                            sqlDB.execSQL(deleteQuery);
                            dbId = date + edtItem.getText().toString();
                            sqlDB.execSQL("INSERT OR REPLACE INTO groupTBL VALUES('"+dbId+"', '"+date+"','"+edtItem.getText().toString()+"', '" +checked+ "');");

                            sqlDB.close();
                            adapter.setItem(position, edtItem.getText().toString());
                            adapter.notifyDataSetChanged();
                            ad.dismiss();
                        }
                    }
                });
                return false;
            }
        };
        listview.setOnItemLongClickListener(OCL_L);
        listview.setOnItemClickListener(OCL);
    }

    private void loaditem(final ListView listview, final CustomLinearLayoutAdapter adapter, int year, int monthOfYear, int dayOfMonth){
        int ID = 1;
        sqlDB = myHelper.getWritableDatabase();
        sqlDB.execSQL("INSERT OR REPLACE INTO DATE VALUES('"+ID+"', '"+date+"');");
        sqlDB.close();
        String sqlSelect = "SELECT * FROM groupTBL WHERE gDate=" + "'" + year + "" + monthOfYear + "" + dayOfMonth + "'";
        int index = 0;
        sqlDB = myHelper.getReadableDatabase();
        Cursor cursor;
        cursor = sqlDB.rawQuery(sqlSelect, null);
        while (cursor.moveToNext()) {
            adapter.addItem(cursor.getString(2));
            listview.setItemChecked(index, Boolean.parseBoolean(cursor.getString(3)));
            index++;
        }
        if(index == 0) adapter.addItem("새로운 할 일");
        sqlDB.close();
        cursor.close();
    }

 private void makeDiary(int year, int monthOfYear, int dayOfMonth) {

        btnDatePicker.setText(year + "년 " + monthOfYear + "월 " + dayOfMonth + "일");
        fileName = year + "" + String.format("%02d", monthOfYear) + "" + String.format("%02d", dayOfMonth) + ".txt";

        FileInputStream inFS;
        try {
            inFS = openFileInput(fileName);

            byte[] fileData = new byte[inFS.available()];
            inFS.read(fileData);
            inFS.close();

            AlertDialog.Builder ad = new AlertDialog.Builder(this, R.style.MyDialogTheme);

            ad.setTitle("이미 일기가 존재합니다.");       // 제목 설정
            ad.setMessage("삭제하고 새로 만드시겠습니까?");   // 내용 설정

            // 확인 버튼 설정
            ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                String str = "", str1 = "", str2 = "";
                int str1cnt = 0, str2cnt = 0;
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();     //닫기
                    FileOutputStream outFS;
                    try {
                        outFS = openFileOutput(fileName, Context.MODE_PRIVATE); //MODE_WORLD_WRITEABLE
                        String sqlSelect = "SELECT * FROM groupTBL WHERE gDate=" + "'" + date + "'";
                        sqlDB = myHelper.getReadableDatabase();
                        Cursor cursor;
                        cursor = sqlDB.rawQuery(sqlSelect, null);
                        while (cursor.moveToNext()) {
                            if(Boolean.parseBoolean(cursor.getString(3)))
                            {
                                str1 += cursor.getString(2) + "를 하고, ";
                                str1cnt++;
                            }
                            else
                            {
                                str2 += cursor.getString(2) + "와 ";
                                str2cnt++;
                            }
                        }
                        cursor.close();

                        if(str1cnt == 0 && str2cnt == 0){
                            Toast.makeText(getApplicationContext(), "먼저 투두리스트를 작성하세요", Toast.LENGTH_SHORT).show();
                            deleteFile(fileName);
                        }
                        else if(str1cnt == 0 && str2cnt != 0) {
                            str = "목표를 하나도 달성하지 못했다. " + str2.substring(0, str2.length()-2) + "를 하지 못 했다.";
                            outFS.write(str.getBytes());
                            Toast.makeText(getApplicationContext(), "생성됨", Toast.LENGTH_SHORT).show();
                        }
                        else if(str1cnt != 0 && str2cnt == 0) {
                            str = "목표를 모두 달성했다. " + str1.substring(0, str1.length()-5) + " 했다.";
                            outFS.write(str.getBytes());
                            Toast.makeText(getApplicationContext(), "생성됨", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            str = str1.substring(0, str1.length() - 5) + " 했다." + " 하지만 " + str2.substring(0, str2.length() - 2) + "는 하지 못 했다.";
                            outFS.write(str.getBytes());
                            Toast.makeText(getApplicationContext(), "생성됨", Toast.LENGTH_SHORT).show();
                        }
                        outFS.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "오류", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            // 취소 버튼 설정
            ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();     //닫기
                    // Event
                }
            });
            // 창 띄우기
            ad.show();
        } catch (Exception e) {     //일기가 없을 때
            e.printStackTrace();

            String str, str1 = "", str2 = "";
            int str1cnt = 0, str2cnt = 0;

            FileOutputStream outFS;
            try {
                outFS = openFileOutput(fileName, Context.MODE_PRIVATE); //MODE_WORLD_WRITEABLE
                String sqlSelect = "SELECT * FROM groupTBL WHERE gDate=" + "'" +date+ "'";
                sqlDB = myHelper.getReadableDatabase();
                Cursor cursor;
                cursor = sqlDB.rawQuery(sqlSelect, null);
                while (cursor.moveToNext()) {
                    if(Boolean.parseBoolean(cursor.getString(3)))
                    {
                        str1 += cursor.getString(2) + "를 하고, ";
                        str1cnt++;
                    }
                    else
                    {
                        str2 += cursor.getString(2) + "와 ";
                        str2cnt++;
                    }
                }
                cursor.close();

                if(str1cnt == 0 && str2cnt == 0){
                    Toast.makeText(getApplicationContext(), "먼저 투두리스트를 작성하세요", Toast.LENGTH_SHORT).show();
                    deleteFile(fileName);
                }
                else if(str1cnt == 0 && str2cnt != 0) {
                    str = "목표를 하나도 달성하지 못했다. " + str2.substring(0, str2.length()-2) + "를 하지 못 했다.";
                    outFS.write(str.getBytes());
                    Toast.makeText(getApplicationContext(), "생성됨", Toast.LENGTH_SHORT).show();
                }
                else if(str1cnt != 0 && str2cnt == 0) {
                    str = "목표를 모두 달성했다. " + str1.substring(0, str1.length()-5) + " 했다.";
                    outFS.write(str.getBytes());
                    Toast.makeText(getApplicationContext(), "생성됨", Toast.LENGTH_SHORT).show();
                }
                else {
                    str = str1.substring(0, str1.length() - 5) + " 했다." + " 하지만 " + str2.substring(0, str2.length() - 2) + "는 하지 못 했다.";
                    outFS.write(str.getBytes());
                    Toast.makeText(getApplicationContext(), "생성됨", Toast.LENGTH_SHORT).show();
                }
                outFS.close();

            } catch (Exception c) {
                c.printStackTrace();
                Toast.makeText(getApplicationContext(), "오류", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void Appexec() {
        SharedPreferences settings = getSharedPreferences(PREFNAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        if (settings.getBoolean("Appexec", true)) {
            editor.putBoolean("Appexec", false);
            editor.apply();
            sqlDB = myHelper.getReadableDatabase();

            Cursor cursor;
            cursor = sqlDB.rawQuery("SELECT * FROM pwTBL", null);

            cursor.moveToFirst();
            if(Boolean.parseBoolean(cursor.getString(2))) {
                sqlDB.close();
                cursor.close();
                Intent intent = new Intent(getApplicationContext(), AppLocker.class);
                startActivity(intent);
            }
        }
    }

    public void isFirstTime() {
        SharedPreferences settings = getSharedPreferences(PREFNAME0, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        if (settings.getBoolean("isFirstTime", true)) {
            editor.putBoolean("isFirstTime", false);
            editor.apply();

            int Id = 1;
            String Pw = "1" + "0000";

            Boolean Enable = false;

            sqlDB = myHelper.getWritableDatabase();
            sqlDB.execSQL("INSERT INTO pwTBL VALUES('"+Id+"', '"+Pw+"','"+Enable+"');");
            sqlDB.close();
        }
    }
}

