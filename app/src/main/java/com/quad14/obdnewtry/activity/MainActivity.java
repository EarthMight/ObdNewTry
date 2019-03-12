package com.quad14.obdnewtry.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;

import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialog;
import com.github.pires.obd.commands.ObdCommand;
import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.LoadCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.control.DistanceSinceCCCommand;

import com.github.pires.obd.commands.engine.RuntimeCommand;
import com.github.pires.obd.commands.temperature.EngineCoolantTemperatureCommand;
import com.github.pires.obd.enums.AvailableCommandNames;
import com.google.inject.Inject;
import com.irozon.sneaker.Sneaker;
import com.quad14.obdnewtry.R;
import com.quad14.obdnewtry.SQliteHelperClass;
import com.quad14.obdnewtry.SpeedAndRpm;
import com.quad14.obdnewtry.SpeedFragment;
import com.quad14.obdnewtry.SpeedModel;
import com.quad14.obdnewtry.RpmsFragment;
import com.quad14.obdnewtry.TotalKmModel;
import com.quad14.obdnewtry.TotalTravelFrag;
import com.quad14.obdnewtry.config.ObdConfig;
import com.quad14.obdnewtry.config.ObdConfig2;
import com.quad14.obdnewtry.io.AbstractGatewayService;
import com.quad14.obdnewtry.io.LogCSVWriter;
import com.quad14.obdnewtry.io.MockObdGatewayService;
import com.quad14.obdnewtry.io.ObdCommandJob;
import com.quad14.obdnewtry.io.ObdGatewayService;
import com.quad14.obdnewtry.io.ObdProgressListener;
import com.quad14.obdnewtry.net.ObdReading;
import com.quad14.obdnewtry.net.ObdService;
import com.quad14.obdnewtry.trips.TripLog;
import com.quad14.obdnewtry.trips.TripRecord;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import edu.arbelkilani.compass.Compass;
import edu.arbelkilani.compass.CompassListener;
import im.delight.android.audio.SoundManager;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.RoboGuice;
import roboguice.activity.RoboActivity;

import static com.quad14.obdnewtry.activity.ConfigActivity.getGpsDistanceUpdatePeriod;
import static com.quad14.obdnewtry.activity.ConfigActivity.getGpsUpdatePeriod;


// Some code taken from https://github.com/barbeau/gpstest

public class MainActivity extends RoboActivity implements ObdProgressListener, LocationListener, GpsStatus.Listener, View.OnClickListener {

    private static final String TAG = MainActivity.class.getName();
    private static final int NO_BLUETOOTH_ID = 0;
    private static final int BLUETOOTH_DISABLED = 1;
    private static final int START_LIVE_DATA = 2;
    private static final int STOP_LIVE_DATA = 3;
    private static final int SETTINGS = 4;
    private static final int GET_DTC = 5;
    private static final int TABLE_ROW_MARGIN = 7;
    private static final int NO_ORIENTATION_SENSOR = 8;
    private static final int NO_GPS_SUPPORT = 9;
    private static final int TRIPS_LIST = 10;
    private static final int SAVE_TRIP_NOT_AVAILABLE = 11;
    private static final int REQUEST_ENABLE_BT = 1234;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private static boolean bluetoothDefaultIsEnable = false;
    static {
        RoboGuice.setUseAnnotationDatabases(false);
    }

    public Map<String, String> commandResult = new HashMap<String, String>();
    boolean mGpsIsStarted = false;
    private LocationManager mLocService;
    private LocationProvider mLocProvider;
    private LogCSVWriter myCSVWriter;
    private Location mLastLocation;
/// the trip log
    private TripLog triplog;
    private TripRecord currentTrip;

//Text views
    private TextView btStatusTextView;
    private TextView rpm;
    private TextView speed;
    private TextView TotalKm;
    private TextView EngineRuntime;
    private TextView KmDifference;
    private TextView SaveKmChk;
    private LinearLayout rpmmain;

    private static final int REQUEST_PERMISSION_LOCATION = 255; // int should be between 0 and 255

    private TextView obdStatusTextView;
    private TextView gpsStatusTextView;
    private LinearLayout vv;
    private TableLayout tl;
    private boolean isServiceBound;
    private AbstractGatewayService service;

    private Sensor orientSensor = null;
    private PowerManager.WakeLock wakeLock = null;
    private boolean preRequisites = true;

    @Inject
    private SensorManager sensorManager;
    @Inject
    private PowerManager powerManager;
    @Inject
    private SharedPreferences prefs;

    FrameLayout Framelauoutcontainer;

    Compass compass;
    Bundle Sbundle,Rbundle,TravelBundle,SrBundle,RsBundle;
    Button SpeedButton,TestButton;
    Button TotalTravelDistance,SpeedAndRpm;
    LinearLayout TotalkmLinear;
    LinearLayout Lastrecordliner;

    public static Button Home;

    LinearLayout TravelLinear;
    Button SecondlastRecord;

    public static boolean speedclick=false;
    public static boolean testclick=false;
    public static boolean rpmclick=false;
    public static boolean TravelClick=false;
    public static boolean SnRClick=false;
    public static boolean alertThreadChecker=false;
    public static boolean homeclick=false;
    SpeedModel speedModel;
    List<SpeedModel> speedModelList;

    Thread speedthread,rpmthread,totalTravelThread,speedAndRpmThread,AlertThread,TestingThread,UpdateDiffer;
    Integer i=0;
    Integer j=0;
    Integer k=0;
    Integer r=0;
    Integer t=0;
    Integer c=0;
    Integer l=0;

    private SoundManager mSoundManager;

    private ImageView SpeedQuestiomark,RpmQuestioMark,TravelQuestionmark,EngineRuntimeQuestionmark,kmdifferQuestionmark;

    SQliteHelperClass myhelper;
    TotalKmModel totalKmModel;

    List<String> dataArrayModelList;
    List<Integer> dataIntArrayList;

    List<String> dataArray2ModelList;
    List<Integer> dataInt2ArrayList;
    //**display commponent**

    int height,width,densitydpi;
    float density;

    List<Integer> DifferKmTotal;

    String DifferUpdate;

    private SQLiteDatabase db;
//##################################--Oncreate Method--##############################################

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayInfo();

        if(height>2280 && densitydpi<=460){
            setContentView(R.layout.main1080x2340);
//            Toast.makeText(getApplicationContext(),"main1080x2340",Toast.LENGTH_SHORT).show();
        }
        else if(height>1920 && densitydpi<=460){
            setContentView(R.layout.main1080x2280);
//            Toast.makeText(getApplicationContext(),"main1080x2280",Toast.LENGTH_SHORT).show();

        }
        else if(density<=2){
            setContentView(R.layout.main_density_2);
//            Toast.makeText(getApplicationContext(),"maindencity2",Toast.LENGTH_SHORT).show();

        }else if(densitydpi>=480 && height<=1920) {
            setContentView(R.layout.main_density_2);
//            Toast.makeText(getApplicationContext(),"densitydpi>=480 && height<=1920",Toast.LENGTH_SHORT).show();
        }
        else if(height>2280 && densitydpi>=480) {
            setContentView(R.layout.main1080x2340_density_480);
//            Toast.makeText(getApplicationContext(),"height>2280 && densitydpi>=480",Toast.LENGTH_SHORT).show();
        }
        else if(height>1920 && densitydpi>=480) {
            setContentView(R.layout.main1080x2280_density_480);
//            Toast.makeText(getApplicationContext(),"height>1920 && densitydpi>=480",Toast.LENGTH_SHORT).show();
        }
        else{
            setContentView(R.layout.main1080x1920);
//            Toast.makeText(getApplicationContext(),"main1080x1920",Toast.LENGTH_SHORT).show();
        }

