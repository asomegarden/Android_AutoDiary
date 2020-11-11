package com.garden.todoplusdiary;

import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class DiaryList extends AppCompatActivity {

    Button  goHome, goTodo, goDiary;
    ArrayList<String> items = new ArrayList<String>();
    String date;
    public static final String TAG = "Test_Alert_Dialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_list);

        //엑티비티 이동
        goHome = (Button) findViewById(R.id.goHome);
        goTodo = (Button) findViewById(R.id.goTodo);
        goDiary = (Button) findViewById(R.id.goDiary);

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
        goDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Diary.class);
                startActivity(intent);
                onStop();
            }
        });

        final String[] Diarytitle = getTitleList();
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, items);
        final ListView listview = (ListView) findViewById(R.id.todoView);
        listview.setAdapter(adapter);

        if(Diarytitle == null||Diarytitle.length == 0) items.add("일기를 추가해보세요");

        else
        {
            for(int i=0; i<Diarytitle.length; i++) {
                FileInputStream inFS = null;
                try {
                    inFS = openFileInput(Diarytitle[i]);

                    byte[] fileData = new byte[inFS.available()];
                    inFS.read(fileData);
                    inFS.close();

                    String str = new String(fileData, "UTF-8");

                    date = Diarytitle[i].substring(0,4) + Diarytitle[i].substring(4,6) + Diarytitle[i].substring(6,8);

                    items.add(date + "\n" + str);
                } catch (Exception e) {     //일기 유무 검사
                    e.printStackTrace();
                }
            }
        }
        adapter.notifyDataSetChanged();

        AdapterView.OnItemLongClickListener OLCL = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final String filename = items.get(position).substring(0, 4) + items.get(position).substring(4, 6) + items.get(position).substring(6, 8) + ".txt";
                if(items.get(position).equals("일기를 추가해보세요"))
                {
                    Toast.makeText(getApplicationContext(), "일기를 추가해보세요", Toast.LENGTH_SHORT).show();
                }
                else {
                    AlertDialog.Builder ad = new AlertDialog.Builder(DiaryList.this);

                    ad.setTitle("주의");       // 제목 설정
                    ad.setMessage(date + "의 일기가 삭제됩니다.");   // 내용 설정

                    // 확인 버튼 설정
                    ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.v(TAG, "Yes Btn Click");
                            dialog.dismiss();     //닫기
                            deleteFile(filename);

                            Toast.makeText(getApplicationContext(), date + "의 일기 삭제됨.", Toast.LENGTH_SHORT).show();
                            items.remove(position);
                            adapter.notifyDataSetChanged();
                            // Event
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
                return false;
            }
        };
        listview.setOnItemLongClickListener(OLCL);
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
        onStop();
    }
}