package com.example.tim.onsdomeinga.model;

public enum DeviceTypes {

    READONLY("Alleen uitleesbaar"),
    SWITCHABLE("Schakelbaar"),
    DIMMABLE("Dimbaar");

    private final String description;

    DeviceTypes(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String[] getOptionsAsArray() {
        String[] listOfOptions = new String[]{READONLY.toString(), SWITCHABLE.toString(), DIMMABLE.toString()};

        System.out.println("lop = " + listOfOptions);
        return listOfOptions;
    }
}
