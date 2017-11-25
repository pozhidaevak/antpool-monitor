package worker;

import com.google.gson.annotations.SerializedName;

public class Worker {
    @SerializedName("worker")
    private String name;
    private double last10m;

    public Worker(String name, double last10m) {
        this.name = name;
        this.last10m = last10m;
    }

    public String getName() {
        return name;
    }

    public double getLast10m() {
        return last10m;
    }
}
