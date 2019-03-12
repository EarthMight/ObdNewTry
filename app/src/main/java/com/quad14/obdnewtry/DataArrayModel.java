package com.quad14.obdnewtry;

public class DataArrayModel {
    String Id;
    String TotalKm;
    String Date;

    public DataArrayModel(String id, String totalKm, String date) {
        Id = id;
        TotalKm = totalKm;
        Date = date;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTotalKm() {
        return TotalKm;
    }

    public void setTotalKm(String totalKm) {
        TotalKm = totalKm;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