        init();
        PerMissionMethod();
        ThreadMethods();
        CompassMethod();
        soundMethod();
        wakeUp();
        mainscreen_orientationLock();
        speedModelList=new ArrayList<>();
        vv.setVisibility(View.VISIBLE);
        final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter != null)
            bluetoothDefaultIsEnable = btAdapter.isEnabled();

        // get Orientation sensor
        final List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
        if (sensors.size() > 0)
            orientSensor = sensors.get(0);
        else
            showDialog(NO_ORIENTATION_SENSOR);

        // create a log instance for use by this application
        triplog = TripLog.getInstance(this.getApplicationContext());

        obdStatusTextView.setText(getString(R.string.status_obd_disconnected));

        SpeedQuestiomark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuestionMark("Vehical Speed","Speed is the distance traveled per unit of time.here unit is km/h.");
            }
        });

        RpmQuestioMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuestionMark("Vehical Rpm","In cars, rpm measures how many times the engine's crankshaft makes one full rotation every minute, and along with it, how many times each piston goes up and down in its cylinder.");
            }
        });

        TravelQuestionmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuestionMark("distance travelled","This is showing Data of odometer, odometer or odograph is an instrument used for measuring the distance travelled by a vehicle.");
            }
        });

        EngineRuntimeQuestionmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuestionMark("Engine Runtime","Duration of Engine Run Time");

            }
        });

        kmdifferQuestionmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuestionMark("Latest Distance","This is showing Difference between Your car total Km while You are Driving and car total Km before you start Driving");

            }
        });

        if (isMyServiceRunning(MockObdGatewayService.class) == false ) {

            fancyDialog();


        } else if(isMyServiceRunning(ObdGatewayService.class) == false) {

            fancyDialog();

        }

//################################################################################################

        TravelLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                KmListData kmListData;
                kmListData=new KmListData();
                vv.setVisibility(View.GONE);
                Framelauoutcontainer.setVisibility(View.VISIBLE);

                RoboActivity activity = MainActivity.this;
                if (!isFinishing() && !isDestroyed()) {

                    FragmentTransaction ft = activity.getFragmentManager()
                            .beginTransaction();
                    ft.replace(R.id.fragment_container, kmListData);
                    ft.commit();
                }

            }
        });

//        TodayDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(isMyServiceRunning(MockObdGatewayService.class) == true){
//                    viewData();
//                }else if(isMyServiceRunning(ObdGatewayService.class) == true){
//                    viewData();
//                }
//
//            }
//        });

//        LastRecord.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LastRecord();
//            }
//        });

//        SecondlastRecord.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SecondLastRecord();
//            }
//        });

        }

//#############################--init method for initalization of commponents--################################

    public void init() {
        btStatusTextView=(TextView)findViewById(R.id.BT_STATUS);
        rpm=(TextView)findViewById(R.id.rpm);
        rpmmain=(LinearLayout)findViewById(R.id.rpmmain);
        speed=(TextView)findViewById(R.id.speed);
        TotalKm=(TextView)findViewById(R.id.totalKm);
        obdStatusTextView=(TextView)findViewById(R.id.OBD_STATUS);
        gpsStatusTextView=(TextView)findViewById(R.id.GPS_POS);
        tl=(TableLayout)findViewById(R.id.data_table);
        vv=(LinearLayout)findViewById(R.id.vehicle_view);
        LinearLayout Speedlinear=(LinearLayout)findViewById(R.id.speedLinearId);
        SpeedButton=(Button)findViewById(R.id.speedbtid);
        TestButton=(Button)findViewById(R.id.testid);
        SpeedAndRpm=(Button)findViewById(R.id.snrid);
        TotalTravelDistance=(Button)findViewById(R.id.distanbtid);
        Home=(Button)findViewById(R.id.homebtid);
        compass = findViewById(R.id.compass_1);
        Framelauoutcontainer=(FrameLayout)findViewById(R.id.fragment_container);
        TravelLinear=(LinearLayout)findViewById(R.id.travelLinearid);
        SpeedQuestiomark=(ImageView)findViewById(R.id.speedquestionmark);
        RpmQuestioMark=(ImageView)findViewById(R.id.rpmquestionmark);
        TravelQuestionmark=(ImageView)findViewById(R.id.travelKmquestionmark);
        EngineRuntime=(TextView)findViewById(R.id.engineRunTimemain);
        KmDifference=(TextView)findViewById(R.id.killdiffermain);
        SaveKmChk=(TextView)findViewById(R.id.saveKmchk);
        myhelper=new SQliteHelperClass(MainActivity.this);
        TotalkmLinear=(LinearLayout)findViewById(R.id.tklid);
//        TodayDate=(TextView)findViewById(R.id.tdate);
//        LastRecord=(Button)findViewById(R.id.lastrecord);
        kmdifferQuestionmark=(ImageView)findViewById(R.id.kmdifferquestionmark);
        EngineRuntimeQuestionmark=(ImageView)findViewById(R.id.engineRuntimequestionmark);
//        SecondlastRecord=(Button)findViewById(R.id.Secondlastrecord);

        DifferKmTotal=new ArrayList<>();

    }

