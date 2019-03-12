package com.quad14.obdnewtry;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jackandphantom.blurimage.BlurImage;
import com.quad14.obdnewtry.activity.MainActivity;
import com.quad14.obdnewtry.sql.SQLdbClass;

import java.text.SimpleDateFormat;
import java.util.Date;

public class splash extends Activity {
    String date,dateURL;
    SQLdbClass sqLdbClass;
    ImageView carIcon;
    TextView CarTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getActionBar().hide();
        carIcon=(ImageView)findViewById(R.id.carid);
        CarTxt=(TextView)findViewById(R.id.cartxtid);


//        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//        date = sdf.format(new Date());
//
//        sqLdbClass = new SQLdbClass(this);
//
//        Cursor cursor= sqLdbClass.getData();
//
//        if (cursor!=null)
//        {
//            if (cursor.moveToFirst()) {
//                do {
//                    dateURL = cursor.getString(1);
////                    km=cursor.getString(2);
//                    Log.e("date12","==="+dateURL);
////                    Toast.makeText(getApplicationContext(), "==="+km, Toast.LENGTH_SHORT).show();
//
//                } while (cursor.moveToNext());
//            }
//            cursor.close();
//        }
//        else
//        {
//            Toast.makeText(getApplicationContext(), "nulll", Toast.LENGTH_SHORT).show();
//
//        }

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if(!date.equals(dateURL)) {
////            sqLdbClass.updateFavorite(date, "0");
//                    sqLdbClass.insertdata(date,1,0);
////            finish();
////            startActivity(getIntent());
//
//                    Log.e("dateURL","=="+dateURL);
//                    Log.e("date","=="+date);
//
//                }
//                finish();
//
//            }
//
//        }, 5000);

        final Animation startup= AnimationUtils.loadAnimation(this,R.anim.startupanimation);
        carIcon.startAnimation(startup);
        CarTxt.startAnimation(startup);
        final Intent i=new Intent(splash.this,MainActivity.class);
        Thread timer=new Thread(){
            public void run (){
                try{
                    sleep(5000);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                    finish();
                }
            }
        };

        timer.start();

    }
}
