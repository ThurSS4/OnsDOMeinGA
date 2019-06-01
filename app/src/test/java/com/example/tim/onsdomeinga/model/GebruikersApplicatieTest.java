package com.example.tim.onsdomeinga.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class GebruikersApplicatieTest {

    private GebruikersApplicatie ga = new GebruikersApplicatie();

    @Test
    public void loadClusterList() {
        // TODO: Not testable yet because of the need for 'Context'
    }

    @Test
    public void getDcom() {
        DeviceCommunicator devCom = null;
        assertNull(devCom);

        devCom = ga.getDcom();
        assertNotNull(devCom);
    }

    @Test
    public void getClusterList() {
        List<Cluster> tempList = null;
        assertNull(tempList);

        tempList = ga.getClusterList();
        assertNotNull(tempList);

        assertEquals(tempList.size(), ga.getClusterList().size());
    }

    @Test
    public void getDeviceList() {
        List<Device> tempList = null;
        assertNull(tempList);

        tempList = ga.getDeviceList();
        assertNotNull(tempList);

        assertEquals(tempList.size(), ga.getDeviceList().size());
    }

    @Test
    public void setDeviceList() {
        assertNotNull(ga.getDeviceList());

        ga.setDeviceList(new ArrayList<Device>());
        assertEquals(ga.getDeviceList().size(), 0);
    }
}