package com.swufestu.vocabulary;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

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
    ImageButton addWordlistButton;
    ImageButton openWordListButton;
    ImageButton ephonetic_btnButton;
    ImageButton aphonetic_btnButton;

    public Dictionary dictionary = null;
    public WordMessage wordmessage = null;
    public Handler dictHandler = null;
    public DictDBHelper notebookHelper = null;
    public MediaPlayer mediaPlayer=null;
    public static String wordToSearch = null;
    public final static int ENGLISH_ACCENT=0;
    public final static int USA_ACCENT=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initial();
        new ThreadSearchWord().start();
    }


    //获取控件
    public void initial() {
        //各个显示
        wordText = findViewById(R.id.word);  //单词
        ephoneticText = findViewById(R.id.ephonetic); //英音标
        aphoneticText = findViewById(R.id.aphonetic); //美音标
        meaningText = findViewById(R.id.meaning); //释义
        sentence_listText = findViewById(R.id.sentence_list);  //例句

        //各个按钮
        backButton = findViewById(R.id.back);
        searchButton = findViewById(R.id.search);
        clearButton = findViewById(R.id.clear);
        addWordlistButton = findViewById(R.id.addwordlist);
        openWordListButton = findViewById(R.id.openWordList);
        ephonetic_btnButton = findViewById(R.id.ephonetic_btn);
        aphonetic_btnButton = findViewById(R.id.aphonetic_btn);

        //输入
        inputText = findViewById(R.id.input);
        inputText.setOnEditorActionListener(new EditTextActionLis()); //监听

        //工具
        dictionary = new Dictionary(MainActivity.this, "dict");
        notebookHelper = new DictDBHelper(MainActivity.this, "Notebook");
        dictHandler = new Handler(Looper.getMainLooper()); //需要刷新UI，就需要在主线程下跑用到主线程的looper

        //对wordToSearch进行初始化
        Intent intent = this.getIntent();
        wordToSearch = intent.getStringExtra("word");
        if (wordToSearch == null)
            wordToSearch = "";
        //显示单词
        wordText.setText(wordToSearch);
    }

    //子线程中访问网络
    public void searchWord(String word) {
        //调用该方法后首先初始化界面
        dictHandler.post(new RunnableInterface(wordToSearch, "", "", "", null, null));
        wordmessage = null;
        if (!dictionary.isWordExist(word)) {  //数据库中没有单词记录，从网络上进行同步
            if ((wordmessage = dictionary.getWordFromInternet(word)) == null || wordmessage.getWord().equals("")) {
                return;  //错词不添加进词典
            }
            dictionary.insertWordToDictionary(wordmessage, true);  //添加到词典中
        }
        //数据库中存在单词记录
        wordmessage = dictionary.getWordFromDictionary(word);
        if (wordmessage == null) {  //若词典中还是没有，用空字符串代替
            wordmessage = new WordMessage();
        }
        //单词各个信息
        String nword = wordmessage.getWord();
        String npse = wordmessage.getPsE();
        String npsa = wordmessage.getPsA();
        String nmeaning = wordmessage.getMeaning();
        ArrayList<String> esentList = wordmessage.getOrigList();
        ArrayList<String> csentList = wordmessage.getTransList();
        dictHandler.post(new RunnableInterface(nword, npse, npsa, nmeaning, esentList, csentList));
    }

    //开始搜索
    public void startSearch() {
        String str = inputText.getText().toString(); //查询输入
        if (str == null || str.equals("")) //输入为空
            return;
        wordToSearch = str;
        new ThreadSearchWord().start();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); //用于控制显示或隐藏输入法面板的类
        imm.hideSoftInputFromWindow(inputText.getWindowToken(), 0);
    }

    //返回按钮
    public void backClick(View v) {
        MainActivity.this.finish();
    }

    //删除输入
    public void clearClick(View v) {
        inputText.setText("");
    }

    //搜索
    public void searchClick(View v) {
        startSearch();
    }

    //输入框监听
    class EditTextActionLis implements TextView.OnEditorActionListener {
        @Override
        public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
            if (arg1 == EditorInfo.IME_ACTION_SEARCH || arg2 != null && arg2.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                startSearch();
                return true;
            }
            return false;
        }
    }

    //播放英音音频
    public void eplayClick(View v) {
        playMusic(wordToSearch, ENGLISH_ACCENT);
    }

    //播放美音音频
    public void aplayClick(View v) {
        playMusic(wordToSearch, USA_ACCENT);
    }

    public void playMusic(String word , int accent){
        if(word==null || word.length()<=0) //没有单词
            return;
        String pronUrl;
        WordMessage w;
        if(accent==ENGLISH_ACCENT){
            pronUrl=dictionary.getProneUrl(word);
        }else{
            pronUrl=dictionary.getPronaUrl(word);
        }
        if(pronUrl==null ||pronUrl=="null"||pronUrl.length()<=0) //说明网络上也没有对应发音，退出
            return;
        mediaPlayer = MediaPlayer.create(this, Uri.parse(pronUrl));
        mediaPlayer.start();
    }

    //添加单词到单词本
    public void addClick(View v) {
        String word = (String) wordText.getText();
        if (word.isEmpty())
            return;
        Cursor cursor = notebookHelper.getReadableDatabase().rawQuery("Select * from Notebook where word = ?;", new String[]{word});
        if (cursor.moveToNext()) {
            Toast.makeText(MainActivity.this, "该单词已经在单词本中", Toast.LENGTH_SHORT).show();
            cursor.close();
            return;
        }
        notebookHelper.getWritableDatabase().execSQL("insert into Notebook values (?);", new String[]{word});
        Toast.makeText(MainActivity.this, "成功添加至单词本", Toast.LENGTH_SHORT).show();
    }

    //打开单词本
    public void openClick(View v) {
        Intent intent = new Intent(MainActivity.this, WordListActivity.class);
        startActivityForResult(intent, 0);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 单词本中点击单词后，查询这个单词
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == 1) {
                String word = data.getStringExtra("word");
                inputText.setText(word);
                searchButton.callOnClick(); //不用用户手动点击，直接触发View的点击事件
            }

            if (resultCode == 2) {
                String deleteword = data.getStringExtra("delete");
                inputText.setText(deleteword);
                notebookHelper.getWritableDatabase().execSQL("delete from Notebook where word = ?;", new String[]{deleteword});
                openWordListButton.callOnClick(); //不用用户手动点击，直接触发View的点击事件
            }
        }
    }

    //子线程中网络查词
    public class ThreadSearchWord extends Thread {
        @Override
        public void run() {
            super.run();
            searchWord(wordToSearch);
        }

    }

    //线程接口
    public class RunnableInterface implements Runnable {
        String nword;
        String npse;
        String npsa;
        String nmeaning;
        ArrayList<String> esentList;
        ArrayList<String> csentList;

        public RunnableInterface(String nword, String npse, String npsa, String nmeaning,
                                 ArrayList<String> esentList, ArrayList<String> csentList) {
            super();
            this.nword = nword;
            this.npse = "英[" + npse + "]";
            this.npsa = "美[" + npsa + "]";
            this.nmeaning = nmeaning;
            this.esentList = esentList;
            this.csentList = csentList;
        }

        @Override
        public void run() {
            wordText.setText(nword);
            ephoneticText.setText(npse);
            aphoneticText.setText(npsa);
            meaningText.setText(nmeaning);
            if (esentList == null || csentList == null) {     //对链表为空进行防护
                return;
            }
            int count;
            if (esentList.size() <= csentList.size()) {
                count = esentList.size();
            } else {
                count = csentList.size();  //取两者长度最小值，但一般二者长度相等
            }
            ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
            for (int i = 0; i < count; i++) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("sentence", esentList.get(i) + "\n" + csentList.get(i)); //把英文中文例句对应组合
                list.add(map);
            }
            SimpleAdapter adapter = new SimpleAdapter(MainActivity.this,list,R.layout.sentencelistitem,new String[]{"sentence"},new int[]{R.id.sentence_list_item});
            sentence_listText.setAdapter(adapter);
        }
    }

}