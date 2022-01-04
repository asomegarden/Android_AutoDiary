package com.garden.todoplusdiary;

import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class DiaryList extends BaseActivity {
    final ArrayList<String> items = new ArrayList<String>();
    String date = "";
    public static final String TAG = "Test_Alert_Dialog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_list);

        LinearLayout btnMenu = (LinearLayout) findViewById(R.id.btnMenu);

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiaryList.this, MenuActivity.class);
                startActivity(intent);
            }
        });

        final String[] diaryTitle = getTitleList();
        final ArrayAdapter adapter = new ArrayAdapter(this, R.layout.listview_item_simple, items);
        final ListView listview = (ListView) findViewById(R.id.DiaryList);
        listview.setAdapter(adapter);

        if(diaryTitle == null||diaryTitle.length == 0) items.add("일기를 추가해보세요");
        else {
            for(int i=0; i<diaryTitle.length; i++) {
                FileInputStream inFS;
                try {
                    inFS = openFileInput(diaryTitle[i]);

                    byte[] fileData = new byte[inFS.available()];
                    inFS.read(fileData);
                    inFS.close();

                    String str = new String(fileData, "UTF-8");

                    date = diaryTitle[i].substring(0,4) + diaryTitle[i].substring(4,6) + diaryTitle[i].substring(6,8);

                    items.add(date + "\n" + str);
                } catch (Exception e) {     //일기 유무 검사
                    e.printStackTrace();
                }
            }
        }
        adapter.notifyDataSetChanged();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                if(items.get(pos).equals("일기를 추가해보세요")){
                    Intent intent = new Intent(DiaryList.this, Diary.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(DiaryList.this, Diary.class);
                    intent.putExtra("date", date);
                    startActivity(intent);
                }
            }
        });

        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final String filename = items.get(position).substring(0, 4) + items.get(position).substring(4, 6) + items.get(position).substring(6, 8) + ".txt";
                if(items.get(position).equals("일기를 추가해보세요"))
                {
                    Toast.makeText(getApplicationContext(), "일기를 추가해보세요", Toast.LENGTH_SHORT).show();
                }
                else {
                    AlertDialog.Builder ad = new AlertDialog.Builder(DiaryList.this, R.style.MyDialogTheme);

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
                return true;
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

}