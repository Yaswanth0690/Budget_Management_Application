package entity;

public class NotificationSettings {

    private boolean dailyAlertEnabled;
    private boolean monthlyAlertEnabled;
    private boolean remindersEnabled;

    public NotificationSettings() {}

    public NotificationSettings(boolean dailyAlertEnabled,
                                boolean monthlyAlertEnabled,
                                boolean remindersEnabled) {
        this.dailyAlertEnabled = dailyAlertEnabled;
        this.monthlyAlertEnabled = monthlyAlertEnabled;
        this.remindersEnabled = remindersEnabled;
    }

    public boolean isDailyAlertEnabled() { return dailyAlertEnabled; }
    public void setDailyAlertEnabled(boolean dailyAlertEnabled) {
        this.dailyAlertEnabled = dailyAlertEnabled;
    }

    public boolean isMonthlyAlertEnabled() { return monthlyAlertEnabled; }
    public void setMonthlyAlertEnabled(boolean monthlyAlertEnabled) {
        this.monthlyAlertEnabled = monthlyAlertEnabled;
    }

    public boolean isRemindersEnabled() { return remindersEnabled; }
    public void setRemindersEnabled(boolean remindersEnabled) {
        this.remindersEnabled = remindersEnabled;
    }

    @Override
    public String toString() {
        return "Settings: Daily=" + dailyAlertEnabled +
                ", Monthly=" + monthlyAlertEnabled +
                ", Reminders=" + remindersEnabled;
    }
}
