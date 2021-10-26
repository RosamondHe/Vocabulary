package com.swufestu.vocabulary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.xml.sax.InputSource;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;

public class Dictionary {
    public Context context;
    public String tableName;
    private DictDBHelper dbHelper;
    private SQLiteDatabase dbR, dbW;


    public Dictionary(Context context,String tableName){
        this.context=context;
        this.tableName=tableName;
        dbHelper=new DictDBHelper(context, tableName);  //这里要用到前面的DictDBHelper类，在构造方法中实例化该类，
        //调用下面两个方法获得dbR和dbW,用于完成对数据库的增删改查操作。
        //dbR dbW作为成员变量目的：避免反复实例化造成数据库指针泄露。
        dbR=dbHelper.getReadableDatabase();
        dbW=dbHelper.getWritableDatabase();
    }

    //在该对象销毁时，释放dbR和dbW
    @Override
    protected void finalize() throws Throwable {
        dbR.close();
        dbW.close();
        dbHelper.close();
        super.finalize();
    }

    //将包含单词信息的WordValue对象添加进数据库，这里使用了dbW的insert方法，需要创建一个ContentValues对象存放键值对
    public void insertWordToDict(WordMessage wordmessage, boolean isOverWrite){
        //避免空指针异常
        if(wordmessage==null){
            return;
        }
        Cursor cursor=null; //游标
        try{
            ContentValues values=new ContentValues();
            values.put("word",wordmessage.getWord() );
            values.put("pse", wordmessage.getPsE());
            values.put("prone",wordmessage.getPronE());
            values.put("psa", wordmessage.getPsA());
            values.put("prona", wordmessage.getPronA());
            values.put("meaning",wordmessage.getMeaning());
            values.put("sentorig", wordmessage.getSentOrig());
            values.put("senttrans", wordmessage.getSentTrans());
            cursor=dbR.query(tableName, new String[]{"word"}, "word=?", new String[]{w.getWord()}, null, null, null);
            if(cursor.getCount()>0){
                if(isOverWrite==false)//首先看看数据库中有没有这个单词，若词典库中已经有了这一个单词，所以不再操作
                    return;
                else{ //执行更新操作
                    dbW.update(tableName, values, "word=?",new String[]{ w.getWord()});
                }
            }else{
                dbW.insert(tableName, null, values);
                //这里可能会发生空指针异常，到时候考虑
            }
        }catch(Exception e){

        }finally{
            if(cursor!=null)
                cursor.close();
        }

    }

    //判断数据库中是否存在某个单词
    public boolean isWordExist(String word){

        Cursor cursor=null;
        try{
            cursor=dbR.query(tableName, new String[]{"word"}, "word=?", new String[]{word}, null, null, null);
            if(cursor.getCount()>0){
                cursor.close();
                return true;
            }else{
                cursor.close();
                return false;
            }
        }finally{
            if(cursor!=null)
                cursor.close();
        }

    }

    //从单词库中获得某个单词的信息，如果词库中没有改单词，那么返回null
    public WordMessage getWordFromDict(String searchedWord){
        WordMessage w=new WordMessage();//预防空指针异常
//        db.execSQL("create table dict(word text,pse text,prone text,psa text,prona text," +
//                "interpret text, sentorig text, senttrans text)");
        String[] columns=new String[]{"word",
                "pse","prone","psa","prona","interpret","sentorig","senttrans"};

        String[] strArray=new String[8];
        Cursor cursor=dbR.query(tableName, columns, "word=?", new String[]{searchedWord}, null, null, null);
        while(cursor.moveToNext()){
            for(int i=0;i<strArray.length;i++){
                strArray[i]=cursor.getString(cursor.getColumnIndex(columns[i]));

            }
            w=new WordMessage(strArray[0],strArray[1],strArray[2],strArray[3],strArray[4],strArray[5],strArray[6],strArray[7]);
        }
        cursor.close();
        return w;
    }

    //从网络查找某个单词，并且返回一个含有单词信息的WordValue对象，这个方法在第二讲的最后提过
    public WordMessage getWordFromInternet(String searchedWord){
        WordMessage wordValue=null;
        String tempWord=searchedWord;
        if(tempWord==null&& tempWord.equals(""))
            return null;
        char[] array=tempWord.toCharArray();
        if(array[0]>256)           //是中文，或其他语言的的简略判断
            tempWord="_"+ URLEncoder.encode(tempWord);
        InputStream in=null;
        String str=null;
        try{
            String tempUrl=NetOperator.iCiBaURL1+tempWord+NetOperator.iCiBaURL2;
            in=NetOperator.getInputStreamByUrl(tempUrl);
            if(in!=null){
                new FileUtils().saveInputStreamToFile(in, "", "gfdgf.txt");
                XMLParser xmlParser=new XMLParser();
                InputStreamReader reader=new InputStreamReader(in,"utf-8");
                HandleContent contentHandler=new HandleContent();
                xmlParser.parseJinshanXml(contentHandler, new InputSource(reader));
                wordValue=contentHandler.getWordMessage();
                wordValue.setWord(searchedWord);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return wordValue;
    }


    //以下几个方法都是获得某个单词的某一项信息，基本的思路还是先获得全部信息WordValue然后调用WordValue的get方法获得具体的信息。
    //获取发音文件地址
    public String getPronEngUrl(String searchedWord){
        Cursor cursor=dbR.query(tableName, new String[]{"prone"}, "word=?", new String[]{searchedWord}, null, null, null);
        if(cursor.moveToNext()==false){
            cursor.close();
            return null;
        }
        String str=cursor.getString(cursor.getColumnIndex("prone"));
        cursor.close();
        return str;

    }

    public String getPronUSAUrl(String searchedWord){
        Cursor cursor=dbR.query(tableName, new String[]{"prona"}, "word=?", new String[]{searchedWord}, null, null, null);
        if(cursor.moveToNext()==false){
            cursor.close();
            return null;
        }
        String str=cursor.getString(cursor.getColumnIndex("prona"));
        cursor.close();
        return str;
    }

    //获取音标
    public String getPsEng(String searchedWord){
        Cursor cursor=dbR.query(tableName, new String[]{"pse"}, "word=?", new String[]{searchedWord}, null, null, null);
        if(cursor.moveToNext()==false){
            cursor.close();
            return null;
        }
        String str=cursor.getString(cursor.getColumnIndex("pse"));
        cursor.close();
        return str;
    }

    public String getPsUSA(String searchedWord){
        Cursor cursor=dbR.query(tableName, new String[]{"psa"}, "word=?", new String[]{searchedWord}, null, null, null);
        if(cursor.moveToNext()==false){
            cursor.close();
            return null;
        }
        String str=cursor.getString(cursor.getColumnIndex("psa"));
        cursor.close();
        return str;
    }


    /**
     * 若没有句子那么返回的链表的长度为0,若单词不存在那么直接返回null,所以最好对null和长度同时检验
     * @param searchedWord
     * @return
     */


    public String getInterpret(String searchedWord){
        Cursor cursor=dbR.query(tableName, new String[]{"interpret"}, "word=?", new String[]{searchedWord}, null, null, null);
        if(cursor.moveToNext()==false){
            cursor.close();
            return null;
        }
        String str=cursor.getString(cursor.getColumnIndex("interpret"));
        cursor.close();
        return str;

    }
}
