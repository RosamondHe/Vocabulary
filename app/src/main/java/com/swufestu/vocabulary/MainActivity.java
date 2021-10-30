package com.swufestu.vocabulary;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.swufestu.vocabulary.adapter.SentenceListAdapter;

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
    public mp3Player mp3player = null;
    public WordMessage wordmessage = null;
    public Handler dictHandler = null;
    public DictDBHelper notebookHelper = null;
    public static String wordToSearch = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initial();
        setOnClickLis();
        new ThreadSearchWord().start();
    }


    //获取控件
    public void initial() {
        //各个显示
        wordText = (TextView) findViewById(R.id.word);  //单词
        ephoneticText = (TextView) findViewById(R.id.ephonetic); //英音标
        aphoneticText = (TextView) findViewById(R.id.aphonetic); //美音标
        meaningText = (TextView) findViewById(R.id.meaning); //释义
        sentence_listText = (ListView) findViewById(R.id.sentence_list);  //例句

        //各个按钮
        backButton = (ImageButton) findViewById(R.id.back);
        searchButton = (ImageButton) findViewById(R.id.search);
        clearButton = (ImageButton) findViewById(R.id.clear);
        addWordlistButton = (ImageButton) findViewById(R.id.addwordlist);
        openWordListButton = findViewById(R.id.openWordList);
        ephonetic_btnButton = (ImageButton) findViewById(R.id.ephonetic_btn);
        aphonetic_btnButton = (ImageButton) findViewById(R.id.aphonetic_btn);

        //输入
        inputText = (EditText) findViewById(R.id.input);
        inputText.setOnEditorActionListener(new EditTextActionLis()); //监听

        //工具
        dictionary = new Dictionary(MainActivity.this, "dict");
        mp3player = new mp3Player(MainActivity.this, "dict");
        notebookHelper = new DictDBHelper(MainActivity.this, "Notebook");
        dictHandler = new Handler(Looper.getMainLooper());

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
        if (!npse.equals("") && !npsa.equals("")) {    //只有有音标时才去下载音乐
            mp3player.playMusic(nword, mp3Player.ENGLISH_ACCENT, true, false);
            mp3player.playMusic(nword, mp3Player.USA_ACCENT, true, false);
        }
    }


    //设置监听
    public void setOnClickLis() {
        backButton.setOnClickListener(new BackClickLis());
        clearButton.setOnClickListener(new ClearClickLis());
        searchButton.setOnClickListener(new SearchClickLis());
        ephonetic_btnButton.setOnClickListener(new PlayMusicClickLis(mp3Player.ENGLISH_ACCENT));
        aphonetic_btnButton.setOnClickListener(new PlayMusicClickLis(mp3Player.USA_ACCENT));
        // 添加单词到单词本
        addWordlistButton.setOnClickListener(v -> {
            String word = (String) wordText.getText();
            if (word.isEmpty()) return;
            Cursor cursor = notebookHelper.getReadableDatabase().rawQuery("Select * from Notebook where word = ?;",
                    new String[]{word});
            if (cursor.moveToNext()) {
                cursor.close();
                return;
            }
            notebookHelper.getWritableDatabase().execSQL(
                    "insert into Notebook values (?);", new String[]{word}
            );
        });
        // 打开单词本
        openWordListButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WordListActivity.class);
            startActivityForResult(intent, 0);
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 单词本中点击单词后，查询这个单词
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String word = data.getStringExtra("word");
                inputText.setText(word);
                searchButton.callOnClick();
            }
        }
    }

    //开始播放音频
    protected void musicStart() {
        super.onStart();
        mp3player.isMusicPermitted = true;
    }

    //暂缓播放音频
    protected void musicPause() {
        mp3player.isMusicPermitted = false;
        super.onPause();
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
                //sentence_listText.setText("");
                return;
            }
            int count = 0;
            if (esentList.size() <= csentList.size()) {
                count = esentList.size();
            } else {
                count = csentList.size();  //取两者长度最小值，但一般二者长度相等
            }
            ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
            for (int i = 0; i < count; i++) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("sentence", esentList.get(i) + "\n" + csentList.get(i));
                list.add(map);
            }
            SentenceListAdapter adapter = new SentenceListAdapter(MainActivity.this, R.layout.sentencelistitem, list, new String[]{"sentence"}, new int[]{R.id.text_dict_sentence_list_item});
            sentence_listText.setAdapter(adapter);
        }

    }

    //以下是各个按钮接口
    //返回
    class BackClickLis implements View.OnClickListener {
        @Override
        public void onClick(View arg0) {
            MainActivity.this.finish();
        }
    }

    //删除输入
    class ClearClickLis implements View.OnClickListener {
        @Override
        public void onClick(View arg0) {
            inputText.setText("");
        }

    }

    //搜索
    class SearchClickLis implements View.OnClickListener {
        @Override
        public void onClick(View arg0) {
            startSearch();
        }
    }

    //播放音频
    class PlayMusicClickLis implements View.OnClickListener {
        public int accent = 0;

        public PlayMusicClickLis(int accent) {
            super();
            this.accent = accent;
        }

        @Override
        public void onClick(View arg0) {
            mp3player.playMusic(wordToSearch, accent, false, true);
        }
    }

//    class IBDictAddWordToGlossaryClickLis implements View.OnClickListener {


//        }
//    }


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

    //开始搜索
    public void startSearch() {
        String str = inputText.getText().toString(); //查询输入
        if (str == null || str.equals("")) //输入为空
            return;
        wordToSearch = str;
        new ThreadSearchWord().start();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(inputText.getWindowToken(), 0);
    }
}