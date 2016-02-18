package com.menvia.farolsample;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.menvia.farolsample.fragments.BeaconFragment;
import com.menvia.farolsample.utils.SingleFragmentActivity;

import org.altbeacon.beacon.Beacon;

public class BeaconActivity extends SingleFragmentActivity {


    @Override
    protected Fragment createFragment() {
        return BeaconFragment.newInstance((Beacon) getIntent().getParcelableExtra(BeaconFragment.EXTRA_BEACON));
    }
}
