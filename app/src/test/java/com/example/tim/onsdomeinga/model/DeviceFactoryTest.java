package com.example.tim.onsdomeinga.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class DeviceFactoryTest {

    private DeviceFactory devFactory = new DeviceFactory();

    @Test
    public void getReadableDevice() {
        Device readable = devFactory.getDevice("Alleen uitleesbaar", "Read", 12, true);
        assertTrue(readable instanceof ReadableDevice);
    }

    @Test
    public void getSwitchableDevice() {
        Device switchable = devFactory.getDevice("Schakelbaar", "Switch", 14, false);
        assertTrue(switchable instanceof SwitchableDevice);
    }

    @Test
    public void getDimmableDevice() {
        Device dimmable = devFactory.getDevice("Dimbaar", "Dimm", 16, false);
        assertTrue(dimmable instanceof DimmableDevice);
    }
}