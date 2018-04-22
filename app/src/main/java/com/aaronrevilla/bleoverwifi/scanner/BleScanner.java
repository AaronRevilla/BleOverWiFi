package com.aaronrevilla.bleoverwifi.scanner;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AndroidException;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by aaronrevilla on 1/24/18.
 */

public class BleScanner extends Service {

    private BluetoothAdapter mBluetoothAdapter;
    private static final String TAG = "BleScanner_";
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    private List<BleObjectWrapper> bleObjects;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate Service");
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bleObjects = new ArrayList<>();
        mBluetoothAdapter = bluetoothManager.getAdapter();
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
        Log.d(TAG, "start scanner");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            List<ScanFilter> filters = new ArrayList<ScanFilter>();
            ScanSettings.Builder settingsBuilder =  new ScanSettings.Builder();
            settingsBuilder.setScanMode(ScanSettings.MATCH_MODE_AGGRESSIVE);
            mBluetoothAdapter.getBluetoothLeScanner().startScan(filters, settingsBuilder.build(), mLeScanCallback21);
        }else{
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        }
    }

    public void stopScanner(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBluetoothAdapter.getBluetoothLeScanner().stopScan(mLeScanCallback21);
        }else{
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    private void handleScanResult(BluetoothDevice device, int rssi, byte[] scanRecord){
        final BleObjectWrapper bleObjectWrapper = new BleObjectWrapper(device, rssi, scanRecord);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if(bleObjects.contains(bleObjectWrapper)){
                    bleObjects.add(bleObjects.indexOf(bleObjectWrapper), bleObjectWrapper);
                }else{
                    bleObjects.add(bleObjectWrapper);
                }
            }
        });
    }

    public List<BleObjectWrapper> getBleObjects(){
        return new ArrayList<>(bleObjects);
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback(){

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            handleScanResult(device, rssi, scanRecord);
        }
    };

    private ScanCallback mLeScanCallback21 = new ScanCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if(callbackType > 0){
                switch (callbackType){
                    case SCAN_FAILED_ALREADY_STARTED:
                        Log.d(TAG, "onScanResult ERROR SCAN_FAILED_ALREADY_STARTED");
                        if(result.getDevice() != null){
                            handleScanResult(result.getDevice(), result.getRssi(), result.getScanRecord().getBytes());
                        }
                        break;
                    case SCAN_FAILED_APPLICATION_REGISTRATION_FAILED:
                        Log.d(TAG, "onScanResult ERROR SCAN_FAILED_APPLICATION_REGISTRATION_FAILED");
                        break;
                    case SCAN_FAILED_INTERNAL_ERROR:
                        Log.d(TAG, "onScanResult ERROR SCAN_FAILED_INTERNAL_ERROR");
                        break;
                    case SCAN_FAILED_FEATURE_UNSUPPORTED:
                        Log.d(TAG, "onScanResult ERROR SCAN_FAILED_FEATURE_UNSUPPORTED");
                        break;
                    default:
                        Log.d(TAG, "onScanResult ERROR UNKNOWN");
                        break;
                }
            }else{
                handleScanResult(result.getDevice(), result.getRssi(), result.getScanRecord().getBytes());
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.d(TAG, "onScanFailed errorCode " + errorCode);
        }
    };

    public class LocalBinder extends Binder {
        public BleScanner getService(){
            return BleScanner.this;
        }
    }
}
