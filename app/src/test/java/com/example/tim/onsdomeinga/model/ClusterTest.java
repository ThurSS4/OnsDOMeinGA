package com.example.tim.onsdomeinga.model;

import com.example.tim.onsdomeinga.controller.MainActivity;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ClusterTest {

    private Cluster cluster;
    private Device dimmDev = new DimmableDevice("Dimm", 12);

    @Before
    public void Setup(){
        cluster = new Cluster("Cluster 1");
        cluster.addDeviceToCluster(dimmDev);
    }

    @Test
    public void addDeviceToCluster() {
        assertEquals(cluster.getDevicesInCluster().size(), 1);

        cluster.addDeviceToCluster(dimmDev);
        assertEquals(cluster.getDevicesInCluster().size(), 2);
    }

    @Test
    public void removeDeviceFromCluster() {
        cluster.removeDeviceFromCluster(dimmDev);
        assertEquals(cluster.getDevicesInCluster().size(), 0);
    }

    @Test
    public void switchOn() {
        cluster.switchOn();
        assertTrue(cluster.isSwitchedOn());
    }

    @Test
    public void switchOff() {
        cluster.switchOff();
        assertFalse(cluster.isSwitchedOn());
    }

    @Test
    public void giveClusterContentsAsString() {
        assertEquals(cluster.giveClusterContentsAsString(), "Dimm");
    }

    @Test
    public void getDevicesInCluster() {
        ArrayList<Device> list = cluster.getDevicesInCluster();
        assertNotNull(list);
        assertEquals(list.size(), 1);
    }

    @Test
    public void getName() {
        assertEquals(cluster.getName(), "Cluster 1");
    }

    @Test
    public void changeNameWrong() {
        cluster.changeName("Changed Cluster With a False Name");
        assertEquals(cluster.getName(), "Cluster 1");
    }

    @Test
    public void changeNameCorrect() {
        cluster.changeName("Changed Cluster");
        assertEquals(cluster.getName(), "Changed Cluster");
    }

    @Test
    public void changeNameStandard() {
        cluster.changeName("");
        assertEquals(cluster.getName(), "");
    }

    @Test
    public void changeNameToSameName() {
        assertTrue(cluster.changeName(cluster.getName()));
    }

    @Test
    public void changeNameToExistingName() {
        MainActivity.clusterList.add(new Cluster("Test"));
        assertTrue(MainActivity.clusterList.size() > 0);

        assertFalse(cluster.changeName(MainActivity.clusterList.get(0).getName()));
    }

    @Test
    public void setSwitchedOff() {
        cluster.setSwitchedOn(false);
        assertFalse(cluster.isSwitchedOn());
    }

    @Test
    public void setSwitchedOn() {
        cluster.setSwitchedOn(true);
        assertTrue(cluster.isSwitchedOn());
    }

    // The method requestCurrentValue() has been altered to return void
    @Test
    public void giveCurrentValue() {
        Device dimmDev2 = new DimmableDevice("Dimm2", 13);
        Device dimmDev3 = new DimmableDevice("Dimm3", 14);

        cluster.addDeviceToCluster(dimmDev2);
        cluster.addDeviceToCluster(dimmDev3);

        // 2 van de 3 devices staan aan, is meer dan de helft.
        cluster.getDevicesInCluster().get(0).setSwitchedOn(true);
        cluster.getDevicesInCluster().get(1).setSwitchedOn(true);
        cluster.getDevicesInCluster().get(2).setSwitchedOn(false);
        assertTrue(cluster.requestCurrentValue());

        // 1 van de 3 devices staat aan, is minder dan de helft.
        cluster.getDevicesInCluster().get(1).setSwitchedOn(false);
        assertFalse(cluster.requestCurrentValue());
    }

    @Test
    public void isActivated() {
        // is altijd false want wordt niet gebruikt
        assertFalse(cluster.isActivated());
    }

    @Test
    public void getDCom() {
        DeviceCommunicator devCom = null;
        assertNull(devCom);
        devCom = cluster.getdCom();
        assertNotNull(devCom);
    }
}