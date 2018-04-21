package com.aaronrevilla.bleoverwifi.scanner;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by aaronrevilla on 1/24/18.
 */

public class BleScanner extends Service {

    private static final String TAG = "BleScanner_";
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        return START_STICKY;//services that are explicitly started and stopped as needed
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "in onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "in onUnbind");
        return true;
    }

    public void startScanner(){
        
    }

    public void stopScanner(){

    }

    public class LocalBinder extends Binder {
        public BleScanner getService(){
            return BleScanner.this;
        }
    }
}
