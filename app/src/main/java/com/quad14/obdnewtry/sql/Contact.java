package com.quad14.obdnewtry.sql;

public class Contact {

    String name;
    String uid;
    String time;
    String time1;

    public Contact(String uid, String name, String number,String number1) {
        this.name = name;
        this.uid = uid;
        this.time = number;
        this.time1 =number1;

    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public String getNumber() {
        return time;

    }

    public String getNumber1() {
        return time1;

    }
}
