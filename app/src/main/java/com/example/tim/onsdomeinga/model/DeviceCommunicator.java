package com.example.tim.onsdomeinga.model;

import android.os.StrictMode;

import com.example.tim.onsdomeinga.interfaces.ArduinoProtocol;
import com.example.tim.onsdomeinga.interfaces.ConfigProtocol;
import com.example.tim.onsdomeinga.interfaces.Dimmable;
import com.example.tim.onsdomeinga.proxy.OnsProxy;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class DeviceCommunicator implements Serializable, ArduinoProtocol, ConfigProtocol {

    transient private DeviceFactory dfac = new DeviceFactory();
    transient private OnsProxy proxy;
    transient private GebruikersApplicatie ga;

    private String configMsg;
    private String requestFromId = "1234";
    private String requestForId = "5678";

    DeviceCommunicator() {
        this.proxy = new OnsProxy();
    }

    DeviceCommunicator(GebruikersApplicatie gebruikersApplicatie) {
        this.ga = gebruikersApplicatie;
        this.proxy = new OnsProxy();
    }

    public void setProxy() {
        this.proxy = new OnsProxy();
    }

    public void requestConfigFromServer() {
        if (!connectToServer(requestFromId)) { return; }

        String uncheckedResponse = proxy.sendRequest("getConfig", requestFromId, requestForId, " ");
        String checkedResponse = handleConfigResponse(uncheckedResponse);
        String[] tempArray = checkedResponse.split(INTERSECTION);

        if (!isValidResponse(tempArray)) { return; }

        ga.getDeviceList().clear();
        getDevicesFromString(tempArray);
        proxy.closeConnection();
    }

    private boolean isValidResponse(String[] tempArray) {

        if (tempArray[0].contentEquals(EMPTY_RESPONSE) || tempArray[0].contentEquals("message") || tempArray[0].contentEquals("null")) {
            return false;
        } else {
            return true;
        }

    }

    private String handleConfigResponse(String response) {
        response = response.replace(STR_START, "");
        response = response.replace(STR_STOP, "");

        return response;
    }

    private void getDevicesFromString(String[] tempArray) {

        for (String s : tempArray) {
            String[] tempArray2 = s.split(SPACER);

//            for (String str : tempArray2) {
//                System.out.println("str = " + str);
//            }

            Device d = dfac.getDevice(tempArray2);
            ga.getDeviceList().add(d);

        }
    }

    public boolean pushConfigToServer() {
        ArrayList<Device> devices = (ArrayList<Device>) ga.getDeviceList(); // lijst van devices vanuit
        // GebruikersApplicatie ophalen
        configMsg = OBJECT_START + generateConfigProtocolFromList(devices) + OBJECT_END; // er een string van maken
        String response = toServer("setConfig", configMsg);
        return response.equals("setConfigOK"); // true bij gewenste / verwachte response, anders false.
    }

    private String generateConfigProtocolFromList(ArrayList<Device> devices) {
        StringBuilder result = new StringBuilder();
        for (Device d : devices) {
            String type = d.getClass().getSimpleName();
            String name = d.getName();
            int port = d.getPort();
            boolean isOn = d.getSwitchedOn();
            boolean isActive = d.isActivated();
            result.append(MSG_START).append(type).append(SPACER).append(name).append(SPACER).append(port).append(SPACER)
                    .append(isOn).append(SPACER).append(isActive).append(MSG_STOP);

        }

        return result.toString();
    }

    boolean deviceAction(Device device, String action){
        String cmd = "setHc";
        int whichPin = device.getPort();
        int whichAction = 0;
        int whichValue = 0;
        boolean updateflag = false; // bepaalt of de waarde wel of niet binnen het device wordt aangepast


        if(action.equals("switch")){
            whichAction = ArduinoRequests.switchPin.num;
            System.out.println("The device is on = " + device.getSwitchedOn());
            whichValue = device.getSwitchedOn() ? 1 : 0; // van boolean naar int tbv protocol
        }

        if (action.equals("alter")){
            whichAction = ArduinoRequests.setValue.num;
            whichValue = ((Dimmable) device).getDimValue();
        }

        if(action.equals("read")){
            updateflag = true;
            cmd = "getHc";
            whichAction = ArduinoRequests.getStatus.num;
        }

        int[] message = new int[] { whichPin, whichAction, whichValue };
        System.out.println("DevCom, message to send. Pin: " + message[0] + " Action: " + message[1] + " Value: " + message[2]);
        return sendmessage(cmd, message, updateflag, device);

    }

    private boolean sendmessage(String action, int[] message, boolean updateflag, Device d) {
        int whichPin = message[PIN];
        int whichAction = message[ACTION];
        int whichValue = message[VALUE];

        String msg = ARD_BOM + whichPin + ARD_DIVIDER + whichAction + ARD_DIVIDER + whichValue + ARD_EOM;

        String response = toServer(action, msg);

        if (response.equals("No connection with HC")) {
            return false;
        }

        if (updateflag) {
            // System.out.println("Waarde updaten!");
            String ardValues = response.substring(response.indexOf(ARD_BOM) + 1, response.indexOf(ARD_EOM));
            String[] values = ardValues.split(",");
            String rcPin = values[0];
            int rcValue = Integer.parseInt(values[1]);
            if (!rcPin.equals(Integer.toString(d.getPort())))	{
                //als de poort die je terugkrijgt niet de verwachte poort is gaat er iets helemaal mis, eventueel nog exception voor maken / gooien
                return false;
            }

            //niet de mooiste manier maar voor nu een manier die werkt
            boolean e = false;
            d.setValue(rcValue);
            if (rcValue == 0) {
                e = true;
            }
            if (rcValue == 1) {
                e = false;
            }

            d.switchedOn = e;
        }

        return true;
    }


    private String toServer(String cmd, String msg) {
        connectToServer(requestFromId);
        System.out.println("To server: 	" + cmd + ";" + requestFromId + ";" + requestForId + ";" + msg);
        String resp = proxy.sendRequest(cmd, requestFromId, requestForId, msg);
        System.out.println("Response: 	" + resp);
        proxy.closeConnection();
        return resp;
    }

    private boolean connectToServer(String requestFromId) {
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {

            // TODO: Absoluut niet de beste methode (Networking op de Main/UI-thread) maar hij werkt voor nu.
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        try {
            if (proxy == null) {
                this.proxy = new OnsProxy();
            }

            proxy.connectClientToServer(requestFromId);
        } catch (Exception e) {
            System.out.println("Failed to connect.");
            return false;

        }

        return true;
    }
}