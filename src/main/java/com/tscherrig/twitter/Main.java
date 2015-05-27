package com.tscherrig.twitter;

import com.tscherrig.twitter.config.Config;
import com.tscherrig.twitter.data.Data;
import com.tscherrig.twitter.model.FileAccess;
import com.tscherrig.twitter.model.TwitterUser;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Date;

public class Main {

    public static void main(String[] args) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        Config.setConfig(cb);
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();

        Data data = FileAccess.getInstance().getData();

        int interval = 1000 * 60 * 60 * 24 * 7;

        // Search current user
        System.out.println(">>>> Starting");
        try {
            Long myId = twitter.getId();
            System.out.println("User to analyse : " + myId);

            System.out.print(" Update user info : ");
            if(data.getUser() == null) {
                System.out.println(myId);
                data.setUser(myId);

                if(!data.getTwitterUser().containsKey(myId)) {
                    TwitterUser twitterUser = new TwitterUser(myId);
                    data.getTwitterUser().put(myId, twitterUser);
                }
                FileAccess.save();
                System.out.println("ok");
            } else {
                System.out.println("nothing to update");
            }
        } catch (TwitterException e) {
            System.err.println(e.getMessage());
        }

        // Fill user N
        System.out.println("\n\n>>>> Fill basic user info");
        try {
            TwitterUser twitterUser = data.getTwitterUser().get(data.getUser());

            System.out.println(" Update followers ?");
            if (twitterUser.getLastFollowersUpdate() == null || twitterUser.getLastFollowersUpdate() + interval < new Date().getTime()) {
                twitterUser.fillFollowers(twitter, data);
                FileAccess.save();
            } else
                System.out.println(" -> nothing to update");


            System.out.println(" Update friends ?");
            if (twitterUser.getLastFriendsUpdate() == null || twitterUser.getLastFriendsUpdate() + interval < new Date().getTime()) {
                twitterUser.fillFriends(twitter, data);
                FileAccess.save();
            } else
                System.out.println(" -> nothing to update");

        } catch (TwitterException e) {
            System.err.println(e.getMessage());
        }

        while (true) {
        // Fill user N + 1 -> from friends
        System.out.println("\n\n>>>> Fill user info");
        try {
            for (Long key : data.getTwitterUser().get(data.getUser()).getFriends()) {
                TwitterUser twitterUser = data.getTwitterUser().get(key);
                boolean hasChange = false;

                System.out.println("Processing for user : " + key);
                System.out.println(" Update followers ?");
                if (twitterUser.getLastFollowersUpdate() == null || twitterUser.getLastFollowersUpdate() + interval < new Date().getTime()) {
                    twitterUser.fillFollowers(twitter, data);
                    hasChange = true;
                } else
                    System.out.println(" -> nothing to update");

                System.out.println(" Update friends ?");
                if (twitterUser.getLastFriendsUpdate() == null || twitterUser.getLastFriendsUpdate() + interval < new Date().getTime()) {
                    twitterUser.fillFriends(twitter, data);
                    hasChange = true;
                } else
                    System.out.println(" -> nothing to update");

                if (hasChange)
                    FileAccess.save();
            }
        } catch (TwitterException e) {
            System.err.println(e.getMessage());
        }

            for (int i = 0; i < 15; i++) {
                System.out.println("... next exection in " + (15 - i) + " minute(s)");
                try {
                    Thread.sleep(1000 * 60);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
/*

        while(true) {
            System.out.println("\n\n>>>> Search N+1 users info");

            // Fill user N + 1 -> from followers
            try {
                for (Long key : data.getTwitterUser().get(data.getUser()).getFollowers()) {
                    TwitterUser twitterUser = data.getTwitterUser().get(key);
                    boolean hasChange = false;
                    if (twitterUser.getLastFollowersUpdate() == null || twitterUser.getLastFollowersUpdate() + interval < new Date().getTime()) {
                        twitterUser.fillFollowers(twitter, data);
                        hasChange = true;
                    }

                    if (twitterUser.getLastFriendsUpdate() == null || twitterUser.getLastFriendsUpdate() + interval < new Date().getTime()) {
                        twitterUser.fillFriends(twitter, data);
                        hasChange = true;
                    }
                    if(hasChange)
                        FileAccess.save();
                }
            } catch (TwitterException e) {
                System.err.println(e.getMessage());
            }

            try {
                Thread.sleep(1000 * 60 * 5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        */
    }
}
