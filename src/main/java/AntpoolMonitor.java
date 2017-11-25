import config.Configuration;

public class AntpoolMonitor {
    public static void main(String[] args) {
        System.out.println(Configuration.INSTANCE.getKey());
    }
}
