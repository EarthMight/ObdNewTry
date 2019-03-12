package com.quad14.obdnewtry;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.capur16.digitspeedviewlib.DigitSpeedView;
import com.irozon.sneaker.Sneaker;
import com.quad14.obdnewtry.activity.MainActivity;

public class SpeedAndRpm extends Fragment {

    View view;
    DigitSpeedView SpeedDiview,RpmDiview;
    String speedData,mySpeed,rpmData,myRpm;
    TextView SpeedTxt,RpmTxt;
    LinearLayout speedtxtLiner,RpmtxtLinear;
    int height,width,densitydpi;
    float density;

    public SpeedAndRpm() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        densitydpi=displayMetrics.densityDpi;
        density=displayMetrics.density;

        // Inflate the layout for this fragment
        if(height>2280 && densitydpi<=460){
            view=inflater.inflate(R.layout.fragment_speed_and_rpm1080x2340, container, false);
//            Toast.makeText(getActivity(),"fragment_speed_and_rpm1080x2340",Toast.LENGTH_SHORT).show();
        }
        else if(height>1920 && densitydpi<=460){
            view=inflater.inflate(R.layout.fragment_speed_and_rpm1080x2280, container, false);
//            Toast.makeText(getActivity(),"fragment_speed_and_rpm1080x2280",Toast.LENGTH_SHORT).show();

        }
        else if(height>2280 && densitydpi>=480){
            view=inflater.inflate(R.layout.fragment_speed_and_rpm1080x2340_480dpi, container, false);
//            Toast.makeText(getActivity(),"fragment_speed_and_rpm1080x2340_480dpi",Toast.LENGTH_SHORT).show();

        }
        else if(density<=2 && getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            view=inflater.inflate(R.layout.fragment_speed_and_rpm_density_2, container, false);
//            Toast.makeText(getActivity(),"fragment_speed_and_rpm_density_2",Toast.LENGTH_SHORT).show();

        }
        else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && density>2){
            view = inflater.inflate(R.layout.fragment_speed_and_rpm_landscap, container, false);
//            Toast.makeText(getActivity(),"fragment_speed_and_rpm_landscap",Toast.LENGTH_SHORT).show();

        }
        else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && density<=2){
            view = inflater.inflate(R.layout.fragment_speed_and_rpm_landscap_d2, container, false);
//            Toast.makeText(getActivity(),"fragment_speed_and_rpm_landscap_d2",Toast.LENGTH_SHORT).show();

        }
        else{
            view=inflater.inflate(R.layout.fragment_speed_and_rpm, container, false);
//            Toast.makeText(getActivity(),"fragment_speed_and_rpm",Toast.LENGTH_SHORT).show();

        }

        SpeedDiview=(DigitSpeedView)view.findViewById(R.id.digitalsprfrgid);
        RpmDiview=(DigitSpeedView)view.findViewById(R.id.digitalrpsfrgid);
        SpeedTxt=(TextView)view.findViewById(R.id.snrstxtid);
        RpmTxt=(TextView)view.findViewById(R.id.rnstxtid);
        speedtxtLiner=(LinearLayout)view.findViewById(R.id.speedcontainerliner);
        RpmtxtLinear=(LinearLayout)view.findViewById(R.id.rpmcontainertxtliner);

        speedtxtLiner.setBackgroundColor(getResources().getColor(R.color.primary));
        RpmtxtLinear.setBackgroundColor(getResources().getColor(R.color.primary));

            Bundle bundle = this.getArguments();
            Bundle b2 = bundle.getBundle("r1");

            speedData = bundle.getString("speed2");
            if(speedData==null){
                mySpeed="1";

            }else{
                mySpeed = speedData;
            }

            if(b2.isEmpty()){
                Log.e("b2null", String.valueOf(b2));
                rpmData="1";
                myRpm="1";
            }else{
                rpmData=b2.get("rpm2").toString();
                myRpm=rpmData;
            }

        SpeedDiview.updateSpeed(Integer.valueOf(mySpeed));
        SpeedTxt.setText(String.valueOf(mySpeed));

        if(Integer.valueOf(mySpeed)>60 && Integer.valueOf(mySpeed)<90){

            speedtxtLiner.setBackgroundColor(getResources().getColor(R.color.myello));
        }else if (Integer.valueOf(mySpeed)>=90){

            speedtxtLiner.setBackgroundColor(getResources().getColor(R.color.mred));
        }

        RpmDiview.updateSpeed(Integer.valueOf(myRpm));
        RpmTxt.setText(String.valueOf(myRpm));

        if(Integer.valueOf(myRpm)>2000 && Integer.valueOf(myRpm)<4000){
            RpmtxtLinear.setBackgroundColor(getResources().getColor(R.color.myello));
        }else if (Integer.valueOf(myRpm)>4000){
            RpmtxtLinear.setBackgroundColor(getResources().getColor(R.color.mred));
        }

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

    public void highspeedalert(){
        Sneaker.with(getActivity()) // Activity, Fragment or ViewGroup
                .setTitle("Error!!")
                .setHeight(150)
                .setMessage("This is the error message")
                .sneakError();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(MainActivity.testclick==true){
            MainActivity.SnRClick=false;
        }else if(MainActivity.TravelClick==true){
            MainActivity.SnRClick=false;
        }else if(MainActivity.speedclick==true){
            MainActivity.SnRClick=false;
        }else if(MainActivity.homeclick==true){
            MainActivity.SnRClick=false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(MainActivity.homeclick==false) {
            MainActivity.SnRClick = true;
        }
    }



}
