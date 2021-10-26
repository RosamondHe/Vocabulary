package com.swufestu.vocabulary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DictDBHelper extends SQLiteOpenHelper {
    public Context mContext=null;
    public String tableName=null;
    public static int VERSION=1;
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
        mContext=context;
        tableName=name;
    };

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建一个表，各个列标签
        db.execSQL("create table dict(word, pse, prone, psa, prona, meaning, sentorig, senttrans)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {

    }

}
