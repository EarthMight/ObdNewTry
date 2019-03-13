package com.quad14.obdnewtry.activity;

public class KmDataModel {

    String Id;
    Integer Km;
    Integer DffKm;
    String Date;
    String Time;
    String Rpm;
    String Speed;
    String Runtime;


    public KmDataModel(Integer km, Integer dffKm, String date, String time) {
        Km = km;
        DffKm = dffKm;
        Date = date;
        Time = time;
    }

    public KmDataModel(String id, Integer km, Integer dffKm, String date, String time, String rpm, String speed, String runtime) {
        Id = id;
        Km = km;
        DffKm = dffKm;
        Date = date;
        Time = time;
        Rpm = rpm;
        Speed = speed;
        Runtime = runtime;
    }


    public KmDataModel(Integer km, Integer dffKm, String date, String time, String rpm, String speed, String runtime) {
        Km = km;
        DffKm = dffKm;
        Date = date;
        Time = time;
        Rpm = rpm;
        Speed = speed;
        Runtime = runtime;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public Integer getKm() {
        return Km;
    }

    public void setKm(Integer km) {
        Km = km;
    }

    public Integer getDffKm() {
        return DffKm;
    }

    public void setDffKm(Integer dffKm) {
        DffKm = dffKm;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getRpm() {
        return Rpm;
    }

    public void setRpm(String rpm) {
        Rpm = rpm;
    }

    public String getSpeed() {
        return Speed;
    }

    public void setSpeed(String speed) {
        Speed = speed;
    }

    public String getRuntime() {
        return Runtime;
    }

    public void setRuntime(String runtime) {
        Runtime = runtime;
    }
}


