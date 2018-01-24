package com.aaronrevilla.bleoverwifi;

/**
 * Created by aaronrevilla on 1/24/18.
 */

public interface MainPresenter{

    interface Permissions{
        void hasBleFeature();
        void hasBluetoothPermissions();
        void hasLocationPermissions();
        void requestEnableBt();
    }

}
