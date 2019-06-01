package com.example.tim.onsdomeinga.model;

import java.io.Serializable;

public class ReadableDevice extends AnalogDevice implements Serializable {

    public ReadableDevice(String name, int port) {
        super(name, port);
    }

    public ReadableDevice(String naam, int pin, boolean on, boolean active) {
        super(naam, pin, on, active);
    }
}
