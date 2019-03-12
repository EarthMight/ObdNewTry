package com.quad14.obdnewtry;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.anastr.speedviewlib.DeluxeSpeedView;
import com.github.capur16.digitspeedviewlib.DigitSpeedView;
import com.quad14.obdnewtry.activity.MainActivity;

public class RpmsFragment extends Fragment {

    View view;
    TextView testFrgvalue,RStxt;
    DigitSpeedView DigitrpmFrgView;
    DeluxeSpeedView testometer;
    LinearLayout Digitaltestlinaear;
    String myRpm,Data;
    int height,width,densitydpi;
    float density;

    public RpmsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //##################################-- Display method --########################################################

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        density=displayMetrics.density;
        densitydpi=displayMetrics.densityDpi;

        // Inflate the layout for this fragment
        if(height>2280 && densitydpi<=460){
            view=inflater.inflate(R.layout.fragment_rpms1080x2340, container, false);
//            Toast.makeText(getActivity(), "fragment_rpms1080x2340", Toast.LENGTH_SHORT).show();

        }
        else if(height>1920 && densitydpi<=460){
            view=inflater.inflate(R.layout.fragment_rpms1080x2280, container, false);
//            Toast.makeText(getActivity(), "fragment_rpms1080x2280", Toast.LENGTH_SHORT).show();

        }
        else if(density<=2 && getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            view=inflater.inflate(R.layout.fragment_rpms_density_2, container, false);
//            Toast.makeText(getActivity(), "fragment_rpms_density_2", Toast.LENGTH_SHORT).show();

        }
        else if(densitydpi>=480 && height<=1920){
            view=inflater.inflate(R.layout.fragment_rpms_density_2, container, false);
//            Toast.makeText(getActivity(), "fragment_rpms_density_2", Toast.LENGTH_SHORT).show();

        }

        else if(height>2280 && densitydpi>=480){
            view=inflater.inflate(R.layout.fragment_rpms1080x2340_480dpi, container, false);
//            Toast.makeText(getActivity(), "fragment_rpms1080x2340_480dpi", Toast.LENGTH_SHORT).show();

        }

        else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && density>2){
            view = inflater.inflate(R.layout.fragment_rpms_landscap, container, false);
//            Toast.makeText(getActivity(), "fragment_rpms_landscap", Toast.LENGTH_SHORT).show();

        }

        else if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE && density<=2){
            view = inflater.inflate(R.layout.fragment_rpms_landscap, container, false);
//            Toast.makeText(getActivity(), "fragment_rpms_landscap_d2", Toast.LENGTH_SHORT).show();

        }
        else{
            view=inflater.inflate(R.layout.fragment_rpms, container, false);
//            Toast.makeText(getActivity(), "fragment_rpms", Toast.LENGTH_SHORT).show();
        }

        // Inflate the layout for this fragment
        testFrgvalue=(TextView)view.findViewById(R.id.testfrgvalue);
        testometer=(DeluxeSpeedView)view.findViewById(R.id.testViewfrag);
        DigitrpmFrgView=(DigitSpeedView)view.findViewById(R.id.digitaltestfrgid);

        Digitaltestlinaear=(LinearLayout)view.findViewById(R.id.digitaltestlinaear);

            Bundle bundle = this.getArguments();
            Data = bundle.getString("rpm");

        if(Data==null){
            myRpm="1";

        }else{
            myRpm = Data;
        }
        Digitaltestlinaear.setBackgroundColor(getResources().getColor(R.color.primary));

        testFrgvalue.setText(myRpm);
        DigitrpmFrgView.updateSpeed(Integer.valueOf(myRpm));

        if(Integer.valueOf(myRpm)>2000 && Integer.valueOf(myRpm)<4000){
            Digitaltestlinaear.setBackgroundColor(getResources().getColor(R.color.myello));
        }else if (Integer.valueOf(myRpm)>4000){
            Digitaltestlinaear.setBackgroundColor(getResources().getColor(R.color.mred));
        }
        testometer.setMaxSpeed(8000);
        testometer.speedTo(Integer.valueOf(myRpm),(0));
        testometer.setWithEffects(true);

        testometer.setTicks(Float.valueOf(0),Float.valueOf(1000),Float.valueOf(2000),Float.valueOf(3000),Float.valueOf(4000),Float.valueOf(5000),Float.valueOf(6000),Float.valueOf(7000),Float.valueOf(8000));
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

        if(MainActivity.speedclick==true){
            MainActivity.testclick=false;
        }else if(MainActivity.TravelClick==true){
            MainActivity.testclick=false;
        }else if(MainActivity.SnRClick==true){
            MainActivity.testclick=false;
        }else if(MainActivity.homeclick==true){
            MainActivity.testclick=false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(MainActivity.homeclick==false) {
            MainActivity.testclick = true;
        }
    }

}
