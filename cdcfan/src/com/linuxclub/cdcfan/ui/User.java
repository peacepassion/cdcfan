package com.linuxclub.cdcfan.ui;

/**
 * Created by peace_da on 2015/4/15.
 */
public class User {
    public String psid;
    public String name;
    public String depcode;

    @Override
    public String toString() {
        return "psid: " + psid + ", name: " + name + ", depcode: " + depcode;
    }
}
