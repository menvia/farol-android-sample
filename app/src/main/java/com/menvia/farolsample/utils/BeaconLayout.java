package com.menvia.farolsample.utils;

public enum BeaconLayout {
    IBEACON("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24");

    private final String layout;

    BeaconLayout(String layout) {
        this.layout = layout;
    }

    public String layout() {
        return layout;
    }
}
