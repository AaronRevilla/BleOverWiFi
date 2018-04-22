package com.aaronrevilla.bleoverwifi;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aaronrevilla.bleoverwifi.scanner.BleObjectWrapper;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MainView {

    private static final String TAG = "MainActivity_";
    private BluetoothStateReceiver bluetoothStateReceiver;
    private IntentFilter btIntentFilter;
    private BluetoothAdapter bluetoothAdapter;
    private final static int REQUEST_ENABLE_BT = 1;
    private final static int REQUEST_FINE_LOCATION = 2;
    private MainPresenterImp presenter;

    private RecyclerView recyclerView;
    private BleDeviceAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new MainPresenterImp(this, getApplicationContext());

        //create BT State Receiver
        bluetoothStateReceiver = new BluetoothStateReceiver();
        btIntentFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothStateReceiver, btIntentFilter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        recyclerView = ((RecyclerView) findViewById(R.id.devices_list));

    }

    @Override
    protected void onResume() {
        super.onResume();
        hasBleFeature();
        hasBluetoothPermissions();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasLocationPermissions();
            //presenter.startScanner();
        }else{
            //presenter.startScanner();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(bluetoothStateReceiver != null){
            unregisterReceiver(bluetoothStateReceiver);
        }
        //presenter.stopScaner();
    }

    @Override
    public void hasBleFeature() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            finish();
        }
    }

    @Override
    public void hasBluetoothPermissions() {

        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            requestEnableBt();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void hasLocationPermissions() {
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
        }
    }

    @Override
    public void requestEnableBt() {
        showAlertDialog(getString(R.string.enable_bluetooth_title), getString(R.string.enable_bluetooth_message), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {//possitive button
                dialog.dismiss();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {//cancel button
                dialog.dismiss();
            }
        });
    }

    @Override
    public void displayDevices(final List<BleObjectWrapper> devices) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                listAdapter = new BleDeviceAdapter(devices);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(listAdapter);
            }
        });
    }

    public void showAlertDialog(String title, String message,  DialogInterface.OnClickListener positiveListener,  DialogInterface.OnClickListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (title != null) builder.setTitle(title);

        builder.setMessage(message);
        builder.setPositiveButton("OK", positiveListener);
        builder.setNegativeButton("Cancel", cancelListener);
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startScanner(View view) {
        presenter.startScanner();
    }

    public void stopScanner(View view) {
        presenter.stopScaner();
    }

    private class BluetoothStateReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch (state){

                    case BluetoothAdapter.STATE_ON:
                        Toast.makeText(context, "BT ON", Toast.LENGTH_SHORT).show();
                        break;

                    case BluetoothAdapter.STATE_OFF:
                        Toast.makeText(context, "BT OFF", Toast.LENGTH_SHORT).show();
                        requestEnableBt();
                        break;

                    case BluetoothAdapter.STATE_TURNING_ON:
                        Toast.makeText(context, "BT TURNING ON", Toast.LENGTH_SHORT).show();
                        break;

                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Toast.makeText(context, "BT TURNING OFF", Toast.LENGTH_SHORT).show();
                        break;

                    case BluetoothAdapter.STATE_CONNECTING:
                        Toast.makeText(context, "BT CONNECTING", Toast.LENGTH_SHORT).show();
                        break;

                    case BluetoothAdapter.ERROR:
                        Toast.makeText(context, "BT OFF", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

        }
    }

    private class BleDeviceAdapter extends RecyclerView.Adapter<BleDeviceAdapter.BleViewHolder>{

        public List<BleObjectWrapper> bleDevices;

        public BleDeviceAdapter(List<BleObjectWrapper> list){
            bleDevices = list;
        }

        @Override
        public BleDeviceAdapter.BleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ble_device_row, parent, false);
            return new BleViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(BleViewHolder holder, int position) {
            BleObjectWrapper device = bleDevices.get(position);
            holder.deviceNameView.setText(device.getBluetoothDevice().getName());
            holder.deviceMACView.setText(device.getBluetoothDevice().getAddress());
            holder.deviceRssiView.setText(""+device.getRssi());
            holder.deviceScanRecordView.setText(device.getScanRecord().toString());
        }

        @Override
        public int getItemCount() {
            return bleDevices.size();
        }

        public class BleViewHolder extends RecyclerView.ViewHolder{
            public TextView deviceNameView, deviceMACView, deviceRssiView, deviceScanRecordView;

            public BleViewHolder(View itemView) {
                super(itemView);
                deviceNameView = ((TextView) itemView.findViewById(R.id.ble_device_name));
                deviceMACView = ((TextView) itemView.findViewById(R.id.ble_device_mac));
                deviceRssiView = ((TextView) itemView.findViewById(R.id.ble_device_rssi));
                deviceScanRecordView = ((TextView) itemView.findViewById(R.id.ble_device_bytes));
            }
        }
    }
}
