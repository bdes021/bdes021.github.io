package com.example.bruno_desousa_trakker;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EventDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "events.db";
    private static final int VERSION = 3;

    private static EventDatabase mEventDb;
    private User user;

    public static EventDatabase getInstance(Context context) {
        if (mEventDb == null) {
            mEventDb = new EventDatabase(context);
        }
        return mEventDb;
    }

    public EventDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    private static final class UserTable {
        private static final String TABLE = "users";
        private static final String COL_USERNAME = "username";
        private static final String COL_PASSWORD = "password";
        private static final String COL_SMS_BOOL = "smsbool";
    }

    private static final class CategoryTable {
        private static final String TABLE = "categories";
        private static final String COL_ID = "_id";
        private static final String COL_NAME = "name";
    }

    private static final class EventTable {
        private static final String TABLE = "events";
        private static final String COL_ID = "_id";
        private static final String COL_TITLE = "title";
        private static final String COL_DATE = "date";
        private static final String COL_DESCRIPTION = "description";
        private static final String COL_LOCATION = "location";
        private static final String COL_EVENT_USERNAME = "eventusername";
        private static final String COL_CATEGORY_NAME = "category_name";
        private static final String COL_RECURRENCE = "recurrence";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating user table
        db.execSQL("CREATE TABLE " + UserTable.TABLE + " (" +
                UserTable.COL_USERNAME + " TEXT PRIMARY KEY NOT NULL, " +
                UserTable.COL_PASSWORD + " TEXT, " +
                UserTable.COL_SMS_BOOL + " INTEGER)");

        // Creating category table
        db.execSQL("CREATE TABLE " + CategoryTable.TABLE + " (" +
                CategoryTable.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CategoryTable.COL_NAME + " TEXT UNIQUE NOT NULL)");

        // Creating event table with recurrence and categories
        db.execSQL("CREATE TABLE " + EventTable.TABLE + " (" +
                EventTable.COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                EventTable.COL_TITLE + " TEXT, " +
                EventTable.COL_DATE + " INTEGER, " +
                EventTable.COL_DESCRIPTION + " TEXT, " +
                EventTable.COL_LOCATION + " TEXT, " +
                EventTable.COL_EVENT_USERNAME + " TEXT, " +
                EventTable.COL_CATEGORY_NAME + " TEXT, " +
                EventTable.COL_RECURRENCE + " TEXT, " +
                "FOREIGN KEY(" + EventTable.COL_EVENT_USERNAME + ") REFERENCES " +
                UserTable.TABLE + "(" + UserTable.COL_USERNAME + ") ON DELETE CASCADE)");

        // Create indexes for performance
        db.execSQL("CREATE INDEX idx_event_username ON " + EventTable.TABLE + "(" + EventTable.COL_EVENT_USERNAME + ")");
        db.execSQL("CREATE INDEX idx_event_date ON " + EventTable.TABLE + "(" + EventTable.COL_DATE + ")");

        // Adding administrator
        ContentValues values = new ContentValues();
        values.put(UserTable.COL_USERNAME, "admin");
        values.put(UserTable.COL_PASSWORD, "1234");
        values.put(UserTable.COL_SMS_BOOL, 3);
        db.insert(UserTable.TABLE, null, values);

        // Pre-populating categories
        String[] defaultCategories = {"Personal", "Work", "School", "Health", "Social"};
        for (String cat : defaultCategories) {
            ContentValues catValues = new ContentValues();
            catValues.put(CategoryTable.COL_NAME, cat);
            db.insert(CategoryTable.TABLE, null, catValues);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            db.execSQL("DROP TABLE IF EXISTS " + CategoryTable.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + UserTable.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + EventTable.TABLE);
            onCreate(db);
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.setForeignKeyConstraintsEnabled(true);
        }
    }

    @SuppressLint({"Range", "RestrictedApi"})
    public User getUser(String username) {
        SQLiteDatabase db = getReadableDatabase();
        User user = null;
        
        // Parameterized query for security
        String sql = "SELECT * FROM " + UserTable.TABLE +
                " WHERE " + UserTable.COL_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{username});

        if (cursor.moveToFirst()) {
            user = new User();
            user.setUsername(cursor.getString(cursor.getColumnIndex(UserTable.COL_USERNAME)));
            user.setPassword(cursor.getString(cursor.getColumnIndex(UserTable.COL_PASSWORD)));
            user.setSmsBool(cursor.getInt(cursor.getColumnIndex(UserTable.COL_SMS_BOOL)));
        }

        cursor.close();
        return user;
    }


    public boolean addUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(UserTable.COL_USERNAME, user.getUsername());
        values.put(UserTable.COL_PASSWORD, user.getPassword());
        values.put(UserTable.COL_SMS_BOOL, user.getSmsBool());
        long id = db.insert(UserTable.TABLE, null, values);
        return id != -1;
    }

    public void updateUser(User user, String options, String newData) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        switch (options) {
            case "username":
                values.put(UserTable.COL_USERNAME, newData);
                break;
            case "password":
                values.put(UserTable.COL_PASSWORD, newData);
                break;
            case "smsBool":
                values.put(UserTable.COL_SMS_BOOL, Integer.parseInt(newData));
                break;
            default:
                values.clear();
                break;
        }
        db.update(UserTable.TABLE, values,
                UserTable.COL_USERNAME + " = ?", new String[]{user.getUsername()});
    }

    public void deleteUser(User user) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(UserTable.TABLE,
                UserTable.COL_USERNAME + " = ?", new String[]{user.getUsername()});
    }

    public boolean authenticateUser(User user) {
        SQLiteDatabase db = this.getReadableDatabase();
        final String USERNAME = user.getUsername();
        final String PASSWORD = user.getPassword();

        String sql = "SELECT " + UserTable.COL_PASSWORD + " FROM " + UserTable.TABLE +
                " WHERE " + UserTable.COL_USERNAME + " = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{USERNAME});

        boolean result = false;

        if (cursor.moveToFirst()) {
            int passwordIndex = cursor.getColumnIndex(UserTable.COL_PASSWORD);

            if (passwordIndex != -1) {
                result = cursor.getString(passwordIndex).equals(PASSWORD);
            }
        }

        cursor.close();
        return result;
    }


    public boolean registerUser(User user) {
        final String USERNAME = user.getUsername();

        if (USERNAME != null && USERNAME.trim().length() > 0) {
            User existingUser = getUser(USERNAME);
            if (existingUser != null) {
                return false;
            } else {
                return this.addUser(user);
            }
        } else {
            return false;
        }
    }

    @SuppressLint("Range")
    public List<MyEvent> getEvents(String username) {
        List<MyEvent> events = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        final String orderBy = EventTable.COL_DATE + " ASC";

        // Parameterized query for security
        String sql = "SELECT * FROM " + EventTable.TABLE +
                " WHERE " + EventTable.COL_EVENT_USERNAME + " = ?" +
                " ORDER BY " + orderBy;
        Cursor cursor = db.rawQuery(sql, new String[]{username});
        
        if (cursor.moveToFirst()) {
            do {
                MyEvent event = new MyEvent();
                event.setId(cursor.getInt(cursor.getColumnIndex(EventTable.COL_ID)));
                event.setTitle(cursor.getString(cursor.getColumnIndex(EventTable.COL_TITLE)));
                Calendar cal = formatDate(cursor.getLong(cursor.getColumnIndex(EventTable.COL_DATE)));
                event.setDate(cal);
                event.setDescription(cursor.getString(cursor.getColumnIndex(EventTable.COL_DESCRIPTION)));
                event.setLocation(cursor.getString(cursor.getColumnIndex(EventTable.COL_LOCATION)));
                event.setEventusername(cursor.getString(cursor.getColumnIndex(EventTable.COL_EVENT_USERNAME)));
                event.setCategory(cursor.getString(cursor.getColumnIndex(EventTable.COL_CATEGORY_NAME)));
                event.setRecurrence(cursor.getString(cursor.getColumnIndex(EventTable.COL_RECURRENCE)));
                events.add(event);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return events;
    }

    @SuppressLint("Range")
    public MyEvent getEvent(int eId) {
        SQLiteDatabase db = this.getReadableDatabase();
        MyEvent event = null;

        // Parameterized query for security
        String sql = "SELECT * FROM " + EventTable.TABLE +
                " WHERE " + EventTable.COL_ID + " = ?";

        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(eId)});
        if (cursor.moveToFirst()) {
            event = new MyEvent();
            event.setId(cursor.getInt(cursor.getColumnIndex(EventTable.COL_ID)));
            event.setTitle(cursor.getString(cursor.getColumnIndex(EventTable.COL_TITLE)));
            Calendar cal = formatDate(cursor.getLong(cursor.getColumnIndex(EventTable.COL_DATE)));
            event.setDate(cal);
            event.setDescription(cursor.getString(cursor.getColumnIndex(EventTable.COL_DESCRIPTION)));
            event.setLocation(cursor.getString(cursor.getColumnIndex(EventTable.COL_LOCATION)));
            event.setEventusername(cursor.getString(cursor.getColumnIndex(EventTable.COL_EVENT_USERNAME)));
            event.setCategory(cursor.getString(cursor.getColumnIndex(EventTable.COL_CATEGORY_NAME)));
            event.setRecurrence(cursor.getString(cursor.getColumnIndex(EventTable.COL_RECURRENCE)));
        }
        cursor.close();
        return event;
    }

    public boolean addEvent(MyEvent event) {
        // Validation: Title is required
        if (event.getTitle() == null || event.getTitle().trim().isEmpty()) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        Calendar eventDate = event.getDate();
        String formatDate = String.format("%04d%02d%02d%02d%02d",
                eventDate.get(Calendar.YEAR),
                eventDate.get(Calendar.MONTH) + 1,
                eventDate.get(Calendar.DAY_OF_MONTH),
                eventDate.get(Calendar.HOUR_OF_DAY),
                eventDate.get(Calendar.MINUTE));

        values.put(EventTable.COL_TITLE, event.getTitle());
        values.put(EventTable.COL_DATE, Long.parseLong(formatDate));
        values.put(EventTable.COL_DESCRIPTION, event.getDescription());
        values.put(EventTable.COL_LOCATION, event.getLocation());
        values.put(EventTable.COL_EVENT_USERNAME, event.getEventusername());
        values.put(EventTable.COL_CATEGORY_NAME, event.getCategory());
        values.put(EventTable.COL_RECURRENCE, event.getRecurrence());
        
        long id = db.insert(EventTable.TABLE, null, values);
        event.setId((int) id);
        return id != -1;
    }

    public boolean updateEvent(MyEvent event) {
        // Validation: Title is required
        if (event.getTitle() == null || event.getTitle().trim().isEmpty()) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        Calendar eventDate = event.getDate();
        String formatDate = String.format("%04d%02d%02d%02d%02d",
                eventDate.get(Calendar.YEAR),
                eventDate.get(Calendar.MONTH) + 1,
                eventDate.get(Calendar.DAY_OF_MONTH),
                eventDate.get(Calendar.HOUR_OF_DAY),
                eventDate.get(Calendar.MINUTE));

        values.put(EventTable.COL_TITLE, event.getTitle());
        values.put(EventTable.COL_DATE, Long.parseLong(formatDate));
        values.put(EventTable.COL_DESCRIPTION, event.getDescription());
        values.put(EventTable.COL_LOCATION, event.getLocation());
        values.put(EventTable.COL_EVENT_USERNAME, event.getEventusername());
        values.put(EventTable.COL_CATEGORY_NAME, event.getCategory());
        values.put(EventTable.COL_RECURRENCE, event.getRecurrence());

        int rows = db.update(EventTable.TABLE, values,
                EventTable.COL_ID + " = ?", new String[]{String.valueOf(event.getId())});

        return rows > 0;
    }

    @SuppressLint("Range")
    public List<String> getCategories() {
        List<String> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(CategoryTable.TABLE, new String[]{CategoryTable.COL_NAME}, null, null, null, null, CategoryTable.COL_NAME + " ASC");
        if (cursor.moveToFirst()) {
            do {
                categories.add(cursor.getString(cursor.getColumnIndex(CategoryTable.COL_NAME)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return categories;
    }

    public void deleteEvent(MyEvent event) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(EventTable.TABLE,
                EventTable.COL_ID + " = ?", new String[]{String.valueOf(event.getId())});
    }

    public Calendar formatDate(long longDate) {
        String sDate = Long.toString(longDate);
        int iYear = Integer.parseInt(sDate.substring(0, 4));
        int iMonth = Integer.parseInt(sDate.substring(4, 6)) - 1;
        int iDay = Integer.parseInt(sDate.substring(6, 8));
        int iHour = Integer.parseInt(sDate.substring(8, 10));
        int iMinute = Integer.parseInt(sDate.substring(10, 12));
        Calendar cal = Calendar.getInstance();
        cal.set(iYear, iMonth, iDay, iHour, iMinute);
        return cal;
    }
}