//####################################--Thread Methods for Updating Fragment Ui--##########################################

    public void ThreadMethods(){

    Sbundle = new Bundle();
    speedthread = new Thread() {
        @Override
        public void run() {
            try {
                while (!speedthread.isInterrupted()) {
                    Thread.sleep(30);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                                i++;
//                                Sbundle.putString("speed", String.valueOf(i) );
                            if(speedclick==true){
                                SpeedButton.performClick();

                            }

                        }
                    });
                }
            } catch (InterruptedException e) {
            }
        }
    };
    speedthread.start();


    Rbundle=new Bundle();
    rpmthread=new Thread(){
        @Override
        public void run() {
            try {
                while (!rpmthread.isInterrupted()) {
                    Thread.sleep(30);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                                j++;
//                                Rbundle.putString("rpm", String.valueOf(j) );
                            if(testclick==true){
                                TestButton.performClick();
                            }
                        }
                    });
                }
            } catch (InterruptedException e) {
            }
        }
    };
    rpmthread.start();

    TravelBundle=new Bundle();
    totalTravelThread=new Thread(){
        @Override
        public void run() {
            try {
                while (!totalTravelThread.isInterrupted()) {
                    Thread.sleep(1000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            t++;
//                            TravelBundle.putString("travelkm", String.valueOf(t) );
//                            TravelBundle.putString("enRun",String.valueOf(t) );
//                            TravelBundle.putString("engCooltemp",String.valueOf(t));
//                            TravelBundle.putString("engLoad",String.valueOf(t));

                            if(TravelClick==true){
                                TotalTravelDistance.performClick();

                            }
                        }
                    });
                }
            } catch (InterruptedException e) {
            }
        }
    };
    totalTravelThread.start();


    SrBundle=new Bundle();
    RsBundle=new Bundle();
    speedAndRpmThread=new Thread(){
        @Override
        public void run() {
            try {
                while (!speedAndRpmThread.isInterrupted()) {
                    Thread.sleep(30);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                                k++;
//                                r++;
//                                SrBundle.putString("speed2", String.valueOf(k) );
//                                RsBundle.putString("rpm2",String.valueOf(r));
//                                SrBundle.putBundle("r1",RsBundle);
                            if(SnRClick==true){
                                SpeedAndRpm.performClick();

                            }
                        }
                    });
                }
            } catch (InterruptedException e) {
            }
        }
    };
    speedAndRpmThread.start();


    AlertThread=new Thread(){
        @Override
        public void run() {
            try {
                while (!AlertThread.isInterrupted()) {
                    Thread.sleep(1000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                if(Integer.valueOf(speedModel.getValue())>=90){
                                    soundPlay();
                                    highspeedalert();

                                }
                            }catch (Exception e){

//                                       Toast.makeText(getApplicationContext(),"Exceptoion occurred ",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            } catch (InterruptedException e) {
            }
        }
    };

        TestingThread=new Thread(){
        @Override
        public void run() {
            try {
                while (!TestingThread.isInterrupted()) {
                    Thread.sleep(30000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    DatabaseStuff();
                                }
                            }, 5000);
                        }
                    });
                }
            } catch (InterruptedException e) {
            }
        }
    };
    TestingThread.start();


        UpdateDiffer=new Thread(){

            @Override
            public void run() {
                try {
                    while (!UpdateDiffer.isInterrupted()) {
                        Thread.sleep(5000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (isMyServiceRunning(MockObdGatewayService.class) == true )
                                    try {
                                        final Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                try {
                                                    DifferUpdate = String.valueOf(totalKmModel.getTotalkm() - dataIntArrayList.get(0));
                                                    KmDifference.setText(DifferUpdate);
                                                }catch (Exception e){
                                                    KmDifference.setText("Calculating");
                                                }

                                            }
                                        }, 1000);

                                    }catch (Exception e){

                                    }
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        UpdateDiffer.start();
}


//###############################--On click method for Button click condition--################################################
    public void onClick(View v) {

        switch (v.getId()) {
                case R.id.speedbtid:
                    mainscreen_orientationLock();
                    if (isMyServiceRunning(MockObdGatewayService.class) == true ) {
                        vv.setVisibility(View.GONE);
                        Framelauoutcontainer.setVisibility(View.VISIBLE);
                        homeclick=false;
                        testclick=false;
                        TravelClick=false;
                        speedclick=true;
                        SnRClick=false;
                        SpeedFragment speedFragment;
                        speedFragment = new SpeedFragment();
                        speedFragment.setArguments(Sbundle);

                        RoboActivity activity = MainActivity.this;
                        if (!isFinishing() && !isDestroyed()) {

                            FragmentTransaction ft = activity.getFragmentManager()
                                    .beginTransaction();
                            ft.replace(R.id.fragment_container, speedFragment);
                            ft.commit();
                        }

                    }
                    else if(isMyServiceRunning(ObdGatewayService.class) == true){
                        vv.setVisibility(View.GONE);
                        Framelauoutcontainer.setVisibility(View.VISIBLE);
                        testclick=false;
                        homeclick=false;
                        TravelClick=false;
                        speedclick=true;
                        SnRClick=false;
                        SpeedFragment speedFragment;
                        speedFragment = new SpeedFragment();
                        speedFragment.setArguments(Sbundle);
                        RoboActivity activity = MainActivity.this;
                        if (!isFinishing() && !isDestroyed()) {
                            FragmentTransaction ft = activity.getFragmentManager()
                                    .beginTransaction();
                            ft.replace(R.id.fragment_container, speedFragment);
                            ft.commit();
                        }
                    }
                    else{
                        fancyDialog();
                    }

                break;

            case R.id.testid:
                mainscreen_orientationLock();

                if (isMyServiceRunning(MockObdGatewayService.class) == true) {

                    vv.setVisibility(View.GONE);
                    Framelauoutcontainer.setVisibility(View.VISIBLE);
                    speedclick=false;
                    TravelClick=false;
                    homeclick=false;
                    SnRClick=false;
                    testclick=true;

                    RpmsFragment rpmsFragment;
                    rpmsFragment = new RpmsFragment();
                    rpmsFragment.setArguments(Rbundle);

                    RoboActivity testactivity = MainActivity.this;
                    if (!isFinishing() && !isDestroyed()) {

                        FragmentTransaction ft = testactivity.getFragmentManager()
                                .beginTransaction();
                        ft.replace(R.id.fragment_container, rpmsFragment);
                        ft.commit();
                    }
                }

                else if(isMyServiceRunning(ObdGatewayService.class) == true){

                    vv.setVisibility(View.GONE);
                    Framelauoutcontainer.setVisibility(View.VISIBLE);
                    speedclick=false;
                    homeclick=false;
                    TravelClick=false;
                    SnRClick=false;
                    testclick=true;

                    RpmsFragment rpmsFragment;
                    rpmsFragment = new RpmsFragment();
                    rpmsFragment.setArguments(Rbundle);

                    RoboActivity testactivity = MainActivity.this;
                    if (!isFinishing() && !isDestroyed()) {

                        FragmentTransaction ft = testactivity.getFragmentManager()
                                .beginTransaction();
                        ft.replace(R.id.fragment_container, rpmsFragment);
                        ft.commit();
                    }
                }
                else{
                    fancyDialog();
                }

                break;

            case R.id.snrid:

                mainscreen_orientationLock();

                if (isMyServiceRunning(MockObdGatewayService.class) == true) {

                    vv.setVisibility(View.GONE);
                    Framelauoutcontainer.setVisibility(View.VISIBLE);
                    speedclick=false;
                    homeclick=false;
                    TravelClick=false;
                    SnRClick=true;
                    testclick=false;

                    SpeedAndRpm speedAndRpm;
                    speedAndRpm = new SpeedAndRpm();
                    speedAndRpm.setArguments(SrBundle);
                    SrBundle.putBundle("r1",RsBundle);

                    RoboActivity speeRactivity = MainActivity.this;
                    if (!isFinishing() && !isDestroyed()) {

                        FragmentTransaction ft = speeRactivity.getFragmentManager()
                                .beginTransaction();
                        ft.replace(R.id.fragment_container, speedAndRpm);
                        ft.commit();
                    }
                }

                else if(isMyServiceRunning(ObdGatewayService.class) == true){

                    vv.setVisibility(View.GONE);
                    Framelauoutcontainer.setVisibility(View.VISIBLE);
                    speedclick=false;
                    homeclick=false;
                    TravelClick=false;
                    SnRClick=true;
                    testclick=false;

                    SpeedAndRpm speedAndRpm;
                    speedAndRpm = new SpeedAndRpm();
                    speedAndRpm.setArguments(SrBundle);
                    SrBundle.putBundle("r1",RsBundle);

                    RoboActivity testactivity = MainActivity.this;
                    if (!isFinishing() && !isDestroyed()) {
                        FragmentTransaction ft = testactivity.getFragmentManager()
                                .beginTransaction();
                        ft.replace(R.id.fragment_container, speedAndRpm);
                        ft.commit();
                    }
                }

                else{
                    fancyDialog();
                }

                break;

            case R.id.distanbtid:
                mainscreen_orientationLock();

                if (isMyServiceRunning(MockObdGatewayService.class) == true ) {

                    vv.setVisibility(View.GONE);
                    speedclick=false;
                    homeclick=false;
                    testclick=false;
                    SnRClick=false;
                    TravelClick=true;
                    Framelauoutcontainer.setVisibility(View.VISIBLE);

                    TotalTravelFrag totalTravelFrag;
                    totalTravelFrag =new TotalTravelFrag();
                    totalTravelFrag.setArguments(TravelBundle);

                    RoboActivity activity3 = MainActivity.this;

                    if (!isFinishing() && !isDestroyed()) {

                        FragmentTransaction ft2 = activity3.getFragmentManager()
                                .beginTransaction();
                        ft2.replace(R.id.fragment_container, totalTravelFrag);
                        ft2.commit();
                    }

                }
                 else if(isMyServiceRunning(ObdGatewayService.class) == true){

                    vv.setVisibility(View.GONE);
                    speedclick=false;
                    homeclick=false;
                    testclick=false;
                    SnRClick=false;
                    TravelClick=true;
                    Framelauoutcontainer.setVisibility(View.VISIBLE);

                    TotalTravelFrag totalTravelFrag;
                    totalTravelFrag =new TotalTravelFrag();
                    totalTravelFrag.setArguments(TravelBundle);


                    RoboActivity activity3 = MainActivity.this;

                    if (!isFinishing() && !isDestroyed()) {

                        FragmentTransaction ft2 = activity3.getFragmentManager()
                                .beginTransaction();
                        ft2.replace(R.id.fragment_container, totalTravelFrag);
                        ft2.commit();
                    }

                    }
                else{
                    fancyDialog();

                }

                break;

            case R.id.homebtid:

                homeclick=true;
                speedclick=false;
                testclick=false;
                SnRClick=false;
                TravelClick=false;

                vv.setVisibility(View.VISIBLE);
                Framelauoutcontainer.setVisibility(View.GONE);

                mainscreen_orientationLock();

                break;
        }
    }


//######################################--use of SensorEventListener interface for get Compass Direction--#################
    private final SensorEventListener orientListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            String dir = "";
            if (x >= 337.5 || x < 22.5) {
                dir = "N";
            } else if (x >= 22.5 && x < 67.5) {
                dir = "NE";
            } else if (x >= 67.5 && x < 112.5) {
                dir = "E";
            } else if (x >= 112.5 && x < 157.5) {
                dir = "SE";
            } else if (x >= 157.5 && x < 202.5) {
                dir = "S";
            } else if (x >= 202.5 && x < 247.5) {
                dir = "SW";
            } else if (x >= 247.5 && x < 292.5) {
                dir = "W";
            } else if (x >= 292.5 && x < 337.5) {
                dir = "NW";
            }
//            updateTextView(compass, dir);
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // do nothing
        }
    };

