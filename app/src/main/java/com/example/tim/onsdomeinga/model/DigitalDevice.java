package com.example.tim.onsdomeinga.model;

import java.io.Serializable;

public abstract class DigitalDevice extends Device implements Serializable {

    public DigitalDevice(String name, int port) {
        super(name, port);
    }

    public DigitalDevice(String naam, int pin, boolean switchedOn, boolean active) {
        super(naam, pin, switchedOn, active);
    }
}
