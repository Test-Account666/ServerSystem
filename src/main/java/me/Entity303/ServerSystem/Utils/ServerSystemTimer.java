package me.Entity303.ServerSystem.Utils;

import java.util.LinkedList;

public class ServerSystemTimer implements Runnable {
    private final LinkedList<Double> history = new LinkedList<>();
    private transient long lastPoll = System.nanoTime();

    public ServerSystemTimer() {
        this.history.add(20.0);
    }

    @Override
    public void run() {
        double tps;
        long startTime = System.nanoTime();
        long timeSpent = (startTime - this.lastPoll) / 1000L;
        if (timeSpent == 0L) timeSpent = 1L;
        if (this.history.size() > 10) this.history.remove();
        if ((tps = 5.0E7 / (double) timeSpent) <= 21.0) this.history.add(tps);
        this.lastPoll = startTime;
    }


    public double getAverageTPS() {
        double avg = 0.0;
        for (Double f : this.history) {
            if (f == null) continue;
            avg += f;
        }
        return avg / (double) this.history.size();
    }
}
