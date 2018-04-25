package com.dbsoftwares.bungeeutilisals.bungee.user;

import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.UserCollection;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UserList implements UserCollection {

    private static final long serialVersionUID = -6761513279741915432L;
    private final ConcurrentHashMap<String, User> map = new ConcurrentHashMap<>();

    @Override
    public Optional<User> fromName(String name) {
        return stream().filter(user -> user.getName().equalsIgnoreCase(name)).findFirst();
    }

    @Override
    public Optional<User> fromPlayer(ProxiedPlayer player) {
        return fromName(player.getName());
    }

    @Override
    public int size() {
        return map.keySet().size();
    }

    @SuppressWarnings("unchecked")
    public User get(int index) {
        if (index >= size()) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
        }
        return (User) map.values().toArray()[index];
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean contains(Object obj) {
        if (obj instanceof User) {
            return map.containsValue(obj) || map.containsKey(((User) obj).getName());
        } else {
            return obj instanceof String && map.containsKey(obj);
        }
    }

    @Override
    public Iterator<User> iterator() {
        return map.values().iterator();
    }

    public Iterator<String> nameIterator() {
        return map.keySet().iterator();
    }

    @Override
    public Object[] toArray() {
        return map.values().toArray();
    }

    @Override
    @SuppressWarnings("unchecked")
    public User[] toTypeArray() {
        return (User[]) toArray();
    }

    @Override
    public String[] toNameArray() {
        return map.keySet().toArray(new String[]{});
    }

    @Override
    public <T> T[] toArray(T[] array) {
        return map.values().toArray(array);
    }

    @Override
    public boolean add(User user) {
        return this.map.putIfAbsent(user.getName(), user) == null;
    }

    @Override
    public boolean remove(Object obj) {
        if (obj instanceof User) {
            return map.remove(((User) obj).getName()) != null;
        } else {
            return obj instanceof String && map.remove(obj) != null;
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return map.values().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends User> userColl) {
        int addedAmount = 0;
        for (User user : userColl) {
            if (!contains(user) && add(user)) {
                addedAmount++;
            }
        }
        return addedAmount > 0;
    }

    @Override
    public boolean removeAll(Collection<?> userColl) {
        return removeIf(userColl::contains);
    }

    @Override
    public boolean retainAll(Collection<?> userColl) {
        return removeIf(value -> !userColl.contains(value));
    }

    @Override
    public void clear() {
        map.clear();
    }
}