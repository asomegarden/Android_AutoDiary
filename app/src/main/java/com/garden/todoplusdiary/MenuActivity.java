package com.garden.todoplusdiary;

import android.content.Intent;
import android.graphics.Path;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class MenuActivity extends BaseActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        overridePendingTransition(R.anim.horizon_enter, R.anim.none);

        LinearLayout btnBack = (LinearLayout) findViewById(R.id.btnBack);
        TextView goDiary, goTodo, DiaryView, goOption;

        goDiary = (TextView) findViewById(R.id.goDiary);
        goTodo = (TextView) findViewById(R.id.goTodo);
        goOption = (TextView) findViewById(R.id.goOption);
        DiaryView = (TextView) findViewById(R.id.DiaryView);
        goOption = (TextView) findViewById(R.id.goOption);

        goDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, Diary.class);
                startActivity(intent);
            }
        });
        goTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, Todo.class);
                startActivity(intent);
            }
        });
        goOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, Options.class);
                startActivity(intent);
            }
        });
        DiaryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, DiaryList.class);
                startActivity(intent);
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
