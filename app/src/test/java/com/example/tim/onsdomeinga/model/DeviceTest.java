package com.example.tim.onsdomeinga.model;

import com.example.tim.onsdomeinga.controller.MainActivity;

import org.junit.Test;

import static org.junit.Assert.*;

public class DeviceTest {

    // Creating multiple devices checks their constructors and covers that code
    private Device readDev = new ReadableDevice("Read", 11);
    private Device advReadDev = new ReadableDevice("AdvRead", 13, true, true);
    private Device switchDev = new SwitchableDevice("Switch", 34);
    private Device advancedDev = new SwitchableDevice("Adv", 9, true, true);

    @Test
    public void setSwitchedOn() {
        advancedDev.setSwitchedOn(false);
        assertFalse(advancedDev.getSwitchedOn());

        advancedDev.setSwitchedOn(true);
        assertTrue(advancedDev.getSwitchedOn());
    }

    @Test
    public void setNotActivated() {
        readDev.setActivated(false);
        assertFalse(readDev.isActivated());
    }

    @Test
    public void setIsActivated() {
        readDev.setActivated(true);
        assertTrue(readDev.isActivated());
    }

    @Test
    public void validatePortWrong() {
        assertFalse(switchDev.validatePort(124));
    }

    @Test
    public void validatePortCorrect() {
        assertTrue(switchDev.validatePort(16));
    }

    @Test
    public void changePortWrong() {
        assertEquals(readDev.getPort(), 11);
        assertFalse(readDev.changePort(124));
    }

    @Test
    public void changePortCorrect() {
        assertTrue(readDev.changePort(16));
    }

    @Test
    public void changeNameWrong() {
        readDev.changeName("Write123456789Read123456789");
        assertEquals(readDev.getName(), "Read");
    }

    @Test
    public void changeNameCorrect() {
        readDev.changeName("Name changed");
        assertEquals(readDev.getName(), "Name changed");
    }

    @Test
    public void changeNameToExistingName() {
        assertTrue(MainActivity.deviceList.size() > 0);

        assertFalse(advReadDev.changeName(MainActivity.deviceList.get(0).getName()));
    }

    @Test
    public void changeNameStandard() {
        readDev.changeName("");
        assertEquals(readDev.getName(), "");
    }

    @Test
    public void changeNameToSameName() {
        assertTrue(readDev.changeName(readDev.getName()));
    }

    @Test
    public void requestCurrentValue() {
        assertTrue(readDev.requestCurrentValue());
    }

    @Test
    public void setValue() {
        readDev.setValue(88);
        assertEquals(readDev.getValue(), 88);
    }
}