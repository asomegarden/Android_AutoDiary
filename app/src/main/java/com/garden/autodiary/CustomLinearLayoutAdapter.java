package com.garden.autodiary;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomLinearLayoutAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private final ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>();

    // ListViewAdapter의 생성자
    public CustomLinearLayoutAdapter() {
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_custom, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView textTextView = (TextView) convertView.findViewById(R.id.textView1);
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox1);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ListViewItem listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        textTextView.setText(listViewItem.getText());

        myDBHelper myHelper;
        SQLiteDatabase sqlDB;

        myHelper = new myDBHelper(context);
        sqlDB = myHelper.getReadableDatabase();

        String sqlSelect = "SELECT * FROM DATE";

        Cursor cursor;

        cursor = sqlDB.rawQuery(sqlSelect, null);
        cursor.moveToNext();

        String date = cursor.getString(1);

        cursor.close();

        sqlSelect = "SELECT * FROM groupTBL WHERE gdbId =" + "'" + date + "" + listViewItem.getText() + "'";
        cursor = sqlDB.rawQuery(sqlSelect, null);
        while (cursor.moveToNext()) {
            if(Boolean.parseBoolean(cursor.getString(3)))
            {
                textTextView.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            }
            else
            {
                textTextView.setPaintFlags(0);
            }
        }
        cursor.close();

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public String getItem(int position) {
        return listViewItemList.get(position).getText() ;
    }

    public void  setItem(int position, String text){
        ListViewItem item = new ListViewItem();

        item.setText(text);
        listViewItemList.set(position, item);
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String text) {
        ListViewItem item = new ListViewItem();

        item.setText(text);
        listViewItemList.add(item);
    }

    public void removeItem(int position){
        listViewItemList.remove(position);
    }

    public void clear(){
        listViewItemList.clear();
    }
    public static class myDBHelper extends SQLiteOpenHelper { //투두 테이블, 패스워드 테이블 생성
        public myDBHelper(Context context) {
            super(context, "todoDB", null, 1);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE groupTBL (gdbId String PRIMARY KEY, gDate String , gContent String, gCheck BOOLEAN);");
            db.execSQL("CREATE TABLE DATE (ID Integer PRIMARY KEY, Date String);");
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS groupTBL");
            db.execSQL("DROP TABLE IF EXISTS DATE");
        }
    }
}
