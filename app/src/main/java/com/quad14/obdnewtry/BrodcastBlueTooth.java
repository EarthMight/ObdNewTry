package com.quad14.obdnewtry;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class BrodcastBlueTooth extends BroadcastReceiver {
    public BrodcastBlueTooth() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String DeviceName=null;
        String action = intent.getAction();
//        Log.d("BroadcastActions", "Action "+action+"received");
        int state;
        BluetoothDevice bluetoothDevice;

        switch(action)
        {
            case BluetoothAdapter.ACTION_STATE_CHANGED:
                state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1);
                if (state == BluetoothAdapter.STATE_OFF)
                {
//                    Toast.makeText(context, "Bluetooth is off", Toast.LENGTH_SHORT).show();
                    Log.d("BroadcastActions", "Bluetooth is off");
                }
                else if (state == BluetoothAdapter.STATE_TURNING_OFF)
                {
//                    Toast.makeText(context, "Bluetooth is turning off", Toast.LENGTH_SHORT).show();
                    Log.d("BroadcastActions", "Bluetooth is turning off");
                }
                else if(state == BluetoothAdapter.STATE_ON)
                {
                    Log.d("BroadcastActions", "Bluetooth is on");
                }
                break;

            case BluetoothDevice.ACTION_ACL_CONNECTED:
                bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Toast.makeText(context, "Connected to "+bluetoothDevice.getName(), Toast.LENGTH_SHORT).show();
                Log.d("BroadcastActions", "Connected to "+bluetoothDevice.getName());

                DeviceName=bluetoothDevice.getName();
                if (DeviceName.contains("OBD")){
                    Intent newIntent = new Intent();
                    newIntent.setClassName("com.quad14.obdnewtry", "com.quad14.obdnewtry.activity.MainActivity");
                    newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(newIntent);


                }else if(DeviceName.contains("obd")){
                    Intent newIntent = new Intent();
                    newIntent.setClassName("com.quad14.obdnewtry", "com.quad14.obdnewtry.activity.MainActivity");
                    newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(newIntent);
                }

                break;

            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                Toast.makeText(context, "Disconnected from "+bluetoothDevice.getName(),
//                        Toast.LENGTH_SHORT).show();
                break;
        }

        if(action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)) {

            int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE, BluetoothAdapter.ERROR);

            switch(mode){
                case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:


                    break;
                case BluetoothAdapter.SCAN_MODE_CONNECTABLE:


                    break;
                case BluetoothAdapter.SCAN_MODE_NONE:


                    break;
            }
        }
    }
}
