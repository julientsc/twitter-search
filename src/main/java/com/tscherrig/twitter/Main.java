package com.tscherrig.twitter;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Date;

public class Main {

    public static void main(String[] args) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("PBwpBVcMe2a5EU2SxZZbUTYC5")
                .setOAuthConsumerSecret("mhdJ0EhJzWfSxqwSQPRFnijiocvQUgJ2Bo9vB2m6V5toragjBE");
        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();

        Data data = Procesing.getInstance().getData();

        int interval = 1000 * 60 * 60 * 24;

        // Serach current user
        try {
            if(data.getUser() == null) {
                Long myId = twitter.getId();
                System.out.println(myId);
                data.setUser(myId);

                if(!data.getTwitterUser().containsKey(myId)) {
                    TwitterUser twitterUser = new TwitterUser(myId);
                    data.getTwitterUser().put(myId, twitterUser);
                }
            }
            Procesing.save();
        } catch (TwitterException e) {
            e.printStackTrace();
        }

        // Fill user N
        try {
            TwitterUser twitterUser = data.getTwitterUser().get(data.getUser());


            if(twitterUser.getLastFollowersUpdate() == null || twitterUser.getLastFollowersUpdate() + interval < new Date().getTime())
                twitterUser.fillFollowers(twitter, data);

            if(twitterUser.getLastFriendsUpdate() == null || twitterUser.getLastFriendsUpdate() + interval < new Date().getTime())
                twitterUser.fillFriends(twitter, data);

            Procesing.save();
        } catch (TwitterException e) {
            e.printStackTrace();
        }

        // Fill user N + 1 -> from friends
        try {
            for(Long key : data.getTwitterUser().get(data.getUser()).getFriends()) {
                TwitterUser twitterUser = data.getTwitterUser().get(key);
                if(twitterUser.getLastFollowersUpdate() == null || twitterUser.getLastFollowersUpdate() + interval < new Date().getTime())
                    twitterUser.fillFollowers(twitter, data);

                if(twitterUser.getLastFriendsUpdate() == null || twitterUser.getLastFriendsUpdate() + interval < new Date().getTime())
                    twitterUser.fillFriends(twitter, data);
                Procesing.save();
            }
        } catch (TwitterException e) {
            e.printStackTrace();
        }
        while(true) {

            // Fill user N + 1 -> from followers
            try {
                for (Long key : data.getTwitterUser().get(data.getUser()).getFollowers()) {
                    TwitterUser twitterUser = data.getTwitterUser().get(key);
                    if (twitterUser.getLastFollowersUpdate() == null || twitterUser.getLastFollowersUpdate() + interval < new Date().getTime())
                        twitterUser.fillFollowers(twitter, data);

                    if (twitterUser.getLastFriendsUpdate() == null || twitterUser.getLastFriendsUpdate() + interval < new Date().getTime())
                        twitterUser.fillFriends(twitter, data);
                    Procesing.save();
                }
            } catch (TwitterException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(1000 * 60 * 5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