//##################--Runnable interface for background Service--###################################
    /*note that here Runnable is used instaed of Thread because Runnable separates code that needs
    to run asynchronously means it's provid more flexiblity and also it's require less memory than
    Thread*/

    private final Runnable mQueueCommands = new Runnable() {
        public void run() {
            if (service != null && service.isRunning() && service.queueEmpty()) {
                queueCommands();

                double lat = 0;
                double lon = 0;
                double alt = 0;
                final int posLen = 7;
                if (mGpsIsStarted && mLastLocation != null) {
                    lat = mLastLocation.getLatitude();
                    lon = mLastLocation.getLongitude();
                    alt = mLastLocation.getAltitude();

                    StringBuilder sb = new StringBuilder();
                    sb.append("Lat: ");
                    sb.append(String.valueOf(mLastLocation.getLatitude()).substring(0, posLen));
                    sb.append(" Lon: ");
                    sb.append(String.valueOf(mLastLocation.getLongitude()).substring(0, posLen));
                    sb.append(" Alt: ");
                    sb.append(String.valueOf(mLastLocation.getAltitude()));
                    gpsStatusTextView.setText(sb.toString());
                }
                if (prefs.getBoolean(ConfigActivity.UPLOAD_DATA_KEY, false)) {
                    // Upload the current reading by http
                    final String vin = prefs.getString(ConfigActivity.VEHICLE_ID_KEY, "UNDEFINED_VIN");
                    Map<String, String> temp = new HashMap<String, String>();
                    temp.putAll(commandResult);
                    ObdReading reading = new ObdReading(lat, lon, alt, System.currentTimeMillis(), vin, temp);
                    new UploadAsyncTask().execute(reading);

                } else if (prefs.getBoolean(ConfigActivity.ENABLE_FULL_LOGGING_KEY, false)) {
                    // Write the current reading to CSV
                    final String vin = prefs.getString(ConfigActivity.VEHICLE_ID_KEY, "UNDEFINED_VIN");
                    Map<String, String> temp = new HashMap<String, String>();
                    temp.putAll(commandResult);
                    ObdReading reading = new ObdReading(lat, lon, alt, System.currentTimeMillis(), vin, temp);
                    if (reading != null) myCSVWriter.writeLineCSV(reading);
                }
                commandResult.clear();
            }
            // run again in period defined in preferences
            new Handler().postDelayed(mQueueCommands, 50);
        }
    };

    private final Runnable mQueueCommands1 = new Runnable() {
        public void run() {
            if (service != null && service.isRunning() && service.queueEmpty()) {
                queueCommands1();

                double lat = 0;
                double lon = 0;
                double alt = 0;
                final int posLen = 7;
                if (mGpsIsStarted && mLastLocation != null) {
                    lat = mLastLocation.getLatitude();
                    lon = mLastLocation.getLongitude();
                    alt = mLastLocation.getAltitude();

                    StringBuilder sb = new StringBuilder();
                    sb.append("Lat: ");
                    sb.append(String.valueOf(mLastLocation.getLatitude()).substring(0, posLen));
                    sb.append(" Lon: ");
                    sb.append(String.valueOf(mLastLocation.getLongitude()).substring(0, posLen));
                    sb.append(" Alt: ");
                    sb.append(String.valueOf(mLastLocation.getAltitude()));
                    gpsStatusTextView.setText(sb.toString());
                }
                if (prefs.getBoolean(ConfigActivity.UPLOAD_DATA_KEY, false)) {
                    // Upload the current reading by http
                    final String vin = prefs.getString(ConfigActivity.VEHICLE_ID_KEY, "UNDEFINED_VIN");
                    Map<String, String> temp = new HashMap<String, String>();
                    temp.putAll(commandResult);
                    ObdReading reading = new ObdReading(lat, lon, alt, System.currentTimeMillis(), vin, temp);
                    new UploadAsyncTask().execute(reading);

                } else if (prefs.getBoolean(ConfigActivity.ENABLE_FULL_LOGGING_KEY, false)) {
                    // Write the current reading to CSV
                    final String vin = prefs.getString(ConfigActivity.VEHICLE_ID_KEY, "UNDEFINED_VIN");
                    Map<String, String> temp = new HashMap<String, String>();
                    temp.putAll(commandResult);
                    ObdReading reading = new ObdReading(lat, lon, alt, System.currentTimeMillis(), vin, temp);
                    if (reading != null) myCSVWriter.writeLineCSV(reading);
                }
                commandResult.clear();
            }
            // run again in period defined in preferences
            new Handler().postDelayed(mQueueCommands1, 1000);
        }
    };


//######################ServiceConnection interface for monitor state of Service--#########################################

    private ServiceConnection serviceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder binder) {
            Log.d(TAG, className.toString() + " service is bound");
            isServiceBound = true;
            service = ((AbstractGatewayService.AbstractGatewayServiceBinder) binder).getService();
            service.setContext(MainActivity.this);
            Log.d(TAG, "Starting live data");
            try {
                service.startService();
                if (preRequisites)
                    btStatusTextView.setText(getString(R.string.status_bluetooth_connected));
            } catch (IOException ioe) {
                Log.e(TAG, "Failure Starting live data");
                btStatusTextView.setText(getString(R.string.status_bluetooth_error_connecting));
                doUnbindService();
            }
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        // This method is *only* called when the connection to the service is lost unexpectedly
        // and *not* when the client unbinds (http://developer.android.com/guide/components/bound-services.html)
        // So the isServiceBound attribute should also be set to false when we unbind from the service.

        @Override
        public void onServiceDisconnected(ComponentName className) {
            Log.d(TAG, className.toString() + " service is unbound");
            isServiceBound = false;
        }
    };

