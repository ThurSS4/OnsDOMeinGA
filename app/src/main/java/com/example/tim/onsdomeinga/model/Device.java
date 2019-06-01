package com.example.tim.onsdomeinga.model;

import com.example.tim.onsdomeinga.controller.MainActivity;
import com.example.tim.onsdomeinga.interfaces.Nameable;
import com.example.tim.onsdomeinga.interfaces.PortHandler;

import java.io.Serializable;

public abstract class Device implements Nameable, PortHandler, Serializable {

    private DeviceCommunicator dCom;
    private String name;
    private int port;
    private int value;
    boolean switchedOn;
    private boolean activated;

    public Device(String name, int port) {

        setdCom(new DeviceCommunicator());
        changePort(port);
//        changeName(name);
        this.name = name;
        switchedOn = true;
        activated = true;
    }

    public Device(String name, int port, boolean on, boolean active) {

        setdCom(new DeviceCommunicator());
        changePort(port);
//        changeName(name);
        this.name = name;
        this.switchedOn = on;
        this.activated = active;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    void setValue(int value){
        this.value = value;
    }

    public boolean changePort(int port) {

        if (validatePort(port)) {
            this.port = port;
            return true;
        }

        return false;
    }

    public boolean validatePort(int port) {
        return (port < maxPort && port > minPort);
    }

    public boolean changeName(String name) {

        if (this.name.equals(name)) {
            return true;
        }

        for (Device dev : MainActivity.deviceList) {
            if (name.equals(dev.getName())) {
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

    public String getName() {
        return this.name;
    }

    public int getPort() {
        return this.port;
    }

    public int getValue(){
        return this.value;
    }

    public boolean requestCurrentValue() {
        return getdCom().deviceAction(this, "read");
    }

    public void setSwitchedOn(boolean b) {
        if (getdCom().deviceAction(this, "switch")) {
            this.switchedOn = b;
        }
    }

    public boolean getSwitchedOn() {

        return this.switchedOn;
    }

    DeviceCommunicator getdCom() {
        return dCom;
    }

    private void setdCom(DeviceCommunicator dCom) {
        this.dCom = dCom;
    }
}
