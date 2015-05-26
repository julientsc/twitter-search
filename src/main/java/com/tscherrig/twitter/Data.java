package com.tscherrig.twitter;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Julien on 26.05.15.
 */
public class Data {

    private Long user;

    private HashMap<Long, TwitterUser> twitterUser = new HashMap<Long, TwitterUser>();

    public HashMap<Long, TwitterUser> getTwitterUser() {
        return twitterUser;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }
}
