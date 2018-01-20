package com.github.novskey.novabot.data;

/**
 * Created by Paris on 17/01/2018.
 */
public class User {

    boolean paused = false;
    String userID;

    public User(String userID) {
        this.userID = userID;
    }

    public User(String id, boolean paused) {
        userID = id;
        this.paused = paused;
    }
}
