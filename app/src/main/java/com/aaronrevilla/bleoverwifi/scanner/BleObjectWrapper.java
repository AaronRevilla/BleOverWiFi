package com.aaronrevilla.bleoverwifi.scanner;

import android.bluetooth.BluetoothDevice;
import android.util.AndroidException;
import android.util.Log;

public class BleObjectWrapper {

    private BluetoothDevice bluetoothDevice;
    private int rssi;
    private byte[] scanRecord;

    public BleObjectWrapper(BluetoothDevice bluetoothDevice, int rssi, byte[] scanRecord){
        this.bluetoothDevice = bluetoothDevice;
        this.rssi = rssi;
        this.scanRecord = scanRecord;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public byte[] getScanRecord() {
        return scanRecord;
    }

    public void setScanRecord(byte[] scanRecord) {
        this.scanRecord = scanRecord;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof BleObjectWrapper){
            final BleObjectWrapper temp = (BleObjectWrapper) obj;
            return this.getBluetoothDevice().getAddress().equals(temp.getBluetoothDevice().getAddress());
        }else{
            return false;
        }
    }
}
