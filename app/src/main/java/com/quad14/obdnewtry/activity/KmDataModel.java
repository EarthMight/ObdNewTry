package com.quad14.obdnewtry.activity;

public class KmDataModel {

    String Id;
    Integer Km;
    Integer DffKm;
    String Date;
    String Time;

    public KmDataModel(String id, Integer km, Integer dffKm, String date, String time) {
        Id = id;
        Km = km;
        DffKm = dffKm;
        Date = date;
        Time = time;
    }

    public KmDataModel(Integer km, Integer dffKm, String date, String time) {
        Km = km;
        DffKm = dffKm;
        Date = date;
        Time = time;
    }

    public KmDataModel(Integer km, String date, String time) {
        Km = km;
        Date = date;
        Time = time;
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
}


