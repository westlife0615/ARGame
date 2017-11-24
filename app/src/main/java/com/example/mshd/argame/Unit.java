package com.example.mshd.argame;

/**
 * Created by mshd on 2016-11-16.
 */

public class Unit {
    private int x;
    private int y;
    private int visible_time =0;
    private String addr, name;
    private double lat, lon;

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public int getVisible_time() {
        return visible_time;
    }

    public void setVisible_time(int visible_time) {
        this.visible_time = visible_time;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
