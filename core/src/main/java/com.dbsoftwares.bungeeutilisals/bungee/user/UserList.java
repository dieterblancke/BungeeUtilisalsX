package com.dbsoftwares.bungeeutilisals.bungee.user;

import com.dbsoftwares.bungeeutilisals.api.user.User;
import com.dbsoftwares.bungeeutilisals.api.user.UserCollection;
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
        return stream().filter(user -> user.getName().equalsIgnoreCase(player.getName())).findFirst();
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
    public boolean contains(Object o) {
        if (o instanceof User) {
            return map.containsValue(o) || map.containsKey(((User) o).getName());
        } else {
            return o instanceof String && map.containsKey(o);
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

    @SuppressWarnings("unchecked")
    public User[] toTypeArray() {
        return (User[]) toArray();
    }

    public String[] toNameArray() {
        return map.keySet().toArray(new String[]{});
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return map.values().toArray(a);
    }

    @Override
    public boolean add(User u) {
        return this.map.putIfAbsent(u.getName(), u) == null;
    }

    @Override
    public boolean remove(Object o) {
        if (o instanceof User) {
            return map.remove(((User) o).getName()) != null;
        } else {
            return o instanceof String && map.remove(o) != null;
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return map.values().containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends User> c) {
        Boolean added = false;
        for (User e : c) {
            if (!contains(e)) {
                if (!added) {
                    added = add(e);
                } else {
                    add(e);
                }
            }
        }
        return added;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return removeIf(c::contains);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return removeIf(value -> !c.contains(value));
    }

    @Override
    public void clear() {
        map.clear();
    }
}