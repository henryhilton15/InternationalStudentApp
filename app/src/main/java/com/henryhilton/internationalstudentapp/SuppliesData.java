package com.henryhilton.internationalstudentapp;

/**
 * Created by henryhilton on 12/11/17.
 */

public class SuppliesData {

    Boolean pillow;
    Boolean sheets;
    Boolean blanket;
    Boolean shampoo;

    public SuppliesData(){
    }

    public Boolean getPillow() {
        return pillow;
    }

    public Boolean getSheets() {
        return sheets;
    }

    public Boolean getBlanket() {
        return blanket;
    }

    public Boolean getShampoo() {
        return shampoo;
    }

    public void setPillow(Boolean pillow) {
        this.pillow = pillow;
    }

    public void setBlanket(Boolean blanket) {
        this.blanket = blanket;
    }

    public void setSheets(Boolean sheets) {
        this.sheets = sheets;
    }

    public void setShampoo(Boolean shampoo) {
        this.shampoo = shampoo;
    }
}
