package config;

public class DailyReport {
    private int hour;

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    private int minute;

    public DailyReport(int hour, int minute) {
        if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
            throw new IllegalArgumentException("Wrong time");
        }
        this.hour = hour;
        this.minute = minute;
    }
    public DailyReport() {}


    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }
}
