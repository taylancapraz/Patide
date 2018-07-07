package com.wrexsoft.canturgut.patide.user;

/**
 * Created by CanTurgut on 23/05/2017.
 */

class UserData {
    private static final UserData userInstance = new UserData();

    private String userUID;

    static UserData getInstance() {
        return userInstance;
    }
    private UserData() {
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }
}
