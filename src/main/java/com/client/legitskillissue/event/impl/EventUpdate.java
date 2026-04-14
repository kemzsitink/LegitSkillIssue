package com.client.legitskillissue.event.impl;

import com.client.legitskillissue.event.Event;

public class EventUpdate extends Event {
    public boolean isPre;

    public EventUpdate(boolean isPre) {
        this.isPre = isPre;
    }
}
