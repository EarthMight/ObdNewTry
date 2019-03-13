package com.quad14.obdnewtry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

import com.quad14.obdnewtry.activity.KmDataModel;

import java.io.File;

public class SQliteHelperClass  extends SQLiteOpenHelper {
    public static final String Dabase_name= "UserRecord.db";
    public static final String Table_name= "RTable";
    public static final String col_0= "Id";
    public static final String col_1= "KM";
    public static final String col_2= "MDATE";
    public static final String col_3= "MTIME";
    public static final String col_4= "MDIFFER";
    public static final String col_5= "MRPM";
    public static final String col_6= "MSPEED";
    public static final String col_7= "MRUNTIME";


//
//    public SQliteHelperClass(Context context) {
//        super(context, Dabase_name, null, 1);
//    }

    public SQliteHelperClass(Context context) {
        super(context, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                + File.separator+"MyCarData"
                + File.separator +  Dabase_name, null, 1);

    }


    private File getDisc(){
        File file=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        return new File(file,"MyCarData");

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+ Table_name +"(Id INTEGER PRIMARY KEY AUTOINCREMENT, KM INTEGER, MDATE TEXT, MTIME TEXT , MDIFFER INTEGER, MRPM TEXT , MSPEED TEXT , MRUNTIME TEXT) ");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ Table_name);
        onCreate(db);
    }

    public boolean insertData(KmDataModel kmDataModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col_0,kmDataModel.getId());
        contentValues.put(col_1,kmDataModel.getKm());
        contentValues.put(col_2,kmDataModel.getDate());
        contentValues.put(col_3,kmDataModel.getTime());
        contentValues.put(col_4,kmDataModel.getDffKm());
        contentValues.put(col_5,kmDataModel.getRpm());
        contentValues.put(col_6,kmDataModel.getSpeed());
        contentValues.put(col_7,kmDataModel.getRuntime());
        long result =db.insert(Table_name,null,contentValues);
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public Boolean updateRecord(String id ,String Km,String Date ,String Time){

        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(col_1,Km);
        contentValues.put(col_2,Date);

        db.update(Table_name, contentValues, "Id = ?",new String[] { id });
        return true;
    }

    public Cursor getAllData() {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from "+Table_name,null);

//        if(res.getCount()!=0) {
//            res.moveToFirst();
//            Log.w("getAllData:total:-", String.valueOf(res.getCount()));
//            Log.w("getAllData:col1:-", String.valueOf(res.getString(0)));
//            Log.w("getAllData:col2:-", String.valueOf(res.getString(1)));
//            Log.w("getAllData:col3:-", String.valueOf(res.getString(2)));
//        }

        return res;
    }

    public Cursor getLastData() {
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("SELECT * \n" +
                "    FROM    RTable \n" +
                "    WHERE   Id = (SELECT MAX(Id)  FROM RTable);",null);

       /* Log.w("getLastData:total:-", String.valueOf(res.getCount()));
        Log.w("getAllData:col1:-", String.valueOf(res.getString(Integer.parseInt(col_0))));
        Log.w("getAllData:col2:-", String.valueOf(res.getString(Integer.parseInt(col_1))));
        Log.w("getAllData:col3:-", String.valueOf(res.getString(Integer.parseInt(col_2))));*/

        return res;
    }

    public Cursor getSecondLastData() {
        SQLiteDatabase db=this.getWritableDatabase();
        Cursor res=db.rawQuery("SELECT * \n" +
                "    FROM    RTable \n" +
                "    WHERE   Id = (SELECT MAX(Id)-1  FROM RTable);",null);

       /* Log.w("getLastData:total:-", String.valueOf(res.getCount()));
        Log.w("getAllData:col1:-", String.valueOf(res.getString(Integer.parseInt(col_0))));
        Log.w("getAllData:col2:-", String.valueOf(res.getString(Integer.parseInt(col_1))));
        Log.w("getAllData:col3:-", String.valueOf(res.getString(Integer.parseInt(col_2))));*/

        return res;
    }

    public void deleteData () {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Table_name, null, null);

    }
    public void deleteall()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ Table_name);
    }

    public boolean isTableExists(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
        if(cursor!=null) {
            if(cursor.moveToFirst()) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

}

