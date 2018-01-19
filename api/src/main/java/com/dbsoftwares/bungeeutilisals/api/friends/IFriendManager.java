package com.dbsoftwares.bungeeutilisals.api.friends;

import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;

import java.util.Collection;
import java.util.UUID;

public interface IFriendManager {

    /**
     * Adds a Friend to the given user.
     * @param user The user you want to give a new friend.
     * @param identifier The friend his UUID.
     * @param name The friend his name.
     */
    void addFriend(User user, UUID identifier, String name);

    /**
     * Removes a Friend from the given user.
     * @param user The user you want to remove a friend of.
     * @param identifier The friend uuid you want to remove.
     */
    void removeFriend(User user, UUID identifier);

    /**
     * Removes a Friend from the given user.
     * @param user The user you want to remove a friend of.
     * @param name The friend name you want to remove.
     */
    void removeFriend(User user, String name);

    /**
     * Gives you a collection of FriendData of the given User.
     * @param user The user you want to get the friends of.
     * @return A collection of FriendData objects.
     */
    Collection<FriendData> getFriends(String user);
}