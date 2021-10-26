package com.swufestu.vocabulary;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView wordText;
    TextView meaningText;
    TextView ephoneticText;
    TextView aphoneticText;
    ListView sentence_listText;
    EditText inputText;
    ImageButton backButton;
    ImageButton clearButton;
    ImageButton searchButton;
    ImageButton addwordlistButton;
    ImageButton ephonetic_btnButton;
    ImageButton aphonetic_btnButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //获取控件
    public void initial(){

        //各个显示
        wordText = (TextView) findViewById(R.id.word);  //单词
        ephoneticText=(TextView)findViewById(R.id.ephonetic); //英音标
        aphoneticText=(TextView)findViewById(R.id.aphonetic); //美音标
        meaningText=(TextView)findViewById(R.id.meaning); //释义
        sentence_listText=(ListView)findViewById(R.id.sentence_list);  //例句

        //各个按钮
        backButton=(ImageButton)findViewById(R.id.back);
        searchButton=(ImageButton)findViewById(R.id.search);
        clearButton=(ImageButton)findViewById(R.id.clear);
        addwordlistButton=(ImageButton)findViewById(R.id.addwordlist);
        ephonetic_btnButton=(ImageButton)findViewById(R.id.ephonetic_btn);
        aphonetic_btnButton=(ImageButton)findViewById(R.id.aphonetic_btn);

        //输入
        inputText=(EditText)findViewById(R.id.input);


    }
}