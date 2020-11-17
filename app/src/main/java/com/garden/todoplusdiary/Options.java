package com.garden.todoplusdiary;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.io.File;
import java.io.FilenameFilter;

public class Options extends BaseActivity {

    public static final String TAG = "Test_Alert_Dialog";
    TextView resetdiary, resettodo, help, helptext, ApplockToggle, setPw, ver;
    Boolean Enable = false, toggle = true;
    String pw = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        resetdiary = (TextView) findViewById(R.id.resetdiary);
        resettodo = (TextView) findViewById(R.id.resettodo);
        help = (TextView) findViewById(R.id.help);
        helptext = (TextView) findViewById(R.id.helptext);
        ApplockToggle = (TextView) findViewById(R.id.ApplockToggle);
        setPw = (TextView) findViewById(R.id.setPw);
        ver = (TextView) findViewById(R.id.ver);
        helptext.setMovementMethod(new ScrollingMovementMethod());

        myHelper = new myDBHelper(this);

        getPw();

        resetdiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] title = getTitleList();
                AlertDialog.Builder ad = new AlertDialog.Builder(Options.this, R.style.MyDialogTheme);

                ad.setTitle("주의");       // 제목 설정
                if(title == null) ad.setMessage("일기가 없습니다.");// 내용 설정
                else ad.setMessage("정말로 " + title.length + "개의 일기를 삭제하시겠습니까?");// 내용 설정

                ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v(TAG, "Yes Btn Click");
                        dialog.dismiss();     //닫기
                        String filename;

                        if(title!=null)
                        {
                            for (int i = 0; i < title.length; i++) {
                                filename = title[i];
                                deleteFile(filename);
                            }
                        }
                        Toast.makeText(getApplicationContext(), "일기장 초기화됨", Toast.LENGTH_SHORT).show();
                    }
                });

                // 취소 버튼 설정
                ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v(TAG, "No Btn Click");
                        dialog.dismiss();     //닫기
                        // Event
                    }
                });
                // 창 띄우기
                ad.show();
            }

        });

        resettodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder ad = new AlertDialog.Builder(Options.this, R.style.MyDialogTheme);

                ad.setTitle("주의");       // 제목 설정
                ad.setMessage("정말로 모든 투두리스트를 삭제하시겠습니까?");   // 내용 설정

                ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v(TAG, "Yes Btn Click");
                        dialog.dismiss();     //닫기
                        sqlDB = myHelper.getReadableDatabase();

                        sqlDB.execSQL("delete from groupTBL");
                        sqlDB.close();

                        Toast.makeText(getApplicationContext(), "투두리스트 초기화됨", Toast.LENGTH_SHORT).show();
                    }
                });

                // 취소 버튼 설정
                ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v(TAG, "No Btn Click");
                        dialog.dismiss();     //닫기
                        // Event
                    }
                });
                // 창 띄우기
                ad.show();
            }
        });

        ApplockToggle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Enable)
                {
                    ApplockToggle.setText("앱 잠금 활성화");
                    setPw.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "비밀번호 초기화됨", Toast.LENGTH_SHORT).show();
                    Enable = false;
                    pw = "10000";
                }
                else
                {
                    ApplockToggle.setText("앱 잠금 비활성화");
                    setPw.setVisibility(View.VISIBLE);
                    Enable = true;
                }
                SavePw();
            }
        });

        setPw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PwSetting.class);
                startActivity(intent);
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toggle) {
                    helptext.setVisibility(View.VISIBLE);
                    toggle = false;
                }
                else
                {
                    helptext.setVisibility(View.GONE);
                    toggle = true;
                }
            }
        });

        ver.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getApplicationContext(), "v1.3.2.1", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

    }
    private String[] getTitleList()
    {
        try
        {
            FilenameFilter fileFilter = new FilenameFilter()
            {
                public boolean accept(File dir, String name)
                {
                    return name.endsWith("txt");
                } //end accept
            };
            File file = new File("/data/data/com.garden.todoplusdiary/files");
            File[] files = file.listFiles(fileFilter);

            if(files == null) return null;
            else {
                String[] titleList = new String[files.length]; //파일이 있는 만큼 어레이 생성
                for (int i = 0; i < files.length; i++) {
                    titleList[i] = files[i].getName();

                }//end for
                return titleList;
            }
        } catch( Exception e )
        {
            return null;
        }//end catch()
    }//end getTitleList
    private void getPw()
    {
        sqlDB = myHelper.getReadableDatabase();

        Cursor cursor;
        cursor = sqlDB.rawQuery("SELECT * FROM pwTBL", null);

        cursor.moveToFirst();

        pw = cursor.getString(1);
        Enable = Boolean.parseBoolean(cursor.getString(2));
        sqlDB.close();
        cursor.close();

        if (Enable)
        {
            ApplockToggle.setText("앱 잠금 비활성화");
            setPw.setVisibility(View.VISIBLE);
        }
        else
        {
            ApplockToggle.setText("앱 잠금 활성화");
            setPw.setVisibility(View.GONE);
        }
    }

    private void SavePw()
    {
        sqlDB = myHelper.getWritableDatabase();

        int Id = 1;
        sqlDB.execSQL("INSERT OR REPLACE INTO pwTBL VALUES('"+Id+"', '"+pw+"','"+Enable+"');");
        sqlDB.close();
    }


}