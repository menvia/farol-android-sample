package com.menvia.farolsample;

import android.app.Fragment;

import com.menvia.farolsample.fragments.MainFragment;
import com.menvia.farolsample.utils.SingleFragmentActivity;

public class MainActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return MainFragment.newInstance();
    }
}
