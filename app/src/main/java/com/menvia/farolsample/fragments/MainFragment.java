package com.menvia.farolsample.fragments;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.menvia.farolsample.BeaconListActivity;
import com.menvia.farolsample.R;

public class MainFragment extends Fragment {
    private Button mScanForBeaconsButton;
    private TextView mBluetoothSupportTextView;
    private TextView mBluetoothLESupportTextView;
    private TextView mAppVersionTextView;

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        mScanForBeaconsButton = (Button) v.findViewById(R.id.scanForBeaconsButton);
        mScanForBeaconsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), BeaconListActivity.class));
            }
        });

        // Bluetooth support
        mBluetoothSupportTextView = (TextView) v.findViewById(R.id.bluetoothSupportTextView);
        if (BluetoothAdapter.getDefaultAdapter() == null) {
            // Bluetooth not supported
            mBluetoothSupportTextView.setTextColor(getResources().getColor(R.color.red));
            mBluetoothSupportTextView.setText(R.string.unsupported);
        } else {
            if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                // Bluetooth enabled
                mBluetoothSupportTextView.setTextColor(getResources().getColor(R.color.green));
                mBluetoothSupportTextView.setText(R.string.enabled);
            } else {
                // Bluetooth disabled
                mBluetoothSupportTextView.setTextColor(getResources().getColor(R.color.red));
                mBluetoothSupportTextView.setText(R.string.disabled);
            }
        }

        // Bluetooth LE support
        mBluetoothLESupportTextView = (TextView) v.findViewById(R.id.bluetoothLESupportTextView);
        if (BluetoothAdapter.getDefaultAdapter() == null) {
            // Bluetooth not supported
            mBluetoothLESupportTextView.setTextColor(getResources().getColor(R.color.red));
            mBluetoothLESupportTextView.setText(R.string.unsupported);
        } else {
            if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                // Bluetooth LE supported
                mBluetoothLESupportTextView.setTextColor(getResources().getColor(R.color.green));
                mBluetoothLESupportTextView.setText(R.string.supported);
            } else {
                // Bluetooth LE not supported
                mBluetoothLESupportTextView.setTextColor(getResources().getColor(R.color.red));
                mBluetoothLESupportTextView.setText(R.string.unsupported);
            }
        }

        mAppVersionTextView = (TextView) v.findViewById(R.id.appVersionTextView);
        String version_name;
        PackageInfo package_info;
        try {
            package_info = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            version_name = package_info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version_name = "0.0.0";
        }
        mAppVersionTextView.setText(String.format("v %s", version_name));

        return v;
    }
}
