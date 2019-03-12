package com.quad14.obdnewtry.activity;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialog;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.quad14.obdnewtry.CustomDataListModel;
import com.quad14.obdnewtry.CustomListDataAdaper;
import com.quad14.obdnewtry.R;
import com.quad14.obdnewtry.SQliteHelperClass;
import com.quad14.obdnewtry.io.MockObdGatewayService;
import com.quad14.obdnewtry.io.ObdGatewayService;
import com.quad14.obdnewtry.trips.TripLogOpenHelper;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

public class KmListData extends Fragment {

    View view;
    ListView DataListView3;
    ArrayList<CustomDataListModel> customDataListModelList;

    ArrayList<String> dataArrayModelList3;
    ArrayList<Integer> dataIntArrayList3;

    SQliteHelperClass myhelper;

    Thread UpdateList;

    ImageView Backlistarrow;
    ImageView DataClear,Checktable;
    CustomListDataAdaper customListDataAdaper;
    CardView Cardviewid;
    private Timer timer;
    private DonutProgress donutProgress;
    LinearLayout ProgressLinerid;
    MainActivity ma;
    TripLogOpenHelper tripLogOpenHelper;

    public KmListData() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataArrayModelList3=new ArrayList<>();
        dataIntArrayList3=new ArrayList<>();

        tripLogOpenHelper=new TripLogOpenHelper(getActivity());
        myhelper=new SQliteHelperClass(getActivity());
        UpdateList=new Thread();

        customDataListModelList=new ArrayList<>();

        //#######################################

        Cursor cursor=myhelper.getAllData();

        if(cursor.getCount() ==0){

            return;
        }

        while (cursor.moveToNext()) {

        try{
            customDataListModelList.add(new CustomDataListModel(cursor.getString(2),cursor.getString(3),cursor.getInt(1),cursor.getInt(4)));

            Log.e("AllListData","Date:"+cursor.getString(2));
            Log.e("AllListData","Time"+cursor.getString(3));
            Log.e("AllListData","Km:"+cursor.getInt(1));
            Log.e("AllListData","Dffer:"+cursor.getInt(4));

        }catch (Exception e){

            Log.e("LogOfSaviour","King of Log which is born for save the day of programmer");
        }

        }





    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.fragment_km_list_data, container, false);
        DataListView3=(ListView)view.findViewById(R.id.tkmrl);
        Backlistarrow=(ImageView)view.findViewById(R.id.backlistarrow);
        Cardviewid=(CardView)view.findViewById(R.id.cardview_id);
        DataClear=(ImageView)view.findViewById(R.id.deleteData);
        ProgressLinerid=(LinearLayout)view.findViewById(R.id.progressLinerid);
        donutProgress = (DonutProgress)view.findViewById(R.id.donut_progress);
        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.pullToRefresh);
        ProgressLinerid.setVisibility(View.INVISIBLE);
        Collections.reverse(customDataListModelList); // ADD THIS LINE TO REVERSE ORDER!
        customListDataAdaper = new CustomListDataAdaper(getActivity(), R.layout.customkmlist, customDataListModelList);
        DataListView3.invalidate();
        DataListView3.setAdapter(customListDataAdaper);
        ma=new MainActivity();
        if (customListDataAdaper.getCount() > 0) {

            DataClear.setVisibility(View.VISIBLE);
        } else {
            DataClear.setVisibility(view.INVISIBLE);

        }
        Backlistarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.Home.performClick();
            }
        });


        DataClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fancyDialog();
            }
        });

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                customListDataAdaper.clear();

                Cursor cursor=myhelper.getAllData();

                if(cursor.getCount() ==0){

                    return;
                }

                while (cursor.moveToNext()) {

                    try{
                        customDataListModelList.add(new CustomDataListModel(cursor.getString(2),cursor.getString(3),cursor.getInt(1),cursor.getInt(4)));
                        Collections.reverse(customDataListModelList);
                        customListDataAdaper.notifyDataSetChanged();

                    }catch (Exception e){

                        Log.e("LogOfSaviour","King of Log which is born for save the day of programmer");
                    }

                }


                pullToRefresh.setRefreshing(false);
            }
        });

        return view;
    }


//############--Mtethod gor delet folder which contain database--#############################

    public void DeleteDataFolder(){

        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                + File.separator+"MyCarData"
                + File.separator);
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                Log.e("File_List",children[i]);
                new File(dir, children[i]).delete();
            }
        }
    }


//###############################--Alert Dialog--########################################################

    public void fancyDialog(){

        new FancyAlertDialog.Builder(getActivity())
                .setTitle("Delete Records")
                .setBackgroundColor(Color.parseColor("#cc0000"))  //Don't pass R.color.colorvalue
                .setMessage("You can Delete all records by click Delete after that You are automatic redirecting to Home page")
                .setNegativeBtnText("Cancel")
                .setPositiveBtnBackground(Color.parseColor("#00cc00"))  //Don't pass R.color.colorvalue
                .setPositiveBtnText("Delete")
                .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))  //Don't pass R.color.colorvalue
                .setAnimation(Animation.SIDE)
                .isCancellable(true)
                .setIcon(R.drawable.delete_white, Icon.Visible)
                .OnPositiveClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                       myhelper.deleteall();
                       customListDataAdaper.notifyDataSetChanged();
                       tripLogOpenHelper.deleteall();
                       coolProgressBar();

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    if(timer != null) {
                                        timer.cancel();
                                        timer = null;
                                    }
                                    ProgressLinerid.setVisibility(View.INVISIBLE);

                                    MainActivity.Home.performClick();
                                }
                            }, 4000);

                    }
                })
                .OnNegativeClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {


                    }
                })
                .build();

    }


    //##########--Restart app--#############################################

    public void RestartApp(){

        Intent i = getActivity().getPackageManager()
                .getLaunchIntentForPackage( getActivity().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);

    }

    //###########-close app--###############################################
    public void closeApp(){

        getActivity().finish();
        System.exit(0);
    }


    //###########--timer with cool Progress bar--############################


    public void coolProgressBar(){
        ProgressLinerid.setBackgroundResource(R.color.transprant);
        ProgressLinerid.setVisibility(View.VISIBLE);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boolean a = false;
                        if (a) {
                            @SuppressLint("ObjectAnimatorBinding") ObjectAnimator anim = ObjectAnimator.ofInt(donutProgress, "progress", 0, 10);
                            anim.setInterpolator(new DecelerateInterpolator());
                            anim.setDuration(4000);
                            anim.start();
                        } else {
                            AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.progress_anim);
                            set.setInterpolator(new DecelerateInterpolator());
                            set.setTarget(donutProgress);
                            set.start();
                        }
                    }
                });
            }
        }, 0, 4000);
    }



//#################--Gif Dialog methods --#################################################################

    public void QuestionMark(String title,String info ){
        new TTFancyGifDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(info)
                .setPositiveBtnText("Ok")
                .setPositiveBtnBackground("#2E7D32")
                .setGifResource(R.drawable.satiesfingcar)      //pass your gif, png or jpg
                .isCancellable(true)
                .build();

    }
    //#####################################checking Service is running or not################################

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}