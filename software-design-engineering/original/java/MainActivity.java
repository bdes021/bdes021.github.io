package com.example.bruno_desousa_trakker;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private View eventDetailsScrollView;
    private LinearLayout eventDetailsList;
    private TextView noEventsTextView;
    private ImageView addEventIcon;

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button createAccountButton;
    private EventDatabase db;
    private User currentUser;
    private Calendar selectedDate = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = EventDatabase.getInstance(this);
        showLoginLayout();
    }

    private void showLoginLayout() {
        setContentView(R.layout.activity_main_login_create);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        createAccountButton = findViewById(R.id.createAccountButton);

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            User user = new User();
            user.setUsername(username);
            user.setPassword(password);

            if (db.authenticateUser(user)) {
                currentUser = db.getUser(username);
                showCalendarLayout();
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });

        createAccountButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show();
                return;
            }

            User user = new User(username, password, 1); // Default SMS enabled
            if (db.registerUser(user)) {
                Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showCalendarLayout() {
        setContentView(R.layout.calendar_layout);

        calendarView = findViewById(R.id.calendarView);
        eventDetailsScrollView = findViewById(R.id.eventDetailsScrollView);
        eventDetailsList = findViewById(R.id.eventDetailsList);
        noEventsTextView = findViewById(R.id.noEventsTextView);
        addEventIcon = findViewById(R.id.addEventIcon);
        ImageView menuIcon = findViewById(R.id.menuIcon);

        // Set a listener for the calendar view to handle date selection
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDate.set(year, month, dayOfMonth);
            updateEventDetails(year, month, dayOfMonth);
        });

        // Set a click listener for the add event icon
        addEventIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddEditEventLayout(null);
            }
        });

        // Hamburger menu click listener
        menuIcon.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(MainActivity.this, menuIcon);
            popup.getMenu().add("Logout");
            popup.getMenu().add("Settings");
            popup.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Logout")) {
                    currentUser = null;
                    showLoginLayout();
                    return true;
                } else if (item.getTitle().equals("Settings")) {
                    showSettingsLayout();
                    return true;
                }
                return false;
            });
            popup.show();
        });
    }

    private void showSettingsLayout() {
        setContentView(R.layout.settings_layout);

        Switch smsSwitch = findViewById(R.id.smsSwitch);
        Button buttonDeleteAccount = findViewById(R.id.buttonDeleteAccount);
        Button buttonBackSettings = findViewById(R.id.buttonBackSettings);

        // Initialize switch state
        smsSwitch.setChecked(currentUser.getSmsBool() == 1);

        smsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            currentUser.setSmsBool(isChecked ? 1 : 0);
            db.updateUser(currentUser, "smsBool", String.valueOf(currentUser.getSmsBool()));
            Toast.makeText(this, "Preference saved", Toast.LENGTH_SHORT).show();
        });

        buttonDeleteAccount.setOnClickListener(v -> {
            // Confirmation would be better, but implementing direct deletion for now
            db.deleteUser(currentUser);
            currentUser = null;
            Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show();
            showLoginLayout();
        });

        buttonBackSettings.setOnClickListener(v -> showCalendarLayout());
    }

    private void showAddEditEventLayout(MyEvent existingEvent) {
        setContentView(R.layout.add_edit_event_layout);

        EditText editTextTitle = findViewById(R.id.editTextTitle);
        EditText editTextDescription = findViewById(R.id.editTextDescription);
        EditText editTextLocation = findViewById(R.id.editTextLocation);
        Spinner spinnerStartTime = findViewById(R.id.spinnerStartTime);
        Spinner spinnerEndTime = findViewById(R.id.spinnerEndTime);
        TextView textViewDate = findViewById(R.id.textViewDate);
        Button buttonSave = findViewById(R.id.buttonSave);
        Button buttonBack = findViewById(R.id.buttonBack);
        Button buttonDelete = findViewById(R.id.buttonDelete);

        if (existingEvent != null) {
            editTextTitle.setText(existingEvent.getTitle());
            editTextDescription.setText(existingEvent.getDescription());
            editTextLocation.setText(existingEvent.getLocation());
            selectedDate = (Calendar) existingEvent.getDate().clone();
            buttonDelete.setVisibility(View.VISIBLE);
            
            // Set spinners based on existingEvent time
            String timeStr = String.format(Locale.getDefault(), "%02d:%02d %s",
                    existingEvent.getDate().get(Calendar.HOUR) == 0 ? 12 : existingEvent.getDate().get(Calendar.HOUR),
                    existingEvent.getDate().get(Calendar.MINUTE),
                    existingEvent.getDate().get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");
            
            for (int i = 0; i < spinnerStartTime.getCount(); i++) {
                if (spinnerStartTime.getItemAtPosition(i).toString().equals(timeStr)) {
                    spinnerStartTime.setSelection(i);
                    break;
                }
            }
        } else {
            buttonDelete.setVisibility(View.GONE);
        }

        updateDateTextView(textViewDate);

        textViewDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        selectedDate.set(year, month, dayOfMonth);
                        updateDateTextView(textViewDate);
                    },
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        buttonSave.setOnClickListener(v -> {
            String title = editTextTitle.getText().toString();
            if (title.isEmpty()) {
                Toast.makeText(this, "Title is required", Toast.LENGTH_SHORT).show();
                return;
            }

            MyEvent event = (existingEvent != null) ? existingEvent : new MyEvent();
            event.setTitle(title);
            event.setDescription(editTextDescription.getText().toString());
            event.setLocation(editTextLocation.getText().toString());
            event.setEventusername(currentUser.getUsername());

            // Get selected start time
            String startTimeStr = spinnerStartTime.getSelectedItem().toString();
            updateCalendarWithTime(selectedDate, startTimeStr);
            
            event.setDate((Calendar) selectedDate.clone());

            boolean success;
            if (existingEvent != null) {
                success = db.updateEvent(event);
            } else {
                success = db.addEvent(event);
            }

            if (success) {
                Toast.makeText(this, "Event Saved", Toast.LENGTH_SHORT).show();
                showCalendarLayout();
            } else {
                Toast.makeText(this, "Failed to save event", Toast.LENGTH_SHORT).show();
            }
        });

        buttonBack.setOnClickListener(v -> showCalendarLayout());

        buttonDelete.setOnClickListener(v -> {
            if (existingEvent != null) {
                db.deleteEvent(existingEvent);
                Toast.makeText(this, "Event Deleted", Toast.LENGTH_SHORT).show();
                showCalendarLayout();
            }
        });
    }

    private void updateDateTextView(TextView textViewDate) {
        String dateString = String.format(Locale.getDefault(), "%02d/%02d/%d",
                selectedDate.get(Calendar.DAY_OF_MONTH),
                selectedDate.get(Calendar.MONTH) + 1,
                selectedDate.get(Calendar.YEAR));
        textViewDate.setText(dateString);
    }

    private void updateCalendarWithTime(Calendar cal, String timeStr) {
        // timeStr format: "HH:mm AM/PM"
        try {
            String[] parts = timeStr.split(" ");
            String[] hourMin = parts[0].split(":");
            int hour = Integer.parseInt(hourMin[0]);
            int minute = Integer.parseInt(hourMin[1]);
            String amPm = parts[1];

            if (amPm.equals("PM") && hour < 12) {
                hour += 12;
            } else if (amPm.equals("AM") && hour == 12) {
                hour = 0;
            }

            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, minute);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateEventDetails(int year, int month, int dayOfMonth) {
        eventDetailsList.removeAllViews();
        List<MyEvent> events = db.getEvents(currentUser.getUsername());
        boolean found = false;

        for (MyEvent event : events) {
            Calendar cal = event.getDate();
            if (cal.get(Calendar.YEAR) == year &&
                cal.get(Calendar.MONTH) == month &&
                cal.get(Calendar.DAY_OF_MONTH) == dayOfMonth) {
                
                found = true;
                TextView tv = new TextView(this);
                String time = String.format(Locale.getDefault(), "%02d:%02d %s",
                        cal.get(Calendar.HOUR) == 0 ? 12 : cal.get(Calendar.HOUR),
                        cal.get(Calendar.MINUTE),
                        cal.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");
                
                String text = "Title: " + event.getTitle() + "\n" +
                              "Time: " + time + "\n" +
                              "Location: " + event.getLocation() + "\n" +
                              "Description: " + event.getDescription() + "\n" +
                              "------------------------------------------";
                tv.setText(text);
                tv.setTextColor(androidx.core.content.ContextCompat.getColor(this, android.R.color.black));
                tv.setPadding(10, 20, 10, 20);
                tv.setOnClickListener(v -> showAddEditEventLayout(event));
                eventDetailsList.addView(tv);
            }
        }

        if (found) {
            eventDetailsScrollView.setVisibility(View.VISIBLE);
            noEventsTextView.setVisibility(View.GONE);
        } else {
            eventDetailsScrollView.setVisibility(View.GONE);
            noEventsTextView.setVisibility(View.VISIBLE);
        }
    }

    // Setters
    public void setCalendarView(CalendarView calendarView) {
        this.calendarView = calendarView;
    }

}