//#########################--LookUpCommand for get Names value from AvailableCommandNames--###############################
    public static String LookUpCommand(String txt) {
        for (AvailableCommandNames item : AvailableCommandNames.values()) {
            if (item.getValue().equals(txt)) return item.name();
        }
        return txt;
    }


//###########################--Core method responsible for get and update Data in table layout--#################################
    public void stateUpdate(final ObdCommandJob job) {
        final String cmdName = job.getCommand().getName();
        String cmdResult = "";
        final String cmdID = LookUpCommand(cmdName);

        if (job.getState().equals(ObdCommandJob.ObdCommandJobState.EXECUTION_ERROR)) {
            cmdResult = job.getCommand().getResult();
            if (cmdResult != null && isServiceBound) {
                obdStatusTextView.setText(cmdResult.toLowerCase());
            }
        } else if (job.getState().equals(ObdCommandJob.ObdCommandJobState.BROKEN_PIPE)) {
            if (isServiceBound)
                stopLiveData();
        } else if (job.getState().equals(ObdCommandJob.ObdCommandJobState.NOT_SUPPORTED)) {
            cmdResult = getString(R.string.status_obd_no_support);
        } else {
            cmdResult = job.getCommand().getFormattedResult();
            if (isServiceBound)
                obdStatusTextView.setText(getString(R.string.status_obd_data));
        }

        if (vv.findViewWithTag(cmdID) != null) {
            TextView existingTV = (TextView) vv.findViewWithTag(cmdID);
            existingTV.setText(cmdResult);
        } else addTableRow(cmdID, cmdName, cmdResult);
        commandResult.put(cmdID, cmdResult);
        updateTripStatistic(job, cmdID);
    }

//##########################################--gps method for location--####################################################

        private boolean gpsInit() {
            mLocService = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (mLocService != null) {
                        // mLocProvider = mLocService.getProvider(LocationManager.GPS_PROVIDER);
                if (mLocProvider != null) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        // ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        // public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        // int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return true;
                    }
                    mLocService.addGpsStatusListener(this);
                    if (mLocService.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        gpsStatusTextView.setText(getString(R.string.status_gps_ready));
                        return true;
                    }
                }
            }
            gpsStatusTextView.setText(getString(R.string.status_gps_no_support));
//            showDialog(NO_GPS_SUPPORT);
            Log.e(TAG, "Unable to get GPS PROVIDER");
                        // todo disable gps controls into Preferences
            return false;
        }
//###################--on of the Core method responsible for get and update Separate data of obd--########################
    private void updateTripStatistic(final ObdCommandJob job, final String cmdID) {

        if (currentTrip != null) {
            if (cmdID.equals(AvailableCommandNames.SPEED.toString())) {
                SpeedCommand command = (SpeedCommand) job.getCommand();
                currentTrip.setSpeedMax(command.getMetricSpeed());
                speed.setText(String.valueOf(command.getMetricSpeed()));

                Sbundle = new Bundle();
                SrBundle=new Bundle();
                String mySpeed=String.valueOf(command.getMetricSpeed());

                Sbundle.putString("speed",mySpeed);
                SrBundle.putString("speed2",mySpeed);

                speedModel=new SpeedModel(command.getMetricSpeed());

//              Toast.makeText(getApplicationContext(),speedModel.getId()+speedModel.getValue(),Toast.LENGTH_SHORT).show();

            } else if (cmdID.equals(AvailableCommandNames.ENGINE_RPM.toString())) {
                RPMCommand commandrpm = (RPMCommand) job.getCommand();
                currentTrip.setEngineRpmMax(commandrpm.getRPM());
                rpm.setText(String.valueOf(commandrpm.getRPM()));

                Rbundle= new Bundle();
                RsBundle=new Bundle();
                String myRpm = String.valueOf(commandrpm.getRPM());

                Rbundle.putString("rpm", myRpm );
                RsBundle.putString("rpm2",myRpm);

//               Toast.makeText(getApplicationContext(), AvailableCommandNames.ENGINE_RPM.toString() + " : " + String.valueOf(commandrpm.getRPM()), Toast.LENGTH_SHORT).show();
            }

            else if (cmdID.equals(AvailableCommandNames.DISTANCE_TRAVELED_AFTER_CODES_CLEARED.toString()))
            {
                DistanceSinceCCCommand distanceSinceCCCommand=(DistanceSinceCCCommand)job.getCommand();
                TotalKm.setText(String.valueOf(distanceSinceCCCommand.getKm()));

                TravelBundle=new Bundle();
                String myTravelKm=String.valueOf(distanceSinceCCCommand.getKm());
                TravelBundle.putString("travelkm",myTravelKm);

                Integer myINtravellkm=distanceSinceCCCommand.getKm();

                totalKmModel=new TotalKmModel(myINtravellkm);

            }

            else if (cmdID.equals(AvailableCommandNames.ENGINE_RUNTIME.toString()))
            {
                RuntimeCommand runtimeCommand=(RuntimeCommand)job.getCommand();
                String myEngineRuntim=String.valueOf(runtimeCommand.getFormattedResult());
                EngineRuntime.setText(myEngineRuntim);
                TravelBundle.putString("enRun",myEngineRuntim);
            }

            else if (cmdID.equals(AvailableCommandNames.ENGINE_COOLANT_TEMP.toString()))
            {
                EngineCoolantTemperatureCommand engineCoolantTemperatureCommand=(EngineCoolantTemperatureCommand)job.getCommand();
                String myEngineCoolent=String.valueOf(engineCoolantTemperatureCommand.getFormattedResult());
                TravelBundle.putString("engCooltemp",myEngineCoolent);
            }

            else if (cmdID.equals(AvailableCommandNames.ENGINE_LOAD.toString()))
            {
                LoadCommand loadCommand=(LoadCommand)job.getCommand();
                String myEngineLoad=String.valueOf(loadCommand.getPercentage());
                TravelBundle.putString("engLoad",myEngineLoad);

            }

        }
    }

//#################--onStart method here configue just for Log purpose--#####################################################
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "Entered onStart...");
    }

//######################--onDestroy method here configure for stop Service--###################################################
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mLocService != null) {
            mLocService.removeGpsStatusListener(this);
            mLocService.removeUpdates(this);
        }

//        releaseWakeLockIfHeld();
        if (isServiceBound) {
            doUnbindService();
        }

        endTrip();

        final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter != null && btAdapter.isEnabled() && !bluetoothDefaultIsEnable)
            btAdapter.disable();
    }

//#####################################--onPause method--######################################################################
    @Override
    protected void onPause() {
        super.onPause();
        Log.e("Checking checking", "Pausing..");
//        releaseWakeLockIfHeld();

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);
        //**************--Geting Current Time--*********************
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
        String formattedTime = dateFormat.format(new Date()).toString();

        try {
            if (String.valueOf(dataArrayModelList.get(0)).contains(formattedDate) && String.valueOf(dataArrayModelList.get(1)).contains(formattedTime)) {
                Toast.makeText(getApplicationContext(), "Data Of this Date and time is already saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "new  Date or Time", Toast.LENGTH_SHORT).show();
                AddData();

            }

        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"onPause is ocuure because Asking for Permmision",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        Home.performClick();
    }

//#######################################--onResume Method--###############################################################
    @SuppressLint("InvalidWakeLockTag")
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "Resuming..");
//        sensorManager.registerListener(orientListener, orientSensor,
//                SensorManager.SENSOR_DELAY_UI);
//        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
//                "ObdReader");

        // get Bluetooth device
        final BluetoothAdapter btAdapter = BluetoothAdapter
                .getDefaultAdapter();

        preRequisites = btAdapter != null && btAdapter.isEnabled();
        if (!preRequisites && prefs.getBoolean(ConfigActivity.ENABLE_BT_KEY, false)) {
            preRequisites = btAdapter != null && btAdapter.enable();
        }

        gpsInit();

        if (!preRequisites) {
            showDialog(BLUETOOTH_DISABLED);
            btStatusTextView.setText(getString(R.string.status_bluetooth_disabled));
        } else {
            btStatusTextView.setText(getString(R.string.status_bluetooth_ok));
        }
    }

    private void updateConfig() {
        startActivity(new Intent(this, ConfigActivity.class));
    }

