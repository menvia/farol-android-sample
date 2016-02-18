package com.menvia.farolsample;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.menvia.farolsample.fragments.MainFragment;
import com.menvia.farolsample.utils.SingleFragmentActivity;

public class MainActivity extends SingleFragmentActivity {
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    @Override
    protected Fragment createFragment() {
        return MainFragment.newInstance();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                showDialog();
            }
        }
    }

    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("This app needs location access");
        builder.setMessage("Please grant location access so this app can detect beacons.");
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                requestPermissions(new String[] { Manifest.permission.ACCESS_COARSE_LOCATION }, PERMISSION_REQUEST_COARSE_LOCATION);
            }
        });
        builder.show();
    }
}
