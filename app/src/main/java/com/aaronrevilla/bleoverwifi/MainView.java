package com.aaronrevilla.bleoverwifi;

/**
 * Created by aaronrevilla on 1/24/18.
 */

public interface MainView {

        void hasBleFeature();
        void hasBluetoothPermissions();
        void hasLocationPermissions();
        void requestEnableBt();

}