//####################################--menu item initalization--#############################################################

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, START_LIVE_DATA, 0, getString(R.string.menu_start_live_data));
        menu.add(0, STOP_LIVE_DATA, 0, getString(R.string.menu_stop_live_data));
        menu.add(0, GET_DTC, 0, getString(R.string.menu_get_dtc));
        menu.add(0, TRIPS_LIST, 0, getString(R.string.menu_trip_list));
        menu.add(0, SETTINGS, 0, getString(R.string.menu_settings));
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case START_LIVE_DATA:
                Home.performClick();
                startLiveData();
                return true;
            case STOP_LIVE_DATA:
                Home.performClick();
                stopLiveData();
                return true;
            case SETTINGS:
                updateConfig();
                return true;
            case GET_DTC:
                getTroubleCodes();
                return true;
            case TRIPS_LIST:
                startActivity(new Intent(this, TripListActivity.class));
                return true;
        }
        return false;
    }

    private void getTroubleCodes() {
        startActivity(new Intent(this, TroubleCodesActivity.class));
    }

//##############################--StartLiveData method--#######################################################
    public void startLiveData() {

        if(alertThreadChecker==false) {
            try{
                AlertThread.start();
            }catch (Exception e){
//  Toast.makeText(getApplicationContext(),"Alert Thresd is alred started",Toast.LENGTH_SHORT).show();
            }

        }
//        Home.performClick();

        Log.d(TAG, "Starting live data..");

        tl.removeAllViews(); //start fresh
        doBindService();

        currentTrip = triplog.startTrip();
        if (currentTrip == null)
            showDialog(SAVE_TRIP_NOT_AVAILABLE);

        // start command execution
        new Handler().post(mQueueCommands);
        new Handler().post(mQueueCommands1);

        if (prefs.getBoolean(ConfigActivity.ENABLE_GPS_KEY, false))
            gpsStart();
        else
            gpsStatusTextView.setText(getString(R.string.status_gps_not_used));

        // screen won't turn off until wakeLock.release()
//        wakeLock.acquire();

        if (prefs.getBoolean(ConfigActivity.ENABLE_FULL_LOGGING_KEY, false)) {

            // Create the CSV Logger
            long mils = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("_dd_MM_yyyy_HH_mm_ss");

            try {
                myCSVWriter = new LogCSVWriter("Log" + sdf.format(new Date(mils)).toString() + ".csv",
                        prefs.getString(ConfigActivity.DIRECTORY_FULL_LOGGING_KEY,
                                getString(R.string.default_dirname_full_logging))
                );
            } catch (FileNotFoundException | RuntimeException e) {
                Log.e(TAG, "Can't enable logging to file.", e);
            }
        }


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DatabaseStuff();
            }
        }, 5000);

    }

//########################--stopLiveData method--##########################################################
    public void stopLiveData() {

        alertThreadChecker=true;
        Log.d(TAG, "Stopping live data..");

        DatabaseStuff();
        gpsStop();

        doUnbindService();
        endTrip();

//        releaseWakeLockIfHeld();

        final String devemail = prefs.getString(ConfigActivity.DEV_EMAIL_KEY, null);
        if (devemail != null && !devemail.isEmpty()) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            ObdGatewayService.saveLogcatToFile(getApplicationContext(), devemail);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            //No button clicked
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Where there issues?\nThen please send us the logs.\nSend Logs?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }

        if (myCSVWriter != null) {
            myCSVWriter.closeLogCSVWriter();
        }
    }

    protected void endTrip() {
        if (currentTrip != null) {
            currentTrip.setEndDate(new Date());
            triplog.updateRecord(currentTrip);
        }
    }

    protected Dialog onCreateDialog(int id) {
        AlertDialog.Builder build = new AlertDialog.Builder(this);
//        switch (id) {
//            case NO_BLUETOOTH_ID:
//                build.setMessage(getString(R.string.text_no_bluetooth_id));
//                return build.create();
//            case BLUETOOTH_DISABLED:
//                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//                return build.create();
//            case NO_ORIENTATION_SENSOR:
//                build.setMessage(getString(R.string.text_no_orientation_sensor));
//                return build.create();
//            case NO_GPS_SUPPORT:
//                build.setMessage(getString(R.string.text_no_gps_support));
//                return build.create();
//            case SAVE_TRIP_NOT_AVAILABLE:
//                build.setMessage(getString(R.string.text_save_trip_not_available));
//                return build.create();
//        }
        return null;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem startItem = menu.findItem(START_LIVE_DATA);
        MenuItem stopItem = menu.findItem(STOP_LIVE_DATA);
        MenuItem settingsItem = menu.findItem(SETTINGS);
        MenuItem getDTCItem = menu.findItem(GET_DTC);

        if (service != null && service.isRunning()) {
            getDTCItem.setEnabled(false);
            startItem.setEnabled(false);
            stopItem.setEnabled(true);
            settingsItem.setEnabled(false);
        } else {
            getDTCItem.setEnabled(true);
            stopItem.setEnabled(false);
            startItem.setEnabled(true);
            settingsItem.setEnabled(true);
        }
        return true;
    }


//#######################--method for Adding data into Table--###############################################
    private void addTableRow(String id, String key, String val) {

        TableRow tr = new TableRow(this);
        MarginLayoutParams params = new MarginLayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(TABLE_ROW_MARGIN, TABLE_ROW_MARGIN, TABLE_ROW_MARGIN,
                TABLE_ROW_MARGIN);
        tr.setLayoutParams(params);

        TextView name = new TextView(this);
        name.setGravity(Gravity.RIGHT);
        name.setText(key + ": ");
        TextView value = new TextView(this);
        value.setGravity(Gravity.LEFT);
        value.setText(val);
        value.setTag(id);
        tr.addView(name);
        tr.addView(value);
        tl.addView(tr, params);
    }

//##################--queueCommands method for geting command object from ObdConfig class--#########################

    private void queueCommands() {
        if (isServiceBound) {
            for (ObdCommand Command : ObdConfig.getCommands()) {
                if (prefs.getBoolean(Command.getName(), true))
                    service.queueJob(new ObdCommandJob(Command));
            }
        }
    }

    private void queueCommands1() {
        if (isServiceBound) {
            for (ObdCommand Command : ObdConfig2.getCommands()) {
                if (prefs.getBoolean(Command.getName(), true))
                    service.queueJob(new ObdCommandJob(Command));
            }
        }
    }

