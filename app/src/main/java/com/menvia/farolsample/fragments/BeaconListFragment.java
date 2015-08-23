package com.menvia.farolsample.fragments;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.menvia.farolsample.BeaconActivity;
import com.menvia.farolsample.R;
import com.menvia.farolsample.utils.BeaconLayout;
import com.menvia.farolsample.utils.BeaconSimulator;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;

public class BeaconListFragment extends ListFragment implements BeaconConsumer {
    private static final String UNIQUE_RANGING_ID = "FarolBeaconUniqueId";

    private BeaconManager mBeaconManager;
    private ArrayList<Beacon> mBeacons;
    private BeaconListAdapter mAdapter;

    public static BeaconListFragment newInstance() {
        BeaconListFragment fragment = new BeaconListFragment();
        return fragment;
    }

    public BeaconListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBeacons = new ArrayList<>();
        mAdapter = new BeaconListAdapter(getActivity(), mBeacons);
        setListAdapter(mAdapter);

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
        View v = inflater.inflate(R.layout.fragment_beacon_list, container, false);

        // If beacon simulator is set, create beacons
        if (BeaconManager.getBeaconSimulator() != null)
            ((BeaconSimulator) BeaconManager.getBeaconSimulator()).createBasicSimulatedBeacons();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Beacon: come back from background mode
        if (!mBeaconManager.isBound(this)) mBeaconManager.bind(this);
        if (mBeaconManager.isBound(this))
            mBeaconManager.setBackgroundMode(false);

        ((BeaconListAdapter) getListAdapter()).notifyDataSetChanged();
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
    public void onListItemClick(ListView l, View v, int position, long id) {
        Beacon beacon = ((BeaconListAdapter) getListAdapter()).getItem(position);
        Intent i = new Intent(getActivity(), BeaconActivity.class);
        i.putExtra(BeaconFragment.EXTRA_BEACON, beacon);
        // unbind it so it wont collide with thing detail screen
        if (mBeaconManager.isBound(this)) mBeaconManager.unbind(this);
        startActivity(i);
    }

    @Override
    public void onBeaconServiceConnect() {
        mBeaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                mBeacons.clear();
                if (beacons.size() > 0) {
                    mBeacons.addAll(beacons);
                }

                if (getActivity() != null) getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        try {
            mBeaconManager.startRangingBeaconsInRegion(new Region(UNIQUE_RANGING_ID, null, null, null));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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

    // Beacon List Adapter
    public class BeaconListAdapter extends ArrayAdapter<Beacon> {

        public BeaconListAdapter(Context context, ArrayList<Beacon> beacons) {
            super(context, android.R.layout.simple_list_item_1, beacons);
        }

        public class ViewHolder {
            TextView beaconUUIDTextView;
            TextView beaconMajorTextView;
            TextView beaconMinorTextView;
            TextView beaconDistanceTextView;

            public ViewHolder(View v) {
                beaconUUIDTextView = (TextView) v.findViewById(R.id.beaconUUIDTextView);
                beaconMajorTextView = (TextView) v.findViewById(R.id.beaconMajorTextView);
                beaconMinorTextView = (TextView) v.findViewById(R.id.beaconMinorTextView);
                beaconDistanceTextView = (TextView) v.findViewById(R.id.beaconDistanceTextView);
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.list_item_beacon, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Beacon beacon = getItem(position);

            holder.beaconUUIDTextView.setText(beacon.getId1().toString());
            holder.beaconMajorTextView.setText(beacon.getId2().toString());
            holder.beaconMinorTextView.setText(beacon.getId3().toString());
            holder.beaconDistanceTextView.setText(String.format("%1$.2fm", beacon.getDistance()));

            return convertView;
        }
    }
}
