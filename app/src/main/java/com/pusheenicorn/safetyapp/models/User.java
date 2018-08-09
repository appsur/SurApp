package com.pusheenicorn.safetyapp.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("_User")
public class User extends ParseUser {

    private final static String KEY_USERNAME = "username";
    private final static String KEY_PROFILE_IMAGE = "profileimage";
    private final static String KEY_NAME = "name";
    private final static String KEY_PHONE_NUMBER = "phonenumber";
    private final static String KEY_TRACKABLE = "trackable";
    private final static String KEY_SAFE = "safe";
    private final static String KEY_LOCATION = "location";
    private final static String KEY_RINGABLE = "ringable";
    private final static String KEY_FREQUENCY = "checkin";
    private final static String KEY_LAST_CHECKIN = "lastCheckin";
    private final static String GEO_LOCATION = "place";
    private final static String KEY_CONTACT = "primaryContact";
    private final static String KEY_FRIENDS = "friends";
    private final static String KEY_EVENTS = "events";
    private final static String KEY_CHECKME = "checkMe";
    private final static String KEY_NOTIFY = "notify";
    private final static String KEY_ID = "objectId";
    private final static String KEY_HOUR = "Hourly";
    private final static String KEY_DAY = "Daily";
    private final static String KEY_WEEK = "Weekly";
    private final static String KEY_EVERY_HOUR = "Every hour";
    private final static String KEY_EVERY_DAY = "Every day";
    private final static String KEY_EVERY_WEEK = "Every week";

    public Number getNotificationThreshold() {
        return getNumber(KEY_NOTIFY);
    }

    public void setNotificationThreshold(int notifyThreshold) {
        put(KEY_NOTIFY, (Number) notifyThreshold);
    }
    public String getUserId() {return getString(KEY_ID);}

    public ParseObject getLastCheckin() {
        return getParseObject(KEY_LAST_CHECKIN);
    }

    public void setLastCheckin(Checkin checkin) {
        put(KEY_LAST_CHECKIN, checkin);
    }

    public String getUserName() {
        return getString(KEY_USERNAME);
    }

    public void setUserName(String username) {
        put(KEY_USERNAME, username);
    }

    public ParseFile getProfileImage() {
        return getParseFile(KEY_PROFILE_IMAGE);
    }

    public void setProfileImage(ParseFile parseFile) {
        put(KEY_PROFILE_IMAGE, parseFile);
    }

    public String getName() {
        return getString(KEY_NAME);
    }

    public void setName(String name) {
        put(KEY_NAME, name);
    }

    public String getPhonNumber() {
        return getString(KEY_PHONE_NUMBER);
    }

    public void setPhoneNumber(String phoneNumber) {
        put(KEY_PHONE_NUMBER, phoneNumber);
    }

    public boolean getTrackable() {
        return getBoolean(KEY_TRACKABLE);
    }

    public void setTrackable(boolean trackable) {
        put(KEY_TRACKABLE, trackable);
    }

    public boolean getSafe() {
        return getBoolean(KEY_SAFE);
    }

    public void setSafe(boolean safe) {
        put(KEY_SAFE, safe);
    }

    public boolean getRingable() {
        return getBoolean(KEY_RINGABLE);
    }

    public void setRingable(boolean ringable) {
        put(KEY_RINGABLE, ringable);
    }

    public void setPlace(ParseGeoPoint point) { put(GEO_LOCATION, point); }

    public ParseGeoPoint getPlace(){return getParseGeoPoint(GEO_LOCATION);}

    public User getPrimary()
    {
        return (User) getParseUser(KEY_CONTACT);
    }

    public void setPrimary(User user) {
        put(KEY_CONTACT, user);
    }


    public String getFrequency() {
        int num = (int) getNumber(KEY_FREQUENCY);
        switch (num) {
            case 10080:
                return KEY_EVERY_WEEK;
            case 1440:
                return KEY_EVERY_DAY;
            case 60:
                return KEY_EVERY_HOUR;
            default:
                return "Error";
        }
    }

    public void setFrequency(String frequency) {
        if (frequency.equals(KEY_DAY)) {
            put(KEY_FREQUENCY, 1440);
        }

        else if (frequency.equals(KEY_HOUR)) {
            put(KEY_FREQUENCY, 60);
        }

        else if (frequency.equals(KEY_WEEK)) {
            put(KEY_FREQUENCY, 10080);
        }
    }

    public List<Event> getEvents() {
        return getList(KEY_EVENTS);
    }

    public void addEvent(Event event) {
        add(KEY_EVENTS, event);
    }

    public ArrayList<String> getEventIds() {
        List<Event> myEvents = getEvents();
        ArrayList<String> eventsIds = new ArrayList<String>();
        for (int i = 0; i < myEvents.size(); i++)
        {
            eventsIds.add(myEvents.get(i).getObjectId());
        }
        return eventsIds;
    }

    public List<Friend> getFriends() {
        return getList(KEY_FRIENDS);
    }

    public boolean getCheckMe() {
        return getBoolean(KEY_CHECKME);
    }

    public void setCheckme(boolean checkme) {
        put(KEY_CHECKME, checkme);
    }
    public ArrayList<String> getFriendIds() {
        List<Friend> myFriends = getFriends();
        ArrayList<String> friendsIds = new ArrayList<String>();

        if (getFriends() != null)
        {
            for (int i = 0; i < myFriends.size(); i++)
            {
                friendsIds.add(myFriends.get(i).getObjectId());
            }
        }
        return friendsIds;
    }

    public ArrayList<String> getFriendUsers() {
        List<Friend> myFriends = getFriends();
        ArrayList<String> myFriendsUserIds = new ArrayList<String>();

        if (getFriends() != null)
        {
            for (int i = 0; i < myFriends.size(); i++) {
                Friend friend = myFriends.get(i);
                try {
                    User user = (User) friend.fetchIfNeeded().getParseUser("user");
                    myFriendsUserIds.add(user.getObjectId());
                }
                catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return myFriendsUserIds;
    }

    public void addFriend(Friend friend) {
        add(KEY_FRIENDS, friend);
    }

    public static class Query extends ParseQuery<User> {
        public Query() {
            super(User.class);
        }

        public Query getTop() {
            setLimit(20);
            return this;
        }
    }

    public List<String> getFriendUserNames() {
        List<Friend> friends = getFriends();
        ArrayList<String> names = new ArrayList<String>();
        if (getFriends() != null)
        {
            for (int i = 0; i < friends.size(); i++)
            {
                Friend friend = friends.get(i);
                names.add(friend.getUser().getUsername());
            }
        }
        return names;
    }
    
    public void setFriends(List<Friend> friends)
    {
        remove(KEY_FRIENDS);
        addAll(KEY_FRIENDS, friends);
    }
}
