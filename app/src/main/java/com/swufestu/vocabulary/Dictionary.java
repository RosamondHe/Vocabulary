package com.swufestu.vocabulary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.xml.sax.InputSource;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class Dictionary {
    public Context context;
    public String tableName;  //数据库表名
    private DictDBHelper dbHelper;
    private SQLiteDatabase dbRead, dbWrite;


    public Dictionary(Context context, String tableName) {
        this.context = context;
        this.tableName = tableName;
        dbHelper = new DictDBHelper(context, tableName);  //这里要用到前面的DictDBHelper类，在构造方法中实例化该类
        //调用下面两个方法获得dbRead和dbWrite,用于读写数据库
        //dbRead dbWrite作为成员变量目的：避免反复实例化造成数据库指针泄露
        dbRead = dbHelper.getReadableDatabase();
        dbWrite = dbHelper.getWritableDatabase();
    }

    //将包含单词信息的WordMessage对象添加进数据库
    //使用dbWrite的insert方法，创建一个ContentValues对象，类似一个map通过键值对的形式存储值
    public void insertWordToDictionary(WordMessage wordmessage, boolean isOverWrite) {
        //避免空指针异常
        if (wordmessage == null) {
            return;
        }
        Cursor cursor = null; //游标
        try {
            ContentValues values = new ContentValues();  //插入数据
            values.put("word", wordmessage.getWord());
            values.put("pse", wordmessage.getPsE());
            values.put("prone", wordmessage.getPronE());
            values.put("psa", wordmessage.getPsA());
            values.put("prona", wordmessage.getPronA());
            values.put("meaning", wordmessage.getMeaning());
            values.put("sentorig", wordmessage.getSentOrig());
            values.put("senttrans", wordmessage.getSentTrans());
            //在词典中查询单词
            //四个参数分别为：表名、属性名（列名）、select语句 （含有?占位符）、占位符的值
            cursor = dbRead.query(tableName, new String[]{"word"}, "word=?", new String[]{wordmessage.getWord()}, null, null, null);

            //首先查看数据库中有没有这个单词
            if (cursor.getCount() > 0) {  //总数据项数量
                if (isOverWrite == false) //若词典库中已经有了这一个单词，所以不再操作
                    return;
                else {                  //执行更新操作
                    dbWrite.update(tableName, values, "word=?", new String[]{wordmessage.getWord()});
                }
            } else {
                dbWrite.insert(tableName, null, values); //插入新单词
                //这里可能会发生空指针异常，到时候考虑
            }
        } catch (Exception e) {

        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    //判断数据库中是否存在某个单词
    public boolean isWordExist(String word) {
        Cursor cursor = null;
        try {
            cursor = dbRead.query(tableName, new String[]{"word"}, "word=?", new String[]{word}, null, null, null);
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;  //存在
            } else {
                cursor.close();
                return false;  //不存在
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    //从单词库中获得某个单词的信息，如果词库中没有这个单词，那么返回null
    public WordMessage getWordFromDictionary(String wordToSearch) {
        WordMessage w = new WordMessage();//预防空指针异常
        String[] columns = new String[]{"word", "pse", "prone", "psa", "prona", "meaning", "sentorig", "senttrans"}; //列名
        String[] strArray = new String[8];  //存放在数据库中得到的数据
        Cursor cursor = dbRead.query(tableName, columns, "word=?", new String[]{wordToSearch}, null, null, null);
        while (cursor.moveToNext()) { //向下查找
            for (int i = 0; i < strArray.length; i++) {
                strArray[i] = cursor.getString(cursor.getColumnIndexOrThrow(columns[i]));  //列名--列索引--内容
            }
            // 将单词信息存储到WordMessage
            w = new WordMessage(strArray[0], strArray[1], strArray[2], strArray[3], strArray[4], strArray[5], strArray[6], strArray[7]);
        }
        cursor.close();
        return w;
    }

    //从网络查找某个单词，返回含有单词信息的WordMessage对象
    public WordMessage getWordFromInternet(String wordToSearch) {
        WordMessage w2 = null;
        String search = wordToSearch;
        if (search == null || search.isEmpty())
            return null;
        char[] array = search.toCharArray();
        if (array[0] > 256)  //是中文，或其他语言的的简略判断
            search = "_" + URLEncoder.encode(search); //填入网址的部分
        //HttpURL中存在中文的话，会因为编码的问题产生乱码，所以先要对中文调用URLEncoder.encode()方法进行一下编码
        InputStream in;
        try {
            String tempUrl = NetOperator.iCiBaURL1 + search + NetOperator.iCiBaURL2; //单词xml网址
            in = NetOperator.getInputStreamByUrl(tempUrl);
            if (in != null) {
                //new FileUtils().saveInputStreamToFile(in, "", "vocabulary.txt");  //调用存储sd卡方法
                XMLParser xmlParser = new XMLParser();
                //InputStream是二进制字节流，必须先经过InputStreamReader包装成字符流在创建InputSource对象，否则会出现编码异常
                InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8);
                HandleContent contentHandler = new HandleContent();
                xmlParser.parseJinshanXml(contentHandler, new InputSource(reader));
                w2 = contentHandler.getWordMessage();
                //w2.setWord(wordToSearch);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return w2;
    }


    //以下几个方法都是获得某个单词的某一项信息。先获得全部信息WordMessage然后调用get方法获得具体的信息。

    //获取英音文件地址
    public String getProneUrl(String wordToSearch) {
        Cursor cursor = dbRead.query(tableName, new String[]{"prone"}, "word=?", new String[]{wordToSearch}, null, null, null);
        if (cursor.moveToNext() == false) {
            cursor.close();
            return null;
        }
        String str = cursor.getString(cursor.getColumnIndexOrThrow("prone"));
        cursor.close();
        return str;
    }

    //获取美音文件地址
    public String getPronaUrl(String wordToSearch) {
        Cursor cursor = dbRead.query(tableName, new String[]{"prona"}, "word=?", new String[]{wordToSearch}, null, null, null);
        if (cursor.moveToNext() == false) {
            cursor.close();
            return null;
        }
        String str = cursor.getString(cursor.getColumnIndexOrThrow("prona"));
        cursor.close();
        return str;
    }

    //在该对象销毁时，释放dbR和dbW
    @Override
    protected void finalize() throws Throwable {
        dbRead.close();
        dbWrite.close();
        dbHelper.close();
        super.finalize();
    }

}
