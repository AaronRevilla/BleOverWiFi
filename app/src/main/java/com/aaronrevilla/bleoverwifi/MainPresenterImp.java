package com.aaronrevilla.bleoverwifi;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.aaronrevilla.bleoverwifi.scanner.BleScanner;

/**
 * Created by aaronrevilla on 1/24/18.
 */

public class MainPresenterImp implements MainPresenter {

    private MainView view;
    private Context context;
    private boolean mServiceBound;
    private BleScanner scanner;

    public  MainPresenterImp(MainView view, Context context) {
        this. view = view;
        this.context = context;
    }

    @Override
    public void startScanner() {
        if(!mServiceBound){
            bindService();
            scanner.startScanner();
        }
    }

    @Override
    public void stopScaner() {
        if(mServiceBound){
            scanner.stopScanner();
            unBindService();
        }
    }

    public void bindService() {
        Intent intent = new Intent(context, BleScanner.class);
        context.startService(intent);
        context.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void unBindService() {
        context.unbindService(mServiceConnection);
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
