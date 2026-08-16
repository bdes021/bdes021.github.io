package com.example.bruno_desousa_trakker;

import java.util.List;

public class GridItem {
    private String date;
    private String time;
    private String title;
    private String description;

    private List<String> list; // Assuming you want a list of Strings

    public GridItem(String date, String time, String title, String description, List<String> list) {
        this.date = date;
        this.time = time;
        this.title = title;
        this.description = description;
        this.list = list;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getList() {
        return list;
    }
}
