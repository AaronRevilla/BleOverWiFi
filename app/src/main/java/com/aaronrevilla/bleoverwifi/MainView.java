package com.aaronrevilla.bleoverwifi;

import com.aaronrevilla.bleoverwifi.scanner.BleObjectWrapper;

import java.util.List;

/**
 * Created by aaronrevilla on 1/24/18.
 */

public interface MainView {

        void hasBleFeature();
        void hasBluetoothPermissions();
        void hasLocationPermissions();
        void requestEnableBt();
        void displayDevices(List<BleObjectWrapper> devices);
}
