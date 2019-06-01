package com.example.tim.onsdomeinga.model;

import com.example.tim.onsdomeinga.interfaces.Dimmable;

import java.io.Serializable;

public class DimmableDevice extends DigitalDevice implements Dimmable, Serializable {

    private int dimValue = 100;

    public DimmableDevice(String name, int port) {
        super(name, port);
    }

    public DimmableDevice(String naam, int pin, boolean switchedOn, boolean active) {
        super(naam, pin, switchedOn, active);
    }

    @Override
    public void setDimValue(int newValue) {
        if(super.getdCom().deviceAction(this, "alter"))
        {
            this.dimValue = newValue;
        }
    }

    @Override
    public int getDimValue() {
        return this.dimValue;
    }
}
