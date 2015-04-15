package com.example.cdcfan;

/**
 * Created by peace_da on 2015/4/15.
 */
public class User {
    String psid;
    String name;
    String depcode;

    @Override
    public String toString() {
        return "psid: " + psid + ", name: " + name + ", depcode: " + depcode;
    }
}