//############################--Binding Sevice and unbinding methods--############################################

    private void doBindService() {
        if (!isServiceBound) {
            Log.d(TAG, "Binding OBD service..");
            if (preRequisites) {
                btStatusTextView.setText(getString(R.string.status_bluetooth_connecting));
                Intent serviceIntent = new Intent(this, ObdGatewayService.class);
                bindService(serviceIntent, serviceConn, Context.BIND_AUTO_CREATE);
            } else {
                btStatusTextView.setText(getString(R.string.status_bluetooth_disabled));
                Intent serviceIntent = new Intent(this, MockObdGatewayService.class);
                bindService(serviceIntent, serviceConn, Context.BIND_AUTO_CREATE);
            }
        }
    }

    private void doUnbindService() {
        if (isServiceBound) {
            if (service.isRunning()) {
                service.stopService();
                if (preRequisites)
                    btStatusTextView.setText(getString(R.string.status_bluetooth_ok));
            }
            Log.d(TAG, "Unbinding OBD service..");
            unbindService(serviceConn);
            isServiceBound = false;
            obdStatusTextView.setText(getString(R.string.status_obd_disconnected));
        }
    }

    public void onLocationChanged(Location location) {
        mLastLocation = location;
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    public void onProviderEnabled(String provider) {
    }

    public void onProviderDisabled(String provider) {
    }

    public void onGpsStatusChanged(int event) {

        switch (event) {
            case GpsStatus.GPS_EVENT_STARTED:
                gpsStatusTextView.setText(getString(R.string.status_gps_started));
                break;
            case GpsStatus.GPS_EVENT_STOPPED:
                gpsStatusTextView.setText(getString(R.string.status_gps_stopped));
                break;
            case GpsStatus.GPS_EVENT_FIRST_FIX:
                gpsStatusTextView.setText(getString(R.string.status_gps_fix));
                break;
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                break;
        }
    }

//##################################--method for clearify RequestCode--#######################################
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                btStatusTextView.setText(getString(R.string.status_bluetooth_connected));
            } else {
                Toast.makeText(this, R.string.text_bluetooth_disabled, Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

//###############################--Gps Related method--#######################################################

    private synchronized void gpsStart() {
        if (!mGpsIsStarted && mLocProvider != null && mLocService != null && mLocService.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


                return;
            }
            mLocService.requestLocationUpdates(mLocProvider.getName(), getGpsUpdatePeriod(prefs), getGpsDistanceUpdatePeriod(prefs), this);
            mGpsIsStarted = true;
        } else {
            gpsStatusTextView.setText(getString(R.string.status_gps_no_support));
        }
    }

    private synchronized void gpsStop() {
        if (mGpsIsStarted) {
            mLocService.removeUpdates(this);
            mGpsIsStarted = false;
            gpsStatusTextView.setText(getString(R.string.status_gps_stopped));
        }
    }

    /**
     * Uploading asynchronous task
     */
    private class UploadAsyncTask extends AsyncTask<ObdReading, Void, Void> {

        @Override
        protected Void doInBackground(ObdReading... readings) {
            Log.d(TAG, "Uploading " + readings.length + " readings..");
            // instantiate reading service client
            final String endpoint = prefs.getString(ConfigActivity.UPLOAD_URL_KEY, "");
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(endpoint)
                    .build();
            ObdService service = restAdapter.create(ObdService.class);
            // upload readings
            for (ObdReading reading : readings) {
                Log.e("ObdService==",reading.toString());


                try {
                    Response response = service.uploadReading(reading);

                    assert response.getStatus() == 200;
                } catch (RetrofitError re) {
                    Log.e(TAG, re.toString());
                }

            }
            Log.d(TAG, "Done");
            return null;
        }

    }
//#####################################checking Service is running or not################################

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

//###############################--Alert Dialog--########################################################

    public void fancyDialog(){

        new FancyAlertDialog.Builder(this)
                .setTitle("Start Service")
                .setBackgroundColor(Color.parseColor("#cc0000"))  //Don't pass R.color.colorvalue
                .setMessage("Select StartLiveData From Menu Items")
                .setNegativeBtnText("Cancel")
                .setPositiveBtnBackground(Color.parseColor("#00cc00"))  //Don't pass R.color.colorvalue
                .setPositiveBtnText("Service")
                .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))  //Don't pass R.color.colorvalue
                .setAnimation(Animation.SIDE)
                .isCancellable(true)
                .setIcon(R.drawable.ic_setting, Icon.Visible)
                .OnPositiveClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        startLiveData();

//                        Toast.makeText(getApplicationContext(),"OK",Toast.LENGTH_SHORT).show();
                    }
                })
                .OnNegativeClicked(new FancyAlertDialogListener() {
                    @Override
                    public void OnClick() {
                        QuestionMark("Service Required","Please start Service Manually or Select service option of notification");                    }
                })
                .build();

    }

//####################################--Alert Notification--#############################################
    public void highspeedalert(){
        Sneaker.with(MainActivity.this) // Activity, Fragment or ViewGroup
                .setTitle("Speed Limit")
                .setHeight(250)
                .setMessage("Plese for Your safety Drive slowly ")
                .sneakError();
    }

    public void mediumspeedwarning(){
        Sneaker.with(MainActivity.this) // Activity, Fragment or ViewGroup
                .setTitle("Modelated ")
                .setHeight(150)
                .setMessage("This is the warning message")
                .sneakWarning();
    }
//###############################--Music Related--######################################################

    public void soundMethod(){
        int maxSimultaneousStreams = 1;
        mSoundManager = new SoundManager(this, maxSimultaneousStreams);
        mSoundManager.start();
        mSoundManager.load(R.raw.buzzer);
    }

    public void soundPlay(){
        mSoundManager.play(R.raw.buzzer);
    }
//#########################--method check and change orientation--#######################################

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        checkOrientation(newConfig);
//
//    }
//
//    private void checkOrientation(Configuration newConfig) {
//        // Checks the orientation of the screen
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            setContentView(R.layout.main_landscap);
//        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            setContentView(R.layout.main1080x2280);
//        }
//    }

//###########################--Lock Orientation Base on Fragment visibility--###################################
    public void mainscreen_orientationLock(){
       if(vv.getVisibility()== View.VISIBLE){
           setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//           Toast.makeText(getApplicationContext(),"SCREEN_ORIENTATION_PORTRAIT",Toast.LENGTH_SHORT).show();
        }else if(vv.getVisibility()== View.GONE) {
           setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
//           Toast.makeText(getApplicationContext(),"SCREEN_ORIENTATION_UNSPECIFIED",Toast.LENGTH_SHORT).show();

       }

    }

//#################--Gif Dialog methods --#################################################################

    public void QuestionMark(String title,String info ){
        new TTFancyGifDialog.Builder(MainActivity.this)
                .setTitle(title)
                .setMessage(info)
                .setPositiveBtnText("Ok")
                .setPositiveBtnBackground("#2E7D32")
                .setGifResource(R.drawable.satiesfingcar)      //pass your gif, png or jpg
                .isCancellable(true)
                .build();

    }
//#####################################--Compass method--###################################################

    public void CompassMethod(){

        compass.setListener(new CompassListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        });
    }


//##################################--Permission method--##################################################

    public void PerMissionMethod(){


        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ,Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            }
        } else
            {
            // We have already permission to use the location
        }

    }

//################################--always wake up--####################################################

    public void wakeUp(){
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }



//########################--current Date Method--##########################################

    public String TodayDate(){

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);

        return formattedDate;
    }

//#####################--Current Time--###############################################

    public String CurrentTime(){

        SimpleDateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
        String formattedTime = dateFormat.format(new Date()).toString();

        return formattedTime;
    }

//###################--gettiing Display info--############################################

    public void DisplayInfo(){

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        density=displayMetrics.density;
        densitydpi=displayMetrics.densityDpi;
    }


