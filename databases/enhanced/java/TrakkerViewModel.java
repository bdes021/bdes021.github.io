package com.example.bruno_desousa_trakker;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TrakkerViewModel extends AndroidViewModel {
    private final TrakkerRepository repository;
    
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final MutableLiveData<List<MyEvent>> eventsList = new MutableLiveData<>();
    private final MutableLiveData<List<String>> categoriesList = new MutableLiveData<>();
    private final MutableLiveData<Calendar> selectedDate = new MutableLiveData<>(Calendar.getInstance());
    private final MutableLiveData<String> authMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();

    public TrakkerViewModel(@NonNull Application application) {
        super(application);
        repository = new TrakkerRepository(application);
    }

    public LiveData<User> getCurrentUser() { return currentUser; }
    public LiveData<List<MyEvent>> getEventsList() { return eventsList; }
    public LiveData<List<String>> getCategoriesList() { return categoriesList; }
    public LiveData<Calendar> getSelectedDate() { return selectedDate; }
    public LiveData<String> getAuthMessage() { return authMessage; }
    public LiveData<Boolean> getLoginSuccess() { return loginSuccess; }

    public void login(String username, String password) {
        if (repository.authenticateUser(username, password)) {
            currentUser.setValue(repository.getUser(username));
            loginSuccess.setValue(true);
        } else {
            authMessage.setValue("Invalid credentials");
            loginSuccess.setValue(false);
        }
    }

    public void register(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            authMessage.setValue("Please enter username and password");
            return;
        }
        if (repository.registerUser(username, password)) {
            authMessage.setValue("Account created");
        } else {
            authMessage.setValue("User already exists");
        }
    }

    public void logout() {
        currentUser.setValue(null);
        loginSuccess.setValue(false);
    }

    public void deleteAccount() {
        if (currentUser.getValue() != null) {
            repository.deleteUser(currentUser.getValue());
            logout();
            authMessage.setValue("Account deleted");
        }
    }

    public void updateSmsPreference(boolean enabled) {
        User user = currentUser.getValue();
        if (user != null) {
            user.setSmsBool(enabled ? 1 : 0);
            repository.updateUser(user, "smsBool", String.valueOf(user.getSmsBool()));
        }
    }

    public void setSelectedDate(int year, int month, int day) {
        Calendar cal = selectedDate.getValue();
        if (cal != null) {
            cal.set(year, month, day);
            selectedDate.setValue(cal);
            loadEvents();
        }
    }

    public void loadEvents() {
        User user = currentUser.getValue();
        Calendar date = selectedDate.getValue();
        if (user != null && date != null) {
            categoriesList.setValue(repository.getCategories());
            List<MyEvent> allEvents = repository.getEvents(user.getUsername());
            List<MyEvent> filteredEvents = new ArrayList<>();
            for (MyEvent event : allEvents) {
                Calendar eventCal = event.getDate();
                if (isOnOrAfter(date, eventCal)) {
                    String recurrence = event.getRecurrence();
                    if (recurrence == null || recurrence.equals("None")) {
                        if (isSameDay(date, eventCal)) {
                            filteredEvents.add(event);
                        }
                    } else if (recurrence.equals("Daily")) {
                        filteredEvents.add(event);
                    } else if (recurrence.equals("Weekly")) {
                        if (eventCal.get(Calendar.DAY_OF_WEEK) == date.get(Calendar.DAY_OF_WEEK)) {
                            filteredEvents.add(event);
                        }
                    } else if (recurrence.equals("Monthly")) {
                        if (eventCal.get(Calendar.DAY_OF_MONTH) == date.get(Calendar.DAY_OF_MONTH)) {
                            filteredEvents.add(event);
                        }
                    } else if (recurrence.equals("Yearly")) {
                        if (eventCal.get(Calendar.DAY_OF_MONTH) == date.get(Calendar.DAY_OF_MONTH) &&
                                eventCal.get(Calendar.MONTH) == date.get(Calendar.MONTH)) {
                            filteredEvents.add(event);
                        }
                    }
                }
            }
            eventsList.setValue(filteredEvents);
        }
    }

    private boolean isSameDay(Calendar c1, Calendar c2) {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) &&
                c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
    }

    private boolean isOnOrAfter(Calendar selected, Calendar event) {
        Calendar s = (Calendar) selected.clone();
        s.set(Calendar.HOUR_OF_DAY, 0);
        s.set(Calendar.MINUTE, 0);
        s.set(Calendar.SECOND, 0);
        s.set(Calendar.MILLISECOND, 0);

        Calendar e = (Calendar) event.clone();
        e.set(Calendar.HOUR_OF_DAY, 0);
        e.set(Calendar.MINUTE, 0);
        e.set(Calendar.SECOND, 0);
        e.set(Calendar.MILLISECOND, 0);

        return !s.before(e);
    }

    public void saveEvent(MyEvent event) {
        User user = currentUser.getValue();
        if (user != null) {
            event.setEventusername(user.getUsername());
            boolean success;
            if (event.getId() > 0) {
                success = repository.updateEvent(event);
            } else {
                success = repository.addEvent(event);
            }
            if (success) {
                loadEvents();
                authMessage.setValue("Event Saved");
            } else {
                authMessage.setValue("Failed to save event");
            }
        }
    }

    public void deleteEvent(MyEvent event) {
        repository.deleteEvent(event);
        loadEvents();
        authMessage.setValue("Event Deleted");
    }
}
