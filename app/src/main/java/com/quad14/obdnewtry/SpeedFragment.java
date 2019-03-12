package com.quad14.obdnewtry;

import android.app.ActivityManager;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.anastr.speedviewlib.SpeedView;
import com.github.capur16.digitspeedviewlib.DigitSpeedView;
import com.quad14.obdnewtry.activity.MainActivity;


public class SpeedFragment extends Fragment {

    View view;
    TextView SpeedFrgvalue;
    DigitSpeedView DigitrpmFrgView;
    SpeedView speedometer;

    LinearLayout linearLayoutspeed;
    String mySpeed;
    String Data;
    int height,width,densitydpi;
    float density;

    private TabLayout tabLayout;
    private ViewPager viewPager;


    public SpeedFragment() {
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
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        density=displayMetrics.density;
        densitydpi=displayMetrics.densityDpi;

        // Inflate the layout for this fragment

        if(height>2280 && densitydpi<=460){
            view = inflater.inflate(R.layout.fragment_speed1080x2340, container, false);
//            Toast.makeText(getActivity(),"fragment_speed1080x2340",Toast.LENGTH_SHORT).show();
        }
        else if(height>1920 && densitydpi<=460){
            view = inflater.inflate(R.layout.fragment_speed1080x2280, container, false);
//            Toast.makeText(getActivity(),"fragment_speed1080x2280",Toast.LENGTH_SHORT).show();

        }
        else if(density<=2 && getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            view = inflater.inflate(R.layout.fragment_speed_density_2, container, false);
//            Toast.makeText(getActivity(),"fragment_speed_density_2_Portrait",Toast.LENGTH_SHORT).show();


        }else if(densitydpi>=480 && height<=1920){
            view = inflater.inflate(R.layout.fragment_speed_density_2, container, false);
//            Toast.makeText(getActivity(),"fragment_speed_density_2",Toast.LENGTH_SHORT).show();

        }
        else if(height>2280 && densitydpi>=480){
            view = inflater.inflate(R.layout.fragment_speed1080x2340_480dpi, container, false);
//            Toast.makeText(getActivity(),"fragment_speed1080x2340_480dpi",Toast.LENGTH_SHORT).show();

        }else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && density>2 ){
            view = inflater.inflate(R.layout.fragment_speed_landscap, container, false);
//            Toast.makeText(getActivity(),"fragment_speed_landscap",Toast.LENGTH_SHORT).show();

        }
        else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && density<=2){
            view = inflater.inflate(R.layout.fragment_speed_density_2_landscap, container, false);
//            Toast.makeText(getActivity(),"fragment_speed_density_2_landscap",Toast.LENGTH_SHORT).show();

        }
        else{
            view = inflater.inflate(R.layout.fragment_speed, container, false);
//            Toast.makeText(getActivity(),"fragment_speed",Toast.LENGTH_SHORT).show();
        }

        SpeedFrgvalue = (TextView) view.findViewById(R.id.speedfrgvalue);
        speedometer = (SpeedView) view.findViewById(R.id.speedViewfrag);
        linearLayoutspeed = (LinearLayout) view.findViewById(R.id.digitalspeedlinaear);
        DigitrpmFrgView = (DigitSpeedView) view.findViewById(R.id.digitalSpeedfrgid);

            Bundle bundle = this.getArguments();
            Data = bundle.getString("speed");
            if(Data==null){
                mySpeed="1";
            }else{
                mySpeed = Data;
            }

        linearLayoutspeed.setBackgroundColor(getResources().getColor(R.color.primary));
        SpeedFrgvalue.setText(mySpeed);

        DigitrpmFrgView.updateSpeed(Integer.valueOf(mySpeed));
        if(Integer.valueOf(mySpeed)>60 && Integer.valueOf(mySpeed)<90){
            linearLayoutspeed.setBackgroundColor(getResources().getColor(R.color.myello));
        }else if (Integer.valueOf(mySpeed)>=90){

            linearLayoutspeed.setBackgroundColor(getResources().getColor(R.color.mred));
        }

        speedometer.setMinMaxSpeed(0,200);
        speedometer.speedTo(Integer.valueOf(mySpeed),(0));
        speedometer.setTicks(Float.valueOf(0),Float.valueOf(20),Float.valueOf(40),Float.valueOf(60),Float.valueOf(80),Float.valueOf(100),Float.valueOf(120),Float.valueOf(140),Float.valueOf(160),Float.valueOf(180),Float.valueOf(200));

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
            MainActivity.speedclick=false;
        }else if(MainActivity.TravelClick==true){
            MainActivity.speedclick=false;
        }else if(MainActivity.SnRClick==true){
            MainActivity.speedclick=false;
        }else if(MainActivity.homeclick==true){
            MainActivity.speedclick=false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(MainActivity.homeclick==false){

            MainActivity.speedclick=true;

        }
    }


//        if (isMyServiceRunning(MockObdGatewayService.class) == true ) {
//            Bundle bundle = this.getArguments();
//            Data = bundle.getString("speed");
//            mySpeed = Data;
//            SStext.setText("Demo Service is Running");
//
//
//        }else if(isMyServiceRunning(ObdGatewayService.class) == true){
//
//            Bundle bundle = this.getArguments();
//            Data = bundle.getString("speed");
//            mySpeed = Data;
//            SStext.setText("OBD Service is Running");
//
//
//        } else{
//            SStext.setText("Service is not running So Demo Data");
//            mySpeed = String.valueOf(8);
//         }



}
