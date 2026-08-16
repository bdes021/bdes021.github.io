package com.example.bruno_desousa_trakker;

import android.app.DatePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
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
import androidx.lifecycle.ViewModelProvider;
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
    
    private TrakkerViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(TrakkerViewModel.class);
        setupObservers();
        showLoginLayout();
    }

    private void setupObservers() {
        viewModel.getLoginSuccess().observe(this, success -> {
            if (success != null && success) {
                // Save user to Prefs for Widget
                User user = viewModel.getCurrentUser().getValue();
                if (user != null) {
                    SharedPreferences prefs = getSharedPreferences("TrakkerPrefs", MODE_PRIVATE);
                    prefs.edit().putString("current_user", user.getUsername()).apply();
                    updateWidget();
                }
                showCalendarLayout();
            }
        });

        viewModel.getAuthMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                if (message.equals("Event Saved") || message.equals("Event Deleted")) {
                    updateWidget();
                }
            }
        });

        viewModel.getEventsList().observe(this, events -> {
            updateEventDetailsUI(events);
        });
        
        viewModel.getSelectedDate().observe(this, date -> {
            // Re-load events if date changes externally if needed
        });
    }

    private void showLoginLayout() {
        setContentView(R.layout.activity_main_login_create);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        createAccountButton = findViewById(R.id.createAccountButton);

        loginButton.setOnClickListener(v -> {
            viewModel.login(usernameEditText.getText().toString(), 
                          passwordEditText.getText().toString());
        });

        createAccountButton.setOnClickListener(v -> {
            viewModel.register(usernameEditText.getText().toString(), 
                             passwordEditText.getText().toString());
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

        // Initial load
        viewModel.loadEvents();

        // Set a listener for the calendar view to handle date selection
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            viewModel.setSelectedDate(year, month, dayOfMonth);
        });

        // Set a click listener for the add event icon
        addEventIcon.setOnClickListener(v -> showAddEditEventLayout(null));

        // Hamburger menu click listener
        menuIcon.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(MainActivity.this, menuIcon);
            popup.getMenu().add("Logout");
            popup.getMenu().add("Settings");
            popup.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Logout")) {
                    SharedPreferences prefs = getSharedPreferences("TrakkerPrefs", MODE_PRIVATE);
                    prefs.edit().remove("current_user").apply();
                    updateWidget();
                    viewModel.logout();
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
        User user = viewModel.getCurrentUser().getValue();
        if (user != null) {
            smsSwitch.setChecked(user.getSmsBool() == 1);
        }

        smsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.updateSmsPreference(isChecked);
        });

        buttonDeleteAccount.setOnClickListener(v -> {
            viewModel.deleteAccount();
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
        Spinner spinnerCategory = findViewById(R.id.spinnerCategory);
        Spinner spinnerRecurrence = findViewById(R.id.spinnerRecurrence);
        TextView textViewDate = findViewById(R.id.textViewDate);
        Button buttonSave = findViewById(R.id.buttonSave);
        Button buttonBack = findViewById(R.id.buttonBack);
        Button buttonDelete = findViewById(R.id.buttonDelete);

        // Populate Category Spinner
        List<String> categories = viewModel.getCategoriesList().getValue();
        if (categories != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
                    android.R.layout.simple_spinner_item, categories);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerCategory.setAdapter(adapter);
        }

        Calendar workingDate;
        if (existingEvent != null) {
            editTextTitle.setText(existingEvent.getTitle());
            editTextDescription.setText(existingEvent.getDescription());
            editTextLocation.setText(existingEvent.getLocation());
            workingDate = (Calendar) existingEvent.getDate().clone();
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

            // Set Category Spinner
            if (categories != null && existingEvent.getCategory() != null) {
                for (int i = 0; i < categories.size(); i++) {
                    if (categories.get(i).equals(existingEvent.getCategory())) {
                        spinnerCategory.setSelection(i);
                        break;
                    }
                }
            }

            // Set Recurrence Spinner
            if (existingEvent.getRecurrence() != null) {
                String[] recurOptions = getResources().getStringArray(R.array.recurrence_options);
                for (int i = 0; i < recurOptions.length; i++) {
                    if (recurOptions[i].equals(existingEvent.getRecurrence())) {
                        spinnerRecurrence.setSelection(i);
                        break;
                    }
                }
            }
        } else {
            workingDate = (Calendar) viewModel.getSelectedDate().getValue().clone();
            buttonDelete.setVisibility(View.GONE);
        }

        updateDateTextView(textViewDate, workingDate);

        textViewDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        workingDate.set(year, month, dayOfMonth);
                        updateDateTextView(textViewDate, workingDate);
                    },
                    workingDate.get(Calendar.YEAR),
                    workingDate.get(Calendar.MONTH),
                    workingDate.get(Calendar.DAY_OF_MONTH));
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
            event.setCategory(spinnerCategory.getSelectedItem() != null ? 
                             spinnerCategory.getSelectedItem().toString() : "");
            event.setRecurrence(spinnerRecurrence.getSelectedItem().toString());

            // Get selected start time and update workingDate
            String startTimeStr = spinnerStartTime.getSelectedItem().toString();
            updateCalendarWithTime(workingDate, startTimeStr);
            event.setDate((Calendar) workingDate.clone());

            viewModel.saveEvent(event);
            showCalendarLayout();
        });

        buttonBack.setOnClickListener(v -> showCalendarLayout());

        buttonDelete.setOnClickListener(v -> {
            if (existingEvent != null) {
                viewModel.deleteEvent(existingEvent);
                showCalendarLayout();
            }
        });
    }

    private void updateDateTextView(TextView textViewDate, Calendar date) {
        String dateString = String.format(Locale.getDefault(), "%02d/%02d/%d",
                date.get(Calendar.DAY_OF_MONTH),
                date.get(Calendar.MONTH) + 1,
                date.get(Calendar.YEAR));
        textViewDate.setText(dateString);
    }

    private void updateCalendarWithTime(Calendar cal, String timeStr) {
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

    private void updateEventDetailsUI(List<MyEvent> events) {
        if (eventDetailsList == null) return;
        
        eventDetailsList.removeAllViews();
        if (events == null || events.isEmpty()) {
            eventDetailsScrollView.setVisibility(View.GONE);
            noEventsTextView.setVisibility(View.VISIBLE);
            return;
        }

        eventDetailsScrollView.setVisibility(View.VISIBLE);
        noEventsTextView.setVisibility(View.GONE);

        for (MyEvent event : events) {
            Calendar cal = event.getDate();
            TextView tv = new TextView(this);
            String time = String.format(Locale.getDefault(), "%02d:%02d %s",
                    cal.get(Calendar.HOUR) == 0 ? 12 : cal.get(Calendar.HOUR),
                    cal.get(Calendar.MINUTE),
                    cal.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM");
            
            String text = "Title: " + event.getTitle() + "\n" +
                          "Time: " + time + "\n" +
                          "Category: " + event.getCategory() + "\n" +
                          "Recurrence: " + event.getRecurrence() + "\n" +
                          "Location: " + event.getLocation() + "\n" +
                          "Description: " + event.getDescription() + "\n" +
                          "------------------------------------------";
            tv.setText(text);
            tv.setTextColor(androidx.core.content.ContextCompat.getColor(this, android.R.color.black));
            tv.setTextSize(22); // Increased font size
            tv.setPadding(10, 20, 10, 20);
            tv.setOnClickListener(v -> showAddEditEventLayout(event));
            eventDetailsList.addView(tv);
        }
    }

    public void setCalendarView(CalendarView calendarView) {
        this.calendarView = calendarView;
    }

    private void updateWidget() {
        Intent intent = new Intent(this, TrakkerWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getApplication())
                .getAppWidgetIds(new ComponentName(getApplication(), TrakkerWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }
}
