package com.quad14.obdnewtry;

public class CustomDataListModel {

    String Date;
    String Time;
    Integer Km;
    Integer DfKm;

    public CustomDataListModel(String date, String time, Integer km, Integer dfKm) {
        Date = date;
        Time = time;
        Km = km;
        DfKm = dfKm;
    }

    public CustomDataListModel(String date, String time, Integer km) {
        Date = date;
        Time = time;
        Km = km;
    }

    public CustomDataListModel(Integer dfKm) {
        DfKm = dfKm;
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

    public Integer getKm() {
        return Km;
    }

    public void setKm(Integer km) {
        Km = km;
    }

    public Integer getDfKm() {
        return DfKm;
    }

    public void setDfKm(Integer dfKm) {
        DfKm = dfKm;
    }
}
