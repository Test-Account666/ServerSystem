package me.entity303.serversystem.utils;

import java.util.LinkedList;

public class ServerSystemTimer implements Runnable {
    private final LinkedList<Double> _history = new LinkedList<>();
    private transient long _lastPoll = System.nanoTime();

    public ServerSystemTimer() {
        this._history.add(20.0);
    }

    @Override
    public void run() {
        double tps;
        var startTime = System.nanoTime();
        var timeSpent = (startTime - this._lastPoll) / 1000L;
        if (timeSpent == 0L)
            timeSpent = 1L;
        if (this._history.size() > 10)
            this._history.remove();
        if ((tps = 5.0E7 / timeSpent) <= 21.0)
            this._history.add(tps);
        this._lastPoll = startTime;
    }


    public double GetAverageTPS() {
        var avg = 0.0;
        for (var tps : this._history) {
            if (tps == null)
                continue;
            avg += tps;
        }
        return avg / this._history.size();
    }
}
