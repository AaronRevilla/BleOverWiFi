package com.aaronrevilla.bleoverwifi;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.aaronrevilla.bleoverwifi.scanner.BleScanner;

/**
 * Created by aaronrevilla on 1/24/18.
 */

public class MainPresenterImp implements MainPresenter {

    private static final String TAG = "MainPresenterImp_";
    private MainView view;
    private Context context;
    private boolean mServiceBound;
    private BleScanner scanner;
    private Handler handler;

    private Runnable readDevicesFromScanner = new Runnable() {
        @Override
        public void run() {
            // Do something here on the main thread
            Log.d(TAG, "readDevicesFromScanner mServiceBound " + mServiceBound );
            if(mServiceBound){
                Log.d(TAG, "scanner " + scanner.getBleObjects().size() );
                view.displayDevices(scanner.getBleObjects());
            }

            handler.postDelayed(this, 2000);
        }
    };

    public  MainPresenterImp(MainView view, Context context) {
        this. view = view;
        this.context = context;
        bindService();
    }

    @Override
    public void startScanner() {
        if(scanner != null){
            scanner.startScanner();
            getDevices();
        }else{
            bindService();
        }
    }

    @Override
    public void stopScaner() {
        if(scanner != null){
            scanner.stopScanner();
        }
    }

    @Override
    public void getDevices() {
        if(handler!= null){
            handler.post(readDevicesFromScanner);
        }else{
            handler = new Handler();
            handler.post(readDevicesFromScanner);
        }
    }

    public void bindService() {
        if(!mServiceBound){
            Intent intent = new Intent(context, BleScanner.class);
            context.startService(intent);
            context.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    public void unBindService() {
        if(mServiceBound){
            context.unbindService(mServiceConnection);
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BleScanner.LocalBinder myBinder = (BleScanner.LocalBinder) service;
            scanner = myBinder.getService();
            mServiceBound = true;
        }
    };
}
