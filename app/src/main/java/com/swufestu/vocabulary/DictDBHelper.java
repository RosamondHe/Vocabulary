package com.swufestu.vocabulary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DictDBHelper extends SQLiteOpenHelper {
    public Context mContext;
    public String tableName;
    public static int VERSION=1; //数据库版本号

    public DictDBHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext=context;
        tableName=name;
    }

    public DictDBHelper(Context context, String name, CursorFactory factory){
        this(context,name,factory,VERSION);
        mContext=context;
        tableName=name;
    }

    public DictDBHelper(Context context, String name){
        this(context,name,null);
        mContext=context;  //上下文对象
        tableName=name;  //数据库名称
    };

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建一个表，各个列标签
        db.execSQL("create table dict(word, pse, prone, psa, prona, meaning, sentorig, senttrans)"); //存储字典
        db.execSQL("create table Notebook(word)"); //存储单词表
    }

    //数据库升级时自动调用
    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

    }

}
