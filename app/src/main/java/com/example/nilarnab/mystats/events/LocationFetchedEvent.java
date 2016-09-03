package com.example.nilarnab.mystats.events;

/**
 * Created by nilarnab on 6/8/16.
 */
public class LocationFetchedEvent {
    private boolean isChangedLocation;

    public LocationFetchedEvent(boolean isChanged) {
        this.isChangedLocation = isChanged;
    }

    public boolean isChangedLocation() {
        return isChangedLocation;
    }
}
