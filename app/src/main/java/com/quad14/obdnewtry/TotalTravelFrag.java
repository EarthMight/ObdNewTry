package com.quad14.obdnewtry;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DigitalClock;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bestsoft32.tt_fancy_gif_dialog_lib.TTFancyGifDialog;
import com.github.capur16.digitspeedviewlib.DigitSpeedView;
import com.quad14.obdnewtry.activity.MainActivity;
import com.quad14.obdnewtry.io.MockObdGatewayService;
import com.quad14.obdnewtry.io.ObdGatewayService;


public class TotalTravelFrag extends Fragment {

    View view;
    TextView TotalTravelKm,EngineRuntime,EngineCoolentTemp,EngineLoad;
    DigitSpeedView digitTotalTravelView;
    float density;
    ImageView EngineRuntimeQuestionMark,EngineLoadQuestionMark,EngineCoolentTempQuestionMark;

    String travelkmData,engineRuntimeData,engineLoadData,enginecoolentData,mytravelkm,myEngineRuntime,myEngineLoad,myEngineCoolentTemp;
    public TotalTravelFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//#################################--Getting Display Info of any device--##########################################

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        density=displayMetrics.density;

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && density>2){
            view= inflater.inflate(R.layout.fragment_total_travel_landscap, container, false);
//            Toast.makeText(getActivity(),"fragment_total_travel_landscap",Toast.LENGTH_SHORT).show();
        }
        else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && density<=2){
            view= inflater.inflate(R.layout.fragment_total_travel_landscap_d2, container, false);
//            Toast.makeText(getActivity(),"fragment_total_travel_landscap_d2",Toast.LENGTH_SHORT).show();
        }
        else if(density<=2 && getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            view= inflater.inflate(R.layout.fragment_total_travel_d2, container, false);
//            Toast.makeText(getActivity(),"fragment_total_travel_d2",Toast.LENGTH_SHORT).show();

        }
        else{
            view= inflater.inflate(R.layout.fragment_total_travel, container, false);
//            Toast.makeText(getActivity(),"fragment_total_travel",Toast.LENGTH_SHORT).show();
        }

//######################--initalize--################################################################################

        TotalTravelKm=(TextView)view.findViewById(R.id.TTfrgvalue);
        digitTotalTravelView=(DigitSpeedView)view.findViewById(R.id.digitalTTfrgid);
        EngineCoolentTemp=(TextView)view.findViewById(R.id.enginetemp);
        EngineRuntime=(TextView)view.findViewById(R.id.engineRunTime);
        EngineLoad=(TextView)view.findViewById(R.id.engineload);
        EngineRuntimeQuestionMark=(ImageView)view.findViewById(R.id.engineRuntimequestionmark);
        EngineLoadQuestionMark=(ImageView)view.findViewById(R.id.engineloadquestionmark);
        EngineCoolentTempQuestionMark=(ImageView)view.findViewById(R.id.engcooltempquestionmark);

        EngineRuntimeQuestionMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuestionMark("Engine Runtime","Duration of Engine Run Time");
            }
        });
        EngineLoadQuestionMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuestionMark("Engine Load","The engine load is the torque output of the engine, Torque is proportional to the amount of force put on the piston ");
            }
        });
        EngineCoolentTempQuestionMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuestionMark("Engine Coolent Temprature","This is showing Data of (ECT) Sensor, Average Temprature should be between 90C to 104C");
            }
        });

            Bundle bundle = this.getArguments();

            travelkmData = bundle.getString("travelkm");
            engineRuntimeData = bundle.getString("enRun");
            engineLoadData = bundle.getString("engLoad");
            enginecoolentData = bundle.getString("engCooltemp");

            if(travelkmData==null){
                mytravelkm="00000";
            }else {
                mytravelkm=travelkmData;
            }

            if(engineRuntimeData==null){
                myEngineRuntime="0";
            }else {
                myEngineRuntime=engineRuntimeData;
            }

            if(engineLoadData == null){
                myEngineLoad="10";
            }else {
                myEngineLoad=engineLoadData;
            }

            if(enginecoolentData==null){
                myEngineCoolentTemp="37";
            }else{
                myEngineCoolentTemp=enginecoolentData;
            }


        TotalTravelKm.setText(String.valueOf(mytravelkm));
        EngineRuntime.setText(String.valueOf(myEngineRuntime));
        EngineCoolentTemp.setText(String.valueOf(myEngineCoolentTemp));
        EngineLoad.setText(String.valueOf(myEngineLoad));
        digitTotalTravelView.updateSpeed(Integer.valueOf(mytravelkm));

    return view;

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();

        if(MainActivity.testclick==true){
            MainActivity.TravelClick=false;
        }else if(MainActivity.SnRClick==true){
            MainActivity.TravelClick=false;
        }else if(MainActivity.speedclick==true){
            MainActivity.TravelClick=false;
        }else if(MainActivity.homeclick==true){
            MainActivity.TravelClick=false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(MainActivity.homeclick==false) {
            MainActivity.TravelClick = true;
        }
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


}