//####################################--Database Related Stuff--#######################################

    //**********************--Save Total Km--****************
    public void AddData(){
        Integer Skm;

        try{

            Skm = totalKmModel.getTotalkm();

            Integer Dkm = Skm-dataIntArrayList.get(0);
            //****************--Getting Current Date--**********************
            Date c = Calendar.getInstance().getTime();
            System.out.println("Current time => " + c);

            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String formattedDate = df.format(c);
            //**************************************************************

            //**************--Geting Current Time--*********************
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
            String formattedTime = dateFormat.format(new Date()).toString();


            boolean indata = myhelper.insertData(new KmDataModel(Skm,Dkm,formattedDate,formattedTime));

            if (indata == true) {
                Log.d("AddData","Km Saved");
                Log.d("AddData","Differnce  Km Saved");
            }
            else{
                Log.d("AddData","In Data not True");
            }

            LastRecord();
            SecondLastRecord();

        }catch (Exception e){
            Log.e("AddData()","Exception in Add data Method");
            Skm = 0;
            //****************--Getting Current Date--**********************
            Date c = Calendar.getInstance().getTime();
            System.out.println("Current time => " + c);

            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String formattedDate = df.format(c);
            //**************************************************************

            //**************--Geting Current Time--*********************
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
            String formattedTime = dateFormat.format(new Date()).toString();

            boolean indata = myhelper.insertData(new KmDataModel(Skm,0,formattedDate,formattedTime));

            LastRecord();
            SecondLastRecord();
        }



    }

    //******************--Read Total Km--******************
    public void viewData(){

        Cursor cursor=myhelper.getAllData();
        if(cursor.getCount() ==0){
            QuestionMark("Error","Nothing found" );

            return;

        }
        StringBuffer stringBuffer= new StringBuffer();
        while (cursor.moveToNext()) {
            //            stringBuffer.append("Id :"+ cursor.getString(0)+"|| ");
            stringBuffer.append( cursor.getString(2)+"   ");
            stringBuffer.append(cursor.getString(3)+"   ");
            stringBuffer.append("Km :"+ cursor.getInt(1)+"\n");
            stringBuffer.append("DffKm :"+ cursor.getInt(4)+"\n");


            // Show all data
        }
        QuestionMark("Data",stringBuffer.toString());

    }


    //****************--Read Last Data--*****************
    public void LastRecord(){

        dataArrayModelList=new ArrayList<>();
        dataIntArrayList=new ArrayList<>();
        Cursor cursorl=myhelper.getLastData();

        if(cursorl.getCount() == 0){
//            AddData();
        }

        StringBuffer stringBuffer= new StringBuffer();

        cursorl=myhelper.getLastData();

        while (cursorl.moveToNext()) {

            stringBuffer.append("Id :"+ cursorl.getString(0)+"\n");
            stringBuffer.append("Km :"+ cursorl.getInt(1)+"\n");
            stringBuffer.append("Date :"+ cursorl.getString(2)+"\n");
            stringBuffer.append("Time :"+ cursorl.getString(3)+"\n");
            stringBuffer.append("DffKm :"+ cursorl.getString(4)+"\n");
            dataArrayModelList.add(cursorl.getString(2));
            dataArrayModelList.add(cursorl.getString(3));
            dataIntArrayList.add(cursorl.getInt(1));

            Log.e("LastRecord","Date: " +String.valueOf((cursorl.getString(2))));
            Log.e("LastRecord","Time: "+String.valueOf((cursorl.getString(3))));
            Log.e("LastRecord","Km: "+String.valueOf((cursorl.getInt(1))));


        }
//        QuestionMark("Last Record",stringBuffer.toString());


        Log.w("Lastrecored:size-", String.valueOf(dataArrayModelList.size()));

    }

    //*********************-Read Second Last Record--**********

    public void SecondLastRecord(){

        dataArray2ModelList=new ArrayList<>();
        dataInt2ArrayList=new ArrayList<>();
        Cursor cursorl=myhelper.getLastData();

        if(cursorl.getCount() == 0){

            Log.e("SecondLastRecord","Nothing found");
//            QuestionMark("Error","Nothing found" );

        }

        StringBuffer stringBuffer= new StringBuffer();

        cursorl=myhelper.getSecondLastData();

        while (cursorl.moveToNext()) {

            stringBuffer.append("Id :"+ cursorl.getString(0)+"\n");
            stringBuffer.append("Km :"+ cursorl.getInt(1)+"\n");
            stringBuffer.append("Date :"+ cursorl.getString(2)+"\n");
            stringBuffer.append("Time :"+ cursorl.getString(3)+"\n");
            stringBuffer.append("DffKm :"+ cursorl.getString(4)+"\n");
            dataArray2ModelList.add(cursorl.getString(2));
            dataArray2ModelList.add(cursorl.getString(3));
            dataInt2ArrayList.add(cursorl.getInt(1));

            Log.e("SecondLastRecord","km:"+cursorl.getInt(1));
            Log.e("SecondLastRecord","Date:"+cursorl.getString(2));
            Log.e("SecondLastRecord","Time:"+cursorl.getString(3));

        }

//        QuestionMark("Second last Record",stringBuffer.toString());
    }


//########################--DataBase Related Method--###################################


    public void DatabaseStuff(){


        if(isMyServiceRunning(MockObdGatewayService.class) == true){

            //########
            Date c = Calendar.getInstance().getTime();
            System.out.println("Current time => " + c);

            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String formattedDate = df.format(c);
            //########
            //**************--Geting Current Time--*********************
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
            String formattedTime = dateFormat.format(new Date()).toString();

            db=myhelper.getWritableDatabase();

            Cursor cursor=myhelper.getAllData();


            cursor.moveToFirst();
            int count= cursor.getCount();

            if(count >0) {
                LastRecord();
                SecondLastRecord();

                    if (String.valueOf(dataArrayModelList.get(0)).contains(formattedDate) && String.valueOf(dataArrayModelList.get(1)).contains(formattedTime)) {
                        Log.d("DataStuff","Data Of this Date and time is already saved");
                    } else {
                        Log.d("DataStuff","new  Date or Time");
                        Toast.makeText(getApplicationContext(), "new  Date or Time", Toast.LENGTH_SHORT).show();
                        AddData();
                    }

                }else{

                    Toast.makeText(getApplicationContext(), "First Data entry", Toast.LENGTH_SHORT).show();
                    AddData();

                }


        }else if(isMyServiceRunning(ObdGatewayService.class) == true) {


            //########
            Date c = Calendar.getInstance().getTime();
            System.out.println("Current time => " + c);

            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String formattedDate = df.format(c);
            //########
            //**************--Geting Current Time--*********************
            SimpleDateFormat dateFormat = new SimpleDateFormat("hh.mm aa");
            String formattedTime = dateFormat.format(new Date()).toString();

            db=myhelper.getWritableDatabase();

            Cursor cursor=myhelper.getAllData();


            cursor.moveToFirst();
            int count= cursor.getCount();

            if(count >0) {
                LastRecord();
                SecondLastRecord();

                if (String.valueOf(dataArrayModelList.get(0)).contains(formattedDate) && String.valueOf(dataArrayModelList.get(1)).contains(formattedTime)) {
                        Log.d("DataStuff","Data Of this Date and time is already saved");
//                    Toast.makeText(getApplicationContext(), "Data Of this Date and time is already saved", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("DataStuff","new  Date or Time");
                    Toast.makeText(getApplicationContext(), "new  Date or Time", Toast.LENGTH_SHORT).show();
                    AddData();
                }

            }else{
                Log.d("DataStuff","First Data entry");
                Toast.makeText(getApplicationContext(), "First Data entry", Toast.LENGTH_SHORT).show();
                AddData();

            }

        }


        }

}

