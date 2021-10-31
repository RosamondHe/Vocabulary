package com.swufestu.vocabulary;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WordListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wordlist);

        // 初始化单词本
        LinearLayout linearLayout = findViewById(R.id.wordList);
        DictDBHelper dbHelper = new DictDBHelper(this, "Notebook");
        //Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from Notebook;", null);
        Cursor cursor = dbHelper.getReadableDatabase().query("Notebook",null,null,null,null,null,null);
        Intent data = new Intent();
        if (cursor.moveToFirst()) {
            linearLayout.removeAllViews();
            int i = 1;
            do {
                LinearLayout line = new LinearLayout(this);
                line.setOrientation(LinearLayout.HORIZONTAL);
                TextView text = new TextView(this);
                int index = cursor.getColumnIndex("word");
                if (index < 0) continue;
                String vocabulary = cursor.getString(index);
                text.setText(i + ". " + vocabulary);
                i++;
                text.setTextSize(20);
                text.setTextColor(Color.BLACK);
                text.setPadding(0,25,0,10);
                text.setMinWidth(820);
                text.setOnClickListener(v -> {
                    // 点击时，在主界面查询单词
                    data.putExtra("word", text.getText().toString().replaceAll("\\d+","").replaceAll(". ",""));
                    setResult(1, data);
                    finish();
                });
                line.addView(text);

                ImageButton delete = new ImageButton(this);
                delete.setImageResource(R.drawable.ic_baseline_delete_forever_24);
                delete.setBackgroundColor(Color.TRANSPARENT);
                delete.setOnClickListener(v -> {
                    // 点击时,从数据库中删除该单词
                    data.putExtra("delete", text.getText().toString().replaceAll("\\d+","").replaceAll(". ",""));
                    setResult(2, data);
                    finish();
                    Toast.makeText(WordListActivity.this, "已经从单词本删除", Toast.LENGTH_SHORT).show();
                });
                line.addView(delete);
                linearLayout.addView(line);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
}