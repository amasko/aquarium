package com.example.Aquarium;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

import java.text.DecimalFormat;

public class MainThread extends Thread {
    private static final String TAG = MainThread.class.getSimpleName();
    private static final int MAX_FPS = 50;
    private static final int MAX_FRAME_SKIPS = 4;
    private static final int FRAME_PERIOD = 1000 / MAX_FPS;

    /**
     *
     */
    // Stuff for stats */
    private DecimalFormat df = new DecimalFormat("0.##");  // 2 dp
            // we'll be reading the stats every second
    private final static int    STAT_INTERVAL = 1000; //ms
            // the average will be calculated by storing
            // the last n FPSs
    private final static int    FPS_HISTORY_NR = 10;
            // last time the status was stored
    private long lastStatusStore = 0;
            // the status time counter
    private long statusIntervalTimer    = 0l;
            // number of frames skipped since the game started
    private long totalFramesSkipped         = 0l;
            // number of frames skipped in a store cycle (1 sec)
    private long framesSkippedPerStatCycle  = 0l;
            // number of rendered frames in an interval
    private int frameCountPerStatCycle = 0;
    private long totalFrameCount = 0l;
            // the last FPS values
    private double  fpsStore[];
            // the number of times the stat has been read
    private long    statsCount = 0;

    // Surface holder that can access the physical surface
    private final SurfaceHolder surfaceHolder;
    // The actual view that handles inputs
    // and draws to the surface
    private MainView view;
    // flag to hold game state
    private boolean running;
    public void setRunning(boolean running) {
        this.running = running;
    }

    public MainThread(SurfaceHolder surfaceHolder, MainView view) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.view = view;
    }

    @Override
    public void run() {
        Canvas canvas;
        long beginTime;
        long timeDiff;
        int sleepTime;
        int framesSkipped;
        initTimingElements();

        while (running) {
            canvas = null;
            // try locking the canvas for exclusive pixel editing
            // in the surface

            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    beginTime = System.currentTimeMillis();
                    framesSkipped = 0;
                    // update game state
                    this.view.update();
                    this.view.checkCollisions();
                    // render state to the screen
                    // draws the canvas on the panel
                    if (canvas != null) {
                        this.view.render(canvas);
                    }
                    timeDiff = System.currentTimeMillis() - beginTime;
                    sleepTime = (int) (FRAME_PERIOD - timeDiff);
                    //Log.w(TAG, "SleepTime: " + sleepTime);
                    if (sleepTime > 0) {
                        Thread.sleep(sleepTime);
                    }
                    while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
                        this.view.update();
                        if (sleepTime < -20) {
                            Log.e(TAG, "execution time difference: " + sleepTime);
                        }
                        //is it right?
                        this.view.checkCollisions();
                        sleepTime += FRAME_PERIOD;
                        framesSkipped++;
                    }
                    framesSkippedPerStatCycle += framesSkipped;
                    storeStats();

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.w(TAG, "Interrupted while sleeping");
            } finally {
                // in case of an exception the surface is not left in
                // an inconsistent state
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    private void storeStats() {
        frameCountPerStatCycle++;
        totalFrameCount++;
        // assuming that the sleep works each call to storeStats
        // happens at 1000/FPS so we just add it up
		statusIntervalTimer += FRAME_PERIOD;
        // check the actual time
        statusIntervalTimer += (System.currentTimeMillis() - statusIntervalTimer);
        if (statusIntervalTimer >= lastStatusStore + STAT_INTERVAL) {
            // calculate the actual frames pers status check interval
            double actualFps = (double)(frameCountPerStatCycle / (STAT_INTERVAL / 1000));
            //stores the latest fps in the array
            fpsStore[(int) statsCount % FPS_HISTORY_NR] = actualFps;
            // increase the number of times statistics was calculated
            statsCount++;
            double totalFps = 0.0;
            // sum up the stored fps values
            for (int i = 0; i < FPS_HISTORY_NR; i++) {
                totalFps += fpsStore[i];
            }
            // obtain the average
            double averageFps = 0.0;
            if (statsCount < FPS_HISTORY_NR) {
                // in case of the first 10 triggers
                averageFps = totalFps / statsCount;
            } else {
                averageFps = totalFps / FPS_HISTORY_NR;            }
            // saving the number of total frames skipped
            totalFramesSkipped += framesSkippedPerStatCycle;
            // resetting the counters after a status record (1 sec)
            framesSkippedPerStatCycle = 0;
            statusIntervalTimer = 0;
            frameCountPerStatCycle = 0;
            statusIntervalTimer = System.currentTimeMillis();
            lastStatusStore = statusIntervalTimer;
//			Log.d(TAG, "Average FPS:" + df.format(averageFps));
            view.setAvgFps("FPS: " + df.format(averageFps));
        }
    }

    private void initTimingElements() {
        // initialise timing elements
        fpsStore = new double[FPS_HISTORY_NR];
        for (int i = 0; i < FPS_HISTORY_NR; i++) {
            fpsStore[i] = 0.0;
        }
        Log.d(TAG + ".initTimingElements()", "Timing elements for stats initialised");
    }

}
