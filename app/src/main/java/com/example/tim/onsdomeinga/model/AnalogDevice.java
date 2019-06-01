package com.example.tim.onsdomeinga.model;

import java.io.Serializable;

public abstract class AnalogDevice extends Device implements Serializable {

    public AnalogDevice(String name, int port) {
        super(name, port);
    }

    public AnalogDevice(String naam, int pin, boolean on, boolean active) {
        super(naam, pin, on, active);
    }
}
