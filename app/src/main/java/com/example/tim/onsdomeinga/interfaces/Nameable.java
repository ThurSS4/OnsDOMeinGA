package com.example.tim.onsdomeinga.interfaces;

public interface Nameable {

    int maxNamelength = 21;
    String standardName = "";

    boolean changeName(String name);
    boolean checkStringLength(String name);
}
