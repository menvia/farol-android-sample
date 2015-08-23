package com.menvia.farolsample;

import android.app.Fragment;

import com.menvia.farolsample.fragments.BeaconFragment;
import com.menvia.farolsample.utils.SingleFragmentActivity;

import org.altbeacon.beacon.Beacon;

public class BeaconActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        Fragment frag = BeaconFragment.newInstance((Beacon) getIntent().getParcelableExtra(BeaconFragment.EXTRA_BEACON));
        return frag;
    }
}
