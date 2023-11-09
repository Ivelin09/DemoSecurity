package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpResponse;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.StatusLine;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.HttpClient;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.client.methods.HttpGet;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity implements
        OnCheckedChangeListener {
    static final String TAG = "DevicePolicyDemoActivity";
    static final int ACTIVATION_REQUEST = 47; // identifies our request id
    DevicePolicyManager devicePolicyManager;
    ComponentName demoDeviceAdmin;
    ToggleButton toggleButton;

    /** Called when the activity is first created. */
    @SuppressLint("ResourceType")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.main);

        toggleButton = (ToggleButton) super
                .findViewById(R.id.toggle_device_admin);
        toggleButton.setOnCheckedChangeListener(this);

        // Initialize Device Policy Manager service and our receiver class
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        demoDeviceAdmin = new ComponentName(this, DemoDeviceAdminReceiver.class);
    }

    /**
     * Called when a button is clicked on. We have Lock Device and Reset Device
     * buttons that could invoke this method.
     */
    @SuppressLint("LongLogTag")
    public void onClick(View v) {
        switch (v.getId()) {
            case 0x7f060003:
                // We lock the screen
                Toast.makeText(this, "Locking device...", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Locking device now");
                devicePolicyManager.lockNow();
                break;
            case 0x7f060004:
                // We reset the device - this will erase entire /data partition!
                Toast.makeText(this, "Locking device...", Toast.LENGTH_LONG).show();
                Log.d(TAG,
                        "RESETing device now - all user data will be ERASED to factory settings");
                devicePolicyManager.wipeData(ACTIVATION_REQUEST);
                break;
        }
    }

    /**
     * Called when the state of toggle button changes. In this case, we send an
     * intent to activate the device policy administration.
     */
    @SuppressLint("LongLogTag")
    @Override
    public void onCheckedChanged(CompoundButton button, boolean isChecked) {
        if (isChecked) {
            // Activate device administration
            Intent intent = new Intent(
                    DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                    demoDeviceAdmin);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "Your boss told you to do this");
            startActivityForResult(intent, ACTIVATION_REQUEST);
        }
        Log.d(TAG, "onCheckedChanged to: " + isChecked);
    }

    /**
     * Called when startActivityForResult() call is completed. The result of
     * activation could be success of failure, mostly depending on user okaying
     * this app's request to administer the device.
     */
    @SuppressLint("LongLogTag")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTIVATION_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Log.i(TAG, "Administration enabled!");
                    toggleButton.setChecked(true);
                } else {
                    Log.i(TAG, "Administration enable FAILED!");
                    toggleButton.setChecked(false);
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}