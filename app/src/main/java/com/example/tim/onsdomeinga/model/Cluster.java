package com.example.tim.onsdomeinga.model;

import com.example.tim.onsdomeinga.controller.MainActivity;
import com.example.tim.onsdomeinga.interfaces.Nameable;
import com.example.tim.onsdomeinga.interfaces.Switchable;

import java.io.Serializable;
import java.util.ArrayList;

public class Cluster implements Serializable, Nameable, Switchable {

    private DeviceCommunicator dCom;
    private String name;
    private ArrayList<Device> devicesInCluster = new ArrayList<>();
    private boolean switchedOn;
    private boolean activated;

    public Cluster(String name) {
        setdCom(new DeviceCommunicator());
        this.name = name;
//        changeName(name);
    }

    public void addDeviceToCluster(Device device) {
        this.devicesInCluster.add(device);
    }

    public void removeDeviceFromCluster(Device device) {
        this.devicesInCluster.remove(device);
    }

    public void switchOn() {
        System.out.println("Cluster aanzetten");

        for (Device device : devicesInCluster) {
            System.out.println("Device in cluster " + device.getName() + " : " + device.getSwitchedOn());
            if (device.isActivated()) {
                device.setSwitchedOn(true);
            }
        }

        this.switchedOn = true;
    }

    public void switchOff() {
        System.out.println("Cluster uitzetten");

        for (Device device : devicesInCluster) {
            if (device.isActivated()) {
                device.setSwitchedOn(false);
            }
        }

        this.switchedOn = false;
    }

    public void setSwitchedOn(boolean switchedOn) {
        this.switchedOn = switchedOn;
    }

    @Override
    public boolean getSwitchedOn() {
        // TODO Auto-generated method stub
        return switchedOn;
    }

    public String giveClusterContentsAsString() {
        String str = "";

        for (Device device : devicesInCluster) {
            str += device.getName() + ", ";
        }

        return str.substring(0, str.length() - 2);
    }

    public ArrayList<Device> getDevicesInCluster() {
        return devicesInCluster;
    }

    public String getName() {
        return name;
    }

    public boolean isSwitchedOn() {
        return switchedOn;
    }

    public boolean isActivated() {
        return activated;
    }

    public boolean changeName(String name) {

        if (this.name.equals(name)) {
            return true;
        }

        for (Cluster cluster : MainActivity.clusterList) {
            if (name.equals(cluster.getName())) {
                System.out.println("Name already exists!");
                return false;
            }
        }

        if (name.equals("") || name.isEmpty()) {
            this.name = standardName;
        } else if (checkStringLength(name)) {
            this.name = name;
        }

        return true;
    }

    public boolean checkStringLength(String name) {
        return (name.length() <= maxNamelength && !name.isEmpty());
    }

    @Override
    public boolean requestCurrentValue() {

        int switchedonCount = 0;

        for (Device device : devicesInCluster) {
            if (device instanceof DigitalDevice) {
                if (((DigitalDevice) device).getSwitchedOn())
                    switchedonCount++;
            }
        }

        // TODO: hoe is deze methode nu bedoeld?
        // true als tenminste  de helft van de apparaten in de cluster aan staat
        if (switchedonCount > (devicesInCluster.size() / 2)) {
            return true;
        } else {
            return false;
        }
    }

    private void setdCom(DeviceCommunicator dCom) {
        this.dCom = dCom;
    }

    public DeviceCommunicator getdCom() {
        return dCom;
    }
}
