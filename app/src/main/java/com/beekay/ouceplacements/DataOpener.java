package com.beekay.ouceplacements;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by bvepuri on 4/11/2016.
 */
public class DataOpener {

    private static final String DBNAME = "cred.db";
    private static final String TABLE_NAME = "ptable";
    private static final String USER = "user";
    private static final String PASS = "pass";
    private static final int VERSION = 1;
    private static final String STATEMENT = "CREATE TABLE IF NOT EXISTS ptable(user text not null,pass text not null);";
    private SQLiteDatabase db;
    private DataHelper helper;
    Context context;

    public DataOpener(Context context){
        this.context = context;
        helper = new DataHelper(context);
    }

    public class DataHelper extends SQLiteOpenHelper{

        public DataHelper(Context context) {
            super(context, DBNAME, null, VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            try{
                sqLiteDatabase.execSQL(STATEMENT);
            }catch (SQLException ex){
                Log.v("Exception",ex.toString());
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS ptable");
            onCreate(sqLiteDatabase);
        }
    }

    public DataOpener open(){
        db = helper.getWritableDatabase();
        return this;
    }

    public void close(){
        helper.close();
    }

    public DataOpener openRead(){
        db = helper.getReadableDatabase();
        return this;
    }

    public long insertData(String user, String pass){
        ContentValues values = new ContentValues();
        values.put(USER,user);
        values.put(PASS,pass);
        return db.insertOrThrow(TABLE_NAME,null,values);
    }

    public long upgrade(String user, String pass){
        ContentValues values = new ContentValues();
        values.put(USER,user);
        values.put(PASS,pass);
        return db.update(TABLE_NAME,values,USER+"="+user,null);
    }

    public Cursor retrieve(){
        return db.query(TABLE_NAME, new String[]{USER, PASS}, null, null, null, null, null);
    }

    public Cursor retrieve(String user){
        return db.query(TABLE_NAME, new String[]{USER,PASS},"user=?",new String[]{user},null,null,null);
    }
}
