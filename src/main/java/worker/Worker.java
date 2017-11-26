package worker;

import com.google.gson.annotations.SerializedName;

public class Worker {
    @SerializedName("worker")
    private String name;
    private double last10m;
    private double last1h;

    public Worker(String name, double last10m, double last1h) {
        this.name = name;
        this.last10m = last10m;
        this.last1h = last1h;
    }

    public String getName() {
        return name;
    }

    public double getLast10m() {
        return last10m;
    }

    public double getLast1h() {
        return last1h;
    }
}
