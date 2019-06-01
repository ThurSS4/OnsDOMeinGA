package com.example.tim.onsdomeinga.model;

import java.io.Serializable;

public class SwitchableDevice extends DigitalDevice implements Serializable {

    public SwitchableDevice(String name, int port) {
        super(name, port);
    }

    public SwitchableDevice(String naam, int pin, boolean switchedOn, boolean active) {
        super(naam, pin, switchedOn, active);
    }
}
