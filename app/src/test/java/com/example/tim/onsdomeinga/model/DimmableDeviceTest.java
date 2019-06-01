package com.example.tim.onsdomeinga.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class DimmableDeviceTest {

    private DimmableDevice dimmDev = new DimmableDevice("Dimm", 6);

    @Test
    public void setDimValue() {
        dimmDev.setDimValue(50);
        assertEquals(dimmDev.getDimValue(), 50);
    }

    @Test
    public void getName() {
        assertEquals(dimmDev.getName(), "Dimm");
    }
}