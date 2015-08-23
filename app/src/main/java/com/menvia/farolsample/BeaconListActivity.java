package com.menvia.farolsample;

import android.app.Fragment;

import com.menvia.farolsample.fragments.BeaconListFragment;
import com.menvia.farolsample.utils.SingleFragmentActivity;

public class BeaconListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return BeaconListFragment.newInstance();
    }
}
