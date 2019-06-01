package com.example.tim.onsdomeinga.interfaces;

public interface PortHandler {

    int minPort = 0;
    int maxPort = 64;
    int standardPort = 0;

    boolean changePort(int port);
    boolean validatePort(int port);
}
