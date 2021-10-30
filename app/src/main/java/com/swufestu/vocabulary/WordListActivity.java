package com.swufestu.vocabulary;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WordListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notebook);

        // 初始化单词本
        LinearLayout linearLayout = findViewById(R.id.wordList);
        DictDBHelper dbHelper = new DictDBHelper(this, "Notebook");
        Cursor cursor = dbHelper.getReadableDatabase().rawQuery("select * from Notebook;", null);
        if (cursor.moveToFirst()) {
            linearLayout.removeAllViews();
            do {
                TextView text = new TextView(this);
                int index = cursor.getColumnIndex("word");
                if (index < 0) continue;
                text.setText(cursor.getString(index));
                text.setTextSize(30);
                text.setTextColor(Color.BLACK);
                text.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                text.setOnClickListener(v -> {
                    // 点击时，在主界面查询单词
                    Intent data = new Intent();
                    data.putExtra("word", text.getText().toString());
                    setResult(RESULT_OK, data);
                    finish();
                });
                linearLayout.addView(text);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
}
