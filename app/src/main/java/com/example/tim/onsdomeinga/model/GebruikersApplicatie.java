package com.example.tim.onsdomeinga.model;

import android.content.Context;

import com.example.tim.onsdomeinga.persistance.SerializeHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GebruikersApplicatie {

    private SerializeHandler SerializeHandler = new SerializeHandler();
    private DeviceCommunicator DeviceCommunicator = new DeviceCommunicator(this);
    private List<Device> deviceList = new ArrayList<Device>();
    private List<Cluster> clusterList = new ArrayList<Cluster>();

    public GebruikersApplicatie() {
        DeviceCommunicator.requestConfigFromServer();
    }

    public void loadClusterList(Context context) {
        try {
            setClusterList(SerializeHandler.loadFromSerializedData(context));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public DeviceCommunicator getDcom() {
        return DeviceCommunicator;
    }

    public List<Cluster> getClusterList() {
        return clusterList;
    }

    private void setClusterList(List<Cluster> clusterList) {
        this.clusterList = clusterList;
    }

    public List<Device> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<Device> deviceList) {
        this.deviceList = deviceList;
    }

}
