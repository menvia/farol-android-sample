package com.menvia.farolsample.utils;

import org.altbeacon.beacon.AltBeacon;
import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BeaconSimulator implements org.altbeacon.beacon.simulator.BeaconSimulator {
    protected static final String TAG = "TimedBeaconSimulator";
    private List<Beacon> beacons;

    public static boolean USE_SIMULATED_BEACONS = false;

    public BeaconSimulator() {
        beacons = new ArrayList<>();
    }

    public List<Beacon> getBeacons() {
        return beacons;
    }

    private ArrayList<Beacon> createBeacons(int number_of_beacons) {
        if (number_of_beacons <= 0) number_of_beacons = 4;
        ArrayList<Beacon> b = new ArrayList<>();
        for (int i = 1; i <= number_of_beacons; i++) {
            beacons.add(new AltBeacon.Builder().setId1("11111111-2222-3333-4444-555555555555")
                    .setId2("1").setId3(Integer.toString(i)).setRssi(-55).setTxPower(-55).build());
        }
        return b;
    }

    public void createBasicSimulatedBeacons() {
        if (USE_SIMULATED_BEACONS) {
            beacons.addAll(createBeacons(10));
        }
    }

    private ScheduledExecutorService scheduleTaskExecutor;

    public void createTimedSimulatedBeacons() {
        if (USE_SIMULATED_BEACONS) {
            beacons.addAll(createBeacons(4));

            final List<Beacon> finalBeacons = new ArrayList<>(beacons);

            beacons.clear();

            scheduleTaskExecutor = Executors.newScheduledThreadPool(5);

            scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    try {
                        if (finalBeacons.size() > beacons.size())
                            beacons.add(finalBeacons.get(beacons.size()));
                        else
                            scheduleTaskExecutor.shutdown();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 10, TimeUnit.SECONDS);
        }
    }
}