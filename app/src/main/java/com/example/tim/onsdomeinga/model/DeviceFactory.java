package com.example.tim.onsdomeinga.model;

public class DeviceFactory {

    public Device getDevice(String devicetype, String name, int port, boolean analoog) {

        if (devicetype.equals(DeviceTypes.READONLY.getDescription())) {
            return new ReadableDevice(name, port);

        } else if (devicetype.equals(DeviceTypes.SWITCHABLE.getDescription())) {
            return new SwitchableDevice(name, port);

        } else if (devicetype.equals(DeviceTypes.DIMMABLE.getDescription())) {
            return new DimmableDevice(name, port);

        }

        return new ReadableDevice(name, port);
    }

    public Device getDevice(String[] tempArray2) {

        String type = tempArray2[0];
        String naam = tempArray2[1];
        int pin = Integer.parseInt(tempArray2[2]);
        boolean switchedOn = tempArray2[3].equals("true");
        boolean active = tempArray2[4].equals("true");

        if (type.equals("DimmableDevice")) {
            return new DimmableDevice(naam, pin, switchedOn, active);
        } else if (type.equals("SwitchableDevice")) {
            return new SwitchableDevice(naam, pin, switchedOn,active);
        }

        return new ReadableDevice(naam, pin);
    }
}
