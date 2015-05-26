package com.tscherrig.twitter;

import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Julien on 26.05.15.
 */
public class TwitterUser {
    private Long id;

    public TwitterUser(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    private Long lastFollowersUpdate = null;
    private ArrayList<Long> followers = new ArrayList<Long>();
    public void fillFollowers(Twitter twitter, Data data) throws TwitterException {
        System.out.println("Search followers for account : " + id);
        long lCursor = -1;
        IDs friendsIDs = twitter.getFriendsIDs(id, lCursor);
        ArrayList<Long> followers  = new ArrayList<Long>();
        int j = 0;
        do
        {
            for (long i : friendsIDs.getIDs())
            {
                if(!this.followers.contains(i))
                    followers.add(i);
                else
                    break;
                if(!data.getTwitterUser().containsKey(i))
                    data.getTwitterUser().put(i, new TwitterUser(i));

                System.out.println(" + Add follower : " + i);
            }
            if(j++==6)
                break;
        }while(friendsIDs.hasNext());
        this.followers.addAll(followers);
        lastFollowersUpdate = new Date().getTime();
    }

    private Long lastFriendsUpdate = null;
    private ArrayList<Long> friends = new ArrayList<Long>();
    public void fillFriends(Twitter twitter, Data data) throws TwitterException {
        System.out.println("Search friends for account : " + id);
        long lCursor = -1;
        IDs friendsIDs = twitter.getFriendsIDs(id, lCursor);
        ArrayList<Long> friends  = new ArrayList<Long>();
        int j = 0;
        do
        {
            for (long i : friendsIDs.getIDs())
            {
                if(!this.friends.contains(i))
                    friends.add(i);
                else
                    break;
                if(!data.getTwitterUser().containsKey(i))
                    data.getTwitterUser().put(i, new TwitterUser(i));

                System.out.println(" + Add friend : " + i);
            }
            if(j++==6)
                break;
        }while(friendsIDs.hasNext());
        this.friends.addAll(friends);
        lastFriendsUpdate = new Date().getTime();
    }

    public Long getLastFollowersUpdate() {
        return lastFollowersUpdate;
    }

    public Long getLastFriendsUpdate() {
        return lastFriendsUpdate;
    }

    public ArrayList<Long> getFollowers() {
        return followers;
    }

    public ArrayList<Long> getFriends() {
        return friends;
    }
}
