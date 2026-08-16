package com.example.bruno_desousa_trakker;
import java.util.Calendar;

public class MyEvent {
    private int id;
    private String title;
    private Calendar date;
    private String description;
    private String location;
    private String eventUsername;
    private String category;
    private String recurrence;

    public MyEvent() {
        // Default constructor
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEventusername() {
        return eventUsername;
    }

    public void setEventusername(String eventUsername) {
        this.eventUsername = eventUsername;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(String recurrence) {
        this.recurrence = recurrence;
    }
}
