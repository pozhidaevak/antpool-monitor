package github.antmonitor.worker;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class Worker {

  final private String name;

  // Hashrate in kH/s
  final private double last10m;

  // Hashrate in kH/s
  final private double last1h;

  @JsonCreator
  public Worker(@JsonProperty(value = "worker", required = true) String name,
      @JsonProperty(value = "last10m", required = true) double last10m,
      @JsonProperty(value = "last1h", required = true) double last1h) {
    this.name = name;
    this.last10m = last10m;
    this.last1h = last1h;
  }

  public double getLast10m() {
    return last10m;
  }

  public double getLast1h() {
    return last1h;
  }

  public String getName() {
    return name;
  }
}
