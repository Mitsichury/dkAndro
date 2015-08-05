package com.example.mitsichury.firstsqldatabase;

/**
 * Created by MITSICHURY on 05/08/2015.
 */
public class Num {
    private int id;
    private int value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public Num(int id, int value) {

        this.id = id;
        this.value = value;
    }

    @Override
    public String toString() {
        return "ID : "+this.id+" Num : "+this.value;
    }
}
