package com.quad14.obdnewtry.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLdbClass extends SQLiteOpenHelper {
    private static final String UID="id";
    private static final String DATE="date";
    private static final String TOTALKM="number";
    private static final String DIFFKM="number1";
    private static final String TABLE_NAME="obddata";
    SQLiteDatabase database;

    public SQLdbClass(Context context) {
        super(context,"androidsqlite.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String quary;
        quary = "CREATE TABLE "+TABLE_NAME+"("+UID+" INTEGER primary key,"+DATE+" TEXT,"+TOTALKM+" INTEGER,"+DIFFKM+" INTEGER)";
        sqLiteDatabase.execSQL(quary);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String qyery;
        qyery = "DROP TABLE IF EXISTS "+TABLE_NAME;
        sqLiteDatabase.execSQL(qyery);
        onCreate(sqLiteDatabase);

    }
    public long insertdata(String date, int Number ,int Number1){
         database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DATE,date);
        contentValues.put(TOTALKM,Number);
        contentValues.put(DIFFKM,Number1);
        return database.insert(TABLE_NAME,null,contentValues);
    }

    public Cursor getData(){
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select * from "+TABLE_NAME,null);
        Log.v("Cursor Object", DatabaseUtils.dumpCursorToString(res));

        return res;

    }

    public Cursor getkm1()
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.rawQuery("select * from "+TABLE_NAME +" ORDER BY number DESC LIMIT 1,1",null);
       // SELECT DISTINCT value FROM Table ORDER BY value DESC LIMIT 2

        return res;
    }

    public  Cursor getkm()
    {
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor res=db.query(TABLE_NAME, new String [] {"MAX(number)"}, null, null, null, null, null);
        return res;
    }

    public void updateFavorite(String date, int Number){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TOTALKM,Number);
        db.update(TABLE_NAME, contentValues, "date = ?",new String[] { date });

    }

    public void diffkm(String date, int Number){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DIFFKM,Number);
        db.update(TABLE_NAME, contentValues, "date = ?",new String[] { date });

    }

    public Cursor getAllContacts() {

        SQLiteDatabase db = this.getWritableDatabase();
        String[] columns = {UID,DATE,TOTALKM,DIFFKM};
        Cursor data = db.query(TABLE_NAME,columns,null,null,null,null,null);
        return data;

    }

}
