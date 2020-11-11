package com.example.simple_diary;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FilenameFilter;

public class Options extends AppCompatActivity {

    public static final String TAG = "Test_Alert_Dialog";
    TextView resetdiary, resettodo, help, helptext;
    myDBHelper myHelper;
    SQLiteDatabase sqlDB;
    Boolean toggle = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        resetdiary = (TextView) findViewById(R.id.resetdiary);
        resettodo = (TextView) findViewById(R.id.resettodo);
        help = (TextView) findViewById(R.id.help);
        helptext = (TextView) findViewById(R.id.helptext);
        myHelper = new myDBHelper(this);
        helptext.setMovementMethod(new ScrollingMovementMethod());

        resetdiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] title = getTitleList();
                AlertDialog.Builder ad = new AlertDialog.Builder(Options.this);

                ad.setTitle("주의");       // 제목 설정
                ad.setMessage("정말로 " + Integer.toString(title.length) + "개의 일기를 삭제하시겠습니까?");   // 내용 설정

                ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v(TAG, "Yes Btn Click");
                        dialog.dismiss();     //닫기
                        String filename="";

                        if(title.length!=0) {
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

                AlertDialog.Builder ad = new AlertDialog.Builder(Options.this);

                ad.setTitle("주의");       // 제목 설정
                ad.setMessage("정말로 모든 투두리스트를 삭제하시겠습니까?");   // 내용 설정

                ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.v(TAG, "Yes Btn Click");
                        dialog.dismiss();     //닫기
                        sqlDB = myHelper.getReadableDatabase();

                        sqlDB.execSQL("delete from groupTBL");

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
            File file = new File("/data/data/com.example.todoplusdiary/files");
            File[] files = file.listFiles(fileFilter);

            String [] titleList = new String [files.length]; //파일이 있는 만큼 어레이 생성
            for(int i = 0;i < files.length;i++)
            {
                titleList[i] = files[i].getName();

            }//end for
            return titleList;
        } catch( Exception e )
        {
            return null;
        }//end catch()
    }//end getTitleList

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
}