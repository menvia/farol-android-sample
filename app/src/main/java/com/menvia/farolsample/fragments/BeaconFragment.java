package com.menvia.farolsample.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.menvia.farolsample.R;
import com.menvia.farolsample.utils.BeaconLayout;
import com.menvia.farolsample.utils.BeaconSimulator;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

public class BeaconFragment extends Fragment implements BeaconConsumer {
    public static final String EXTRA_BEACON = "com.menvia.farolsample.beacon";

    private static final String UNIQUE_RANGING_ID = "FarolBeaconUniqueId";

    private BeaconManager mBeaconManager;
    private Beacon mBeacon;

    private TextView mBeaconUUIDTextView;
    private TextView mBeaconMajorTextView;
    private TextView mbeaconMinorTextView;
    private TextView mBeaconDistanceTextView;

    public static BeaconFragment newInstance(Beacon beacon) {
        BeaconFragment fragment = new BeaconFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_BEACON, beacon);
        fragment.setArguments(args);
        return fragment;
    }

    public BeaconFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBeacon = getArguments().getParcelable(EXTRA_BEACON);

        // Beacon: simulator
        if (false) BeaconManager.setBeaconSimulator(new BeaconSimulator());

        // Beacon: set initial parameters and start scanning, if ble is present
        mBeaconManager = BeaconManager.getInstanceForApplication(getActivity());
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconLayout.IBEACON.layout()));
        mBeaconManager.bind(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_beacon, container, false);

        mBeaconUUIDTextView = (TextView) v.findViewById(R.id.beaconUUIDTextView);
        mBeaconMajorTextView = (TextView) v.findViewById(R.id.beaconMajorTextView);
        mbeaconMinorTextView = (TextView) v.findViewById(R.id.beaconMinorTextView);
        mBeaconDistanceTextView = (TextView) v.findViewById(R.id.beaconDistanceTextView);

        loadBeacon();

        // If beacon simulator is set, create beacons
        if (BeaconManager.getBeaconSimulator() != null)
            ((BeaconSimulator) BeaconManager.getBeaconSimulator()).createBasicSimulatedBeacons();

        return v;
    }

    @Override
    public void onBeaconServiceConnect() {
        mBeaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    mBeacon = beacons.iterator().next();
                }

                if (getActivity() != null) getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadBeacon();
                    }
                });
            }
        });

        try {
            mBeaconManager.startRangingBeaconsInRegion(new Region(UNIQUE_RANGING_ID, mBeacon.getId1(), mBeacon.getId2(), mBeacon.getId3()));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Beacon: come back from background mode
        if (!mBeaconManager.isBound(this)) mBeaconManager.bind(this);
        if (mBeaconManager.isBound(this))
            mBeaconManager.setBackgroundMode(false);
    }

    @Override
    public void onPause() {
        super.onPause();

        // Beacon: set background mode
        if (mBeaconManager.isBound(this))
            mBeaconManager.setBackgroundMode(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Beacon: unbind
        if (mBeaconManager.isBound(this)) mBeaconManager.unbind(this);
    }

    @Override
    public Context getApplicationContext() {
        return getActivity().getApplicationContext();
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        getActivity().unbindService(serviceConnection);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return getActivity().bindService(intent, serviceConnection, i);
    }

    public void loadBeacon() {
        if (mBeacon != null) {
            mBeaconUUIDTextView.setText(mBeacon.getId1().toString());
            mBeaconMajorTextView.setText(mBeacon.getId2().toString());
            mbeaconMinorTextView.setText(mBeacon.getId3().toString());
            mBeaconDistanceTextView.setText(String.format("%1$.2fm", mBeacon.getDistance()));
        }
    }
}
