package com.garden.todoplusdiary;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class Todo extends AppCompatActivity {
    Button goHome, goDiary, addButton, deleteButton, makeDiary;
    Button btnDatePicker, btnAdd, itemsave, itemdelete;
    EditText edtDiary, edititem;
    String fileName;
    String date, dbId;
    LinearLayout listwindow, editwindow;
    myDBHelper myHelper;
    SQLiteDatabase sqlDB;
    int cnt = 0, y, m, d;
    final ArrayList<String> items = new ArrayList<String>();
    public static final String TAG = "Test_Alert_Dialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        goHome = (Button) findViewById(R.id.goHome);
        goDiary = (Button) findViewById(R.id.goDiary);
        edititem = (EditText) findViewById(R.id.edititem);
        itemsave = (Button) findViewById(R.id.itemsave);
        itemdelete = (Button) findViewById(R.id.itemdelete);
        listwindow = (LinearLayout) findViewById(R.id.listwindow);
        editwindow = (LinearLayout) findViewById(R.id.editwindow);
        edtDiary = (EditText) findViewById(R.id.edtDiary);
        btnDatePicker = (Button) findViewById(R.id.btnDatePicker);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        makeDiary = (Button) findViewById(R.id.btnMakeDiary);
        addButton = (Button) findViewById(R.id.btnAdd);
        deleteButton = (Button) findViewById(R.id.btnDel);

        myHelper = new myDBHelper(this);

        goHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                onStop();
            }
        });
        goDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Diary.class);
                startActivity(intent);
                onStop();
            }
        });

        // 첫 시작 시에는 오늘 날짜 일기 읽어주기
        Calendar c = Calendar.getInstance(); // 오늘 날짜를 받음

        final ArrayAdapter adapter = new ArrayAdapter(this, R.layout.listview_item, items);


        // listview 생성 및 adapter 지정.
        final ListView listview = (ListView) findViewById(R.id.todoView);
        listview.setAdapter(adapter);

        btnDatePicker.setText(c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DATE));
        y = c.get(Calendar.YEAR); m = c.get(Calendar.MONTH); d = c.get(Calendar.DATE);
        date = c.get(Calendar.YEAR) + "" + c.get(Calendar.MONTH) + "" + c.get(Calendar.DATE);
        loaditem(listview, adapter, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));

        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            Calendar c = Calendar.getInstance();
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Todo.this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        btnDatePicker.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                        adapter.clear();
                        adapter.notifyDataSetChanged();
                        cnt = 0;
                        date = year + "" + month + "" + dayOfMonth;
                        y = year; m = month; d = dayOfMonth;
                        loaditem(listview, adapter, year, month, dayOfMonth);
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
                datePickerDialog.show();
            }
        });

        makeDiary.setOnClickListener(new View.OnClickListener() {
            String str, str1 = "", str2 = "";
            int str1cnt = 0, str2cnt = 0;
            @Override
            public void onClick(View v) {
                if(checkedDay(y, m, d))
                {
                    FileOutputStream outFS = null;
                    try {
                        outFS = openFileOutput(fileName, Context.MODE_PRIVATE); //MODE_WORLD_WRITEABLE
                        String sqlSelect = "SELECT * FROM groupTBL WHERE gDate=" + "'" + date + "'";
                        sqlDB = myHelper.getReadableDatabase();
                        Cursor cursor;
                        cursor = sqlDB.rawQuery(sqlSelect, null);
                        while (cursor.moveToNext()) {
                            if(Boolean.parseBoolean(cursor.getString(3).toString()))
                            {
                                str1 += cursor.getString(2).toString() + "를 하고, ";
                                str1cnt++;
                            }
                            else
                            {
                                str2 += cursor.getString(2).toString() + "와 ";
                                str2cnt++;
                            }
                        }

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
            }
        });

        addButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {

                // 아이템 추가.
                items.add("새로운 할 일");

                // listview 갱신
                adapter.notifyDataSetChanged();
            }
        });

        addButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder ad = new AlertDialog.Builder(Todo.this);

                ad.setTitle("주의");       // 제목 설정
                ad.setMessage(date + "의 Todo-List가 초기화됩니다.");   // 내용 설정

                // 확인 버튼 설정
                ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v(TAG,"Yes Btn Click");
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

        AdapterView.OnItemClickListener OCL = new AdapterView.OnItemClickListener() {
            boolean checked = true;
            String str;
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                str = items.get(position).toString();
                sqlDB = myHelper.getWritableDatabase();
                if(str.equals("새로운 할 일"))
                {
                    Toast.makeText(getApplicationContext(), "길게 눌러 수정하세요", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    checked = listview.isItemChecked(position);
                    dbId = date + str;
                    sqlDB.execSQL("INSERT OR REPLACE INTO groupTBL VALUES('"+dbId+"', '"+date+"','"+str+"', '" +checked+ "');");
                    sqlDB.close();
                }
            }
        };

        AdapterView.OnItemLongClickListener OCL_L = new AdapterView.OnItemLongClickListener() {
            boolean checked = true;
            String str;
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                edititem.setText(items.get(position));
                listwindow.setVisibility(listwindow.GONE);
                editwindow.setVisibility(editwindow.VISIBLE);
                str = items.get(position).toString();

                itemsave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listwindow.setVisibility(listwindow.VISIBLE);
                        editwindow.setVisibility(editwindow.GONE);

                        if(listview.isItemChecked(position)) checked = true;
                        else checked = false;

                        sqlDB = myHelper.getWritableDatabase();
                        if(str.equals("새로운 할 일") && !(edititem.getText().toString().equals("새로운 할 일")))
                        {
                            dbId = date + edititem.getText().toString();
                            sqlDB.execSQL("INSERT INTO groupTBL VALUES('"+dbId+"', '"+date+"','"+edititem.getText().toString()+"', '" +checked+ "');");
                        }
                        else if(str.equals("새로운 할 일") && edititem.getText().toString().equals("새로운 할 일")) Toast.makeText(getApplicationContext(), "내용을 작성하세요", Toast.LENGTH_SHORT).show();
                        else
                        {
                            dbId = date + items.get(position).toString();
                            String deleteQuery = "DELETE FROM groupTBL WHERE gdbId='" + dbId + "'";
                            sqlDB.execSQL(deleteQuery);
                            dbId = date + edititem.getText().toString();
                            sqlDB.execSQL("INSERT OR REPLACE INTO groupTBL VALUES('"+dbId+"', '"+date+"','"+edititem.getText().toString()+"', '" +checked+ "');");
                        }
                        sqlDB.close();
                        items.set(position, edititem.getText().toString());
                        adapter.notifyDataSetChanged();
                    }
                });

                itemdelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listwindow.setVisibility(listwindow.VISIBLE);
                        editwindow.setVisibility(editwindow.GONE);

                        sqlDB = myHelper.getWritableDatabase();

                        if(str.equals("새로운 할 일")) str = "";
                        else
                        {
                            dbId = date + str;

                            String deleteQuery = "DELETE FROM groupTBL WHERE gdbId='" + dbId + "'";
                            sqlDB.execSQL(deleteQuery);
                        }
                        items.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
                return false;
            }
        };
        listview.setOnItemLongClickListener(OCL_L);
        listview.setOnItemClickListener(OCL);
    }

    private void loaditem(final ListView listview, final ArrayAdapter adapter, int year, int monthOfYear, int dayOfMonth){
        String sqlSelect = "SELECT * FROM groupTBL WHERE gDate=" + "'" + year + "" + monthOfYear + "" + dayOfMonth + "'";
        int index = 0;
        sqlDB = myHelper.getReadableDatabase();
        Cursor cursor;
        cursor = sqlDB.rawQuery(sqlSelect, null);
        while (cursor.moveToNext()) {
            items.add(cursor.getString(2).toString());
            listview.setItemChecked(index, Boolean.parseBoolean(cursor.getString(3).toString()));
            index++;
        }
        adapter.notifyDataSetChanged();

    }



    private Boolean checkedDay(int year, int monthOfYear, int dayOfMonth) {

        btnDatePicker.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

        fileName = year + "" + String.format("%02d", monthOfYear + 1) + "" + String.format("%02d", dayOfMonth) + ".txt";

        FileInputStream inFS = null;
        try {
            inFS = openFileInput(fileName);

            byte[] fileData = new byte[inFS.available()];
            inFS.read(fileData);
            inFS.close();

            Toast.makeText(getApplicationContext(), "오늘의 일기가 이미 존재합니다", Toast.LENGTH_SHORT).show();
            return false;
        } catch (Exception e) {     //일기 유무 검사
            e.printStackTrace();
            return true;
        }

    }

    public class myDBHelper extends SQLiteOpenHelper {
        public myDBHelper(Context context) {
            super(context, "todoDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE groupTBL (gdbId String PRIMARY KEY, gDate String , gContent String, gCheck BOOLEAN);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS groupTBL");
            onCreate(db);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
        onStop();
    }
};


