package com.example.nikolajcolic.jazgobar.eventbus;

import android.location.Location;

/**
 * Created by crepinsek on 14/04/17.
 */

public class MessageEventUpdateLocation {
    Location m;

    public MessageEventUpdateLocation(Location m) {
        this.m = m;
    }

    public Location getM() {
        return m;
    }
}
