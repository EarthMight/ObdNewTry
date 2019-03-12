package com.quad14.obdnewtry;

public class DIfferKmModel {

    Integer ID;
    Integer DIFFERKM;

    public DIfferKmModel(Integer ID, Integer DIFFERKM) {
        this.ID = ID;
        this.DIFFERKM = DIFFERKM;
    }

    public DIfferKmModel(Integer DIFFERKM) {
        this.DIFFERKM = DIFFERKM;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getDIFFERKM() {
        return DIFFERKM;
    }

    public void setDIFFERKM(Integer DIFFERKM) {
        this.DIFFERKM = DIFFERKM;
    }
}
