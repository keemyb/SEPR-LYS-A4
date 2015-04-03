package util;

/**
 * Created by arushiAneja on 03/04/15.
 */
public class Timer {
    private final long nanosPerMilli = 1000000;
    private long startTime = 0;
    private long stopTime = 0;
    private boolean running = false;

    // Start measuring
    public void start() {
        this.startTime = System.nanoTime();
        this.running = true;
    }

    // Stop measuring
    public void stop() {
        this.stopTime = System.nanoTime();
        this.running = false;
    }

    // Reset
    public void reset() {
        this.startTime = 0;
        this.stopTime = 0;
        this.running = false;
    }

    // Get elapsed milliseconds
    public long getElapsedMilliseconds() {
        long elapsed;
        if (running) {
            elapsed = (System.nanoTime() - startTime);
        } else {
            elapsed = (stopTime - startTime);
        }
        return elapsed / nanosPerMilli;
    }
}
