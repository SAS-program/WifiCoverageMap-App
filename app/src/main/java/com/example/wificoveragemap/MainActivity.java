package com.example.wificoveragemap;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    private TextView tvStatus, tvRssi;
    private CoverageOverlayView overlayView;
    private int currentRssi = -100;
    private String currentSsid = "";

    private BroadcastReceiver scanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (wifiManager.getConnectionInfo() != null) {
                currentRssi = wifiManager.getConnectionInfo().getRssi();
                currentSsid = wifiManager.getConnectionInfo().getSSID();
                tvStatus.setText("Wi-Fi: " + currentSsid);
                tvRssi.setText("RSSI: " + currentRssi + " dBm");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus = findViewById(R.id.tvStatus);
        tvRssi = findViewById(R.id.tvRssi);
        overlayView = findViewById(R.id.overlayView);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        }, 1);

        registerReceiver(scanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        findViewById(R.id.btnScan).setOnClickListener(v -> {
            if (wifiManager != null && wifiManager.isWifiEnabled()) {
                wifiManager.startScan();
                Toast.makeText(this, "Scanning...", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Wi-Fi is disabled", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.btnRecord).setOnClickListener(v -> {
            float x = overlayView.getTapX();
            float y = overlayView.getTapY();
            if (x < 0 || y < 0) {
                Toast.makeText(this, "Tap on the map first", Toast.LENGTH_SHORT).show();
                return;
            }
            if (currentRssi == -100) {
                Toast.makeText(this, "Scan Wi-Fi first", Toast.LENGTH_SHORT).show();
                return;
            }
            overlayView.addPoint(x, y, currentRssi);
            Toast.makeText(this, "Point recorded: " + currentRssi + " dBm", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnClear).setOnClickListener(v -> {
            overlayView.clearPoints();
            Toast.makeText(this, "Map cleared", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(scanReceiver);
    }
}