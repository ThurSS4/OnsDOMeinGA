package com.example.tim.onsdomeinga.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class DeviceTypesTest {

    @Test
    public void getDescriptionRead() {
        String read = DeviceTypes.READONLY.getDescription();

        assertEquals(read, "Alleen uitleesbaar");
    }

    @Test
    public void getDescriptionSwitchable() {
        String switchable = DeviceTypes.SWITCHABLE.getDescription();

        assertEquals(switchable, "Schakelbaar");
    }

    @Test
    public void getDescriptionDimmable() {
        String dimmable = DeviceTypes.DIMMABLE.getDescription();
        assertEquals(dimmable, "Dimbaar");
    }

    @Test
    public void checkAmountOfCases() {
        int amountOfCases = DeviceTypes.values().length;
        assertEquals(amountOfCases, 3);
    }
}