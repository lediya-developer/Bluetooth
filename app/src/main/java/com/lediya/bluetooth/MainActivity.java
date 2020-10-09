package com.lediya.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.lediya.bluetooth.databinding.MainBinding;

import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter bluetoothAdapter;
    private MainBinding binding;
    private ArrayAdapter<String> arrayAdapter;
    BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name and the MAC address of the object to the arrayAdapter
                arrayAdapter.add(device.getName() + "\n" + device.getAddress());
                arrayAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.main);
        initialiseAdapter();
    }

    private void initialiseAdapter() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not supported", Toast.LENGTH_LONG).show();
        } else {
            arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
            binding.list.setAdapter(arrayAdapter);
        }
    }

    public void ScanDevice(View view) {
        if (bluetoothAdapter.isDiscovering()) {
            // the button is pressed when it discovers, so cancel the discovery
            bluetoothAdapter.cancelDiscovery();
        } else {
            arrayAdapter.clear();
            bluetoothAdapter.startDiscovery();
            registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (bluetoothAdapter.isEnabled()) {
                binding.textView.setText("Bluetooth is On");
            } else {
                binding.textView.setText("Bluetooth is Off");
            }
        }
    }

    public void turnOnDevice(View view) {
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_LONG).show();
        }
    }

    public void turnOffDevice(View view) {
        if (bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.disable();
        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth is already off", Toast.LENGTH_LONG).show();
        }
    }

    public void PairDevice(View view) {
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        arrayAdapter.clear();
        for (BluetoothDevice device : pairedDevices)
            arrayAdapter.add(device.getName() + "\n" + device.getAddress());
        arrayAdapter.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(), "Show Paired Devices",
                Toast.LENGTH_SHORT).show();
    }
}
