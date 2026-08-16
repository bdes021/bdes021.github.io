package com.example.bruno_desousa_trakker;

import android.content.Context;
import java.util.List;

public class TrakkerRepository {
    private final EventDatabase db;

    public TrakkerRepository(Context context) {
        db = EventDatabase.getInstance(context);
    }

    public boolean authenticateUser(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        return db.authenticateUser(user);
    }

    public boolean registerUser(String username, String password) {
        User user = new User(username, password, 1);
        return db.registerUser(user);
    }

    public User getUser(String username) {
        return db.getUser(username);
    }

    public void updateUser(User user, String field, String value) {
        db.updateUser(user, field, value);
    }

    public void deleteUser(User user) {
        db.deleteUser(user);
    }

    public List<MyEvent> getEvents(String username) {
        return db.getEvents(username);
    }

    public List<String> getCategories() {
        return db.getCategories();
    }

    public boolean addEvent(MyEvent event) {
        return db.addEvent(event);
    }

    public boolean updateEvent(MyEvent event) {
        return db.updateEvent(event);
    }

    public void deleteEvent(MyEvent event) {
        db.deleteEvent(event);
    }
}
